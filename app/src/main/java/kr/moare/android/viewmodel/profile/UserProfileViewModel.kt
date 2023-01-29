package kr.moare.android.viewmodel.profile

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil.request.ImageRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.client.api.models.QueryChannelsRequest
import io.getstream.chat.android.client.models.Filters
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.job
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kr.moare.android.entities.*
import kr.moare.android.network.FollowAPI
import kr.moare.android.network.ProfileAPI
import kr.moare.android.utils.PreferencesKey
import kr.moare.android.utils.UserIdUsernameDataStore
import kr.moare.android.utils.UserInfoDataStore
import javax.inject.Inject

@HiltViewModel
class UserProfileViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    @UserInfoDataStore private val userInfoDataStore: DataStore<Preferences>,
    @UserIdUsernameDataStore private val userIdUsernameDataStore: DataStore<Preferences>,
    private val encryptedSharedPreferences: SharedPreferences,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val profileAPI = ProfileAPI()
    private val followAPI = FollowAPI()

    val chatClient = ChatClient.instance()

    private val username: String = savedStateHandle["username"] ?: ""

    val token = encryptedSharedPreferences.getString("AccessToken", "") ?: ""

    val usernameFlow = userIdUsernameDataStore.data.map {
        it[PreferencesKey.USERNAME] ?: ""
    }
    val accountsFlow = userInfoDataStore.data.map {
        it[PreferencesKey.ACCOUNTS] ?: setOf()
    }
    val profileFlow = userInfoDataStore.data.map {
        it[PreferencesKey.PROFILE] ?: ""
    }

    var myUsername = ""

    val userProfile = MutableStateFlow(
        Profile("", "", "", "", listOf(), "", "", "", "", false)
    )
    val myProfile = MutableStateFlow(
        Profile("", "", "", "", listOf(), "", "", "", "", false)
    )
    // view에서 바로 for문으로 구현하기엔 로직 복잡하다
    val followButtonEnabled = MutableStateFlow(true)

    val postsList = MutableStateFlow<MutableList<MutableList<Post>>>(mutableStateListOf())
    var postsData = mutableListOf<Post>()
    var postNum = 6

    val postLoading = MutableStateFlow(false)
    val followLoading = MutableStateFlow(false)

    // unfollow 확인 알람
    val alertTitle = MutableStateFlow("")
    val alertMessage = MutableStateFlow("")

    init {
        getUserProfile()
        viewModelScope.launch {
            usernameFlow.collect {
                myUsername = it
            }
        }
        viewModelScope.launch {
            profileFlow.collect {
                if (it.isNotEmpty()) {
                    val profile = Json.decodeFromString<Profile>(it)
                    myProfile.value = profile
                }
            }
        }
    }

    private fun getUserProfile() {
        viewModelScope.launch {
            kotlin.runCatching {
                profileAPI.getUserProfile(username)
            }.onSuccess {
                userProfile.value = it
                userProfile.value.chatID = userProfile.value.userID!!.replace("[@.]".toRegex(), "") + "_${userProfile.value.createdAt.replace(":", "")}"

                getUserPosts()

                for (obj in userProfile.value.follower) {
                    if (obj.username == myUsername) {
                        followButtonEnabled.value = false
                        break
                    }
                }
                Log.d("getUserProfile", "$it")
            }.onFailure {
                Log.d("getUserProfile", "message: $it")
            }
        }
    }

    private fun getUserPosts() {
        viewModelScope.launch {
            kotlin.runCatching {
                postLoading.value = true
                postNum = 6
                postsList.value.clear()
                profileAPI.getUserPosts(userProfile.value.userID!!, userProfile.value.username)
            }.onSuccess {
                if (it.isEmpty()) {
                    postLoading.value = false
                    return@launch
                }

                postsData = it.toMutableList()
                val num = if (postsData.count() <= 6) {
                    postsData.count()
                } else {
                    postNum
                }

                postLoading.value = false
                postsList.value.add(postsData.take(num).toMutableList())
                Log.d("getUserPosts", "$it")
            }.onFailure {
                postLoading.value = false
                Log.d("getUserPosts", "message: $it")
            }
        }
    }

    fun loadMorePost() {
        var newPosts = mutableListOf<Post>()
        val count = postsData.count()

        if (count % 60 == 0 && postNum == count) {
            getMorePost()
        } else {
            if (postNum < count && count < postNum+6) {
                newPosts = postsData.subList(postNum, count)
            } else if (count >= postNum+6) {
                newPosts = postsData.subList(postNum, postNum+6)
            } else {
                return
            }

            postsList.value.add(newPosts)
            postNum += 6
        }
    }

    fun getMorePost() {
        viewModelScope.launch {
            kotlin.runCatching {
                val lastPost = postsData.last()
                profileAPI.getMoreUserPosts(
                    userProfile.value.userID!!,
                    userProfile.value.username,
                    lastPost.postCreatedAt)
            }.onSuccess {
                postsData = (postsData + it).toMutableList()
                loadMorePost()
                Log.d("getMorePost", "$it")
            }.onFailure {
                Log.d("getMorePost", "message: $it")
            }
        }
    }

    fun follow() {
        followLoading.value = true
        viewModelScope.launch {
                kotlin.runCatching {
                    val targetObj = UserFollowObj(
                        userProfile.value.userID!!,
                        userProfile.value.createdAt,
                        userProfile.value.profileImage,
                        userProfile.value.username
                    )
                    val userObj = UserFollowObj(
                        myProfile.value.userID!!,
                        myProfile.value.createdAt,
                        myProfile.value.profileImage,
                        myProfile.value.username
                    )
                    val followObj = RequestFollowObj(userObj, targetObj, myProfile.value.isTeam, userProfile.value.isTeam)

                    followAPI.follow(token, followObj)
                }.onSuccess { response ->
                    // targetProfile update
                    userProfile.value.follower = response.targetFollower
                    response.targetTeamOrMember?.let { teamOrMember ->
                        userProfile.value.teamOrMember = teamOrMember
                    }

                    // myProfile update(dataStore), accounts profile update
                    accountsFlow.collect { accounts ->
                        if (accounts.isNotEmpty()) {
                            for ((index, account) in accounts.withIndex()) {
                                if (Json.decodeFromString<Profile>(account).username == myProfile.value.username) {
                                    val newAccounts = accounts.toMutableList()
                                    newAccounts.removeAt(index)

                                    // view에서 collect한 dataStore flow변수는 변수에 직접 값을 할당해도 dataStore에 바로 할당이된다 -> 확인필요
                                    // 근데 decode를 한 상태에서 다시 endcode를 안했는데도 반영이된다..?
                                    myProfile.value.following = response.following
                                    response.teamOrMember?.let { teamOrMember ->
                                        myProfile.value.teamOrMember = teamOrMember
                                    }

                                    newAccounts.add(index, Json.encodeToString(myProfile.value))
                                    userInfoDataStore.edit {
                                        it[PreferencesKey.PROFILE] = Json.encodeToString(myProfile.value)
                                        it[PreferencesKey.ACCOUNTS] = newAccounts.toSet()
                                    } // 실행후 follow함수의 매개변수 myProfile에 바로 반영이된다
                                    break
                                }
                            }
                        }

                        // collect 밖은 실행안됨
                        // join chat
                        response.teamOrMember?.let {
                            joinTeamChannel(myProfile.value.isTeam)
                        }

                        followLoading.value = false
                        followButtonEnabled.value = false
                        Log.d("follow", "$response")

                        coroutineContext.job.cancel()
                    }
                }.onFailure {
                    followLoading.value = false
                    Log.d("follow", "message: $it")
                }
        } // launch
    }

    fun unfollow() {
        followLoading.value = true
        viewModelScope.launch {
            kotlin.runCatching {
                val targetObj = UserFollowObj(
                    userProfile.value.userID!!,
                    userProfile.value.createdAt,
                    userProfile.value.profileImage,
                    userProfile.value.username
                )
                val userObj = UserFollowObj(
                    myProfile.value.userID!!,
                    myProfile.value.createdAt,
                    myProfile.value.profileImage,
                    myProfile.value.username
                )
                val followObj = RequestFollowObj(userObj, targetObj, myProfile.value.isTeam, userProfile.value.isTeam)

                followAPI.unfollow(token, followObj)
            }.onSuccess { response ->
                // targetProfile update
                userProfile.value.follower = response.targetFollower
                response.targetTeamOrMember?.let { teamOrMember ->
                    userProfile.value.teamOrMember = teamOrMember
                }

                // leave chat
                // myProfile을 update하면 teamOrMember에 username이 없어지기때문에 update하기전에 leaveTeamChannel을 해준다
                for (obj in myProfile.value.teamOrMember) {
                    if (obj.username == userProfile.value.username) {
                        leaveTeamChannel(myProfile.value.isTeam)
                        break
                    }
                }

                // myProfile update(dataStore)
                accountsFlow.collect { accounts ->
                    if (accounts.isNotEmpty()) {
                        for ((index, account) in accounts.withIndex()) {
                            if (Json.decodeFromString<Profile>(account).username == myProfile.value.username) {
                                val newAccounts = accounts.toMutableList()
                                newAccounts.removeAt(index)

                                myProfile.value.following = response.following
                                response.teamOrMember?.let { teamOrMember ->
                                    myProfile.value.teamOrMember = teamOrMember
                                }

                                newAccounts.add(index, Json.encodeToString(myProfile.value))
                                userInfoDataStore.edit {
                                    it[PreferencesKey.PROFILE] = Json.encodeToString(myProfile.value)
                                    it[PreferencesKey.ACCOUNTS] = newAccounts.toSet()
                                }
                                break
                            }
                        }
                    }

                    // collect 밖은 실행안됨
                    followLoading.value = false
                    followButtonEnabled.value = true
                    Log.d("follow", "$response")

                    coroutineContext.job.cancel()
                }
            }.onFailure {
                followLoading.value = false
                Log.d("follow", "message: $it")
            }
        }
    }

    fun checkUnfollow(cb: () -> Unit) {
        for (obj in myProfile.value.teamOrMember) {
            if (obj.username == userProfile.value.username) {
                cb()
                if (myProfile.value.isTeam) {
                    alertTitle.value = "${userProfile.value.username}(멤버) 언팔로우"
                    alertMessage.value = "팀에 속해있는 멤버를 언팔로우시 해당 멤버는 팀에서 제외되며 채팅방에서도 제외됩니다."
                    return
                } else {
                    alertTitle.value = "${userProfile.value.username}(팀) 언팔로우"
                    alertMessage.value = "멤버로 속해있는 팀을 언팔로우시 해당 팀 멤버에서 제외되며 팀 채팅방에서도 나가집니다."
                    return
                }
            }
        }
        // teamOrMember에 없을 시 바로 언팔로우
        unfollow()
    }

    fun reportUser(cb: () -> Unit) {
        viewModelScope.launch {
            kotlin.runCatching {
                val obj = mapOf<String, String>(
                    "userID" to userProfile.value.userID!!,
                    "createdAt" to userProfile.value.createdAt,
                    "userCreatedAt" to myProfile.value.createdAt
                )
                profileAPI.reportUser(token, obj)
            }.onSuccess { response ->
                if (response.message == "report success") {
                    cb()
                }
                Log.d("reportUser", "$response")
            }.onFailure {
                Log.d("reportUser", "message: $it")
            }
        }
    }

    fun blockUser(cb: () -> Unit) {
        viewModelScope.launch {
            profileFlow.collect { profile ->
                kotlin.runCatching {
                    val obj = BlockUserObj(
                        targetUserID = userProfile.value.userID!!,
                        targetCreatedAt = userProfile.value.createdAt,
                        userProfile = Json.decodeFromString(profile)
                    )
                profileAPI.blockUser(token, obj)
                }.onSuccess { response ->
                    userInfoDataStore.edit { dataStore ->
                        dataStore[PreferencesKey.PROFILE] = Json.encodeToString(response)
                    }
                    cb()
                    Log.d("blockUser", "$response")
                }.onFailure {
                    Log.d("blockUser", "message: $it")
                }
                coroutineContext.job.cancel()
            }
        }
    }

    // chat
    // 개인 계정이 자신이 속한 팀의 프로필에서 메세지를 눌렀을때 팀 채팅방으로 이동하는게 아니라, 누른 팀계정과 개인 계정간의 채팅방이 만들어진다.
    // 팀 채팅방으로 가고싶을때는 개인 프로필에서 채팅리스트를 통해 이동할 수 있다.
    fun createChannel(completion: (String) -> Unit) {
        val filter = Filters.eq("members", listOf(userProfile.value.chatID!!, myProfile.value.chatID!!))
        val offset = 0
        val limit = 1
        val request = QueryChannelsRequest(filter, offset, limit)

        chatClient.queryChannels(request).enqueue { result ->
            if (result.isSuccess) {
                val channels = result.data()
                // 존재하는 채널이 없으면 만들고, 만들어진 directMessageChannel을 다시 가져와 cid를 전달한다
                if (channels.isEmpty()) {
                    chatClient.createChannel(
                        channelType = "messaging",
                        channelId = "",
                        memberIds = listOf(userProfile.value.chatID!!, myProfile.value.chatID!!),
                        extraData = emptyMap()
                    ).enqueue { result ->
                        if (result.isSuccess) {
                            val filter = Filters.eq("members", listOf(userProfile.value.chatID!!, myProfile.value.chatID!!))
                            val offset = 0
                            val limit = 1
                            val request = QueryChannelsRequest(filter, offset, limit)

                            chatClient.queryChannels(request).enqueue { result ->
                                if (result.isSuccess) {
                                    val channels = result.data()
                                    if (channels.isNotEmpty()) {
                                        completion(channels.first().cid)
                                    }
                                } else {
                                    Log.d("chat error", "${result.error()}")
                                }
                            }
                        } else {
                            Log.d("chat error", "${result.error()}")
                        }
                    }
                } else {
                    completion(channels.first().cid)
                }
            }
        }
    }

    private fun joinTeamChannel(isTeam: Boolean) {
        if (isTeam) {
            val channelClient = chatClient.channel("messaging", myProfile.value.chatID!!)
            channelClient.addMembers(listOf(userProfile.value.chatID!!)).enqueue()
        } else {
            val channelClient = chatClient.channel("messaging", userProfile.value.chatID!!)
            channelClient.addMembers(listOf(myProfile.value.chatID!!)).enqueue()
        }
    }

    private fun leaveTeamChannel(isTeam: Boolean) {
        if (isTeam) {
            val channelClient = chatClient.channel("messaging", myProfile.value.chatID!!)
            channelClient.removeMembers(listOf(userProfile.value.chatID!!)).enqueue()
        } else {
            val channelClient = chatClient.channel("messaging", userProfile.value.chatID!!)
            channelClient.removeMembers(listOf(myProfile.value.chatID!!)).enqueue()
        }
    }
}

















