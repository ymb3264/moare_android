package kr.moare.android.viewmodel.profile

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil.request.ImageRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import id.zelory.compressor.Compressor
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.client.call.enqueue
import io.getstream.chat.android.client.models.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.job
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kr.moare.android.entities.*
import kr.moare.android.network.JoinAPI
import kr.moare.android.network.PostAPI
import kr.moare.android.network.ProfileAPI
import kr.moare.android.utils.*
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.regex.Pattern
import javax.inject.Inject

@HiltViewModel
class MyProfileViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    @UserInfoDataStore private val userInfoDataStore: DataStore<Preferences>,
    @UserIdUsernameDataStore private val userIdUsernameDataStore: DataStore<Preferences>,
    @LocationDataStore private val locationDataStore: DataStore<Preferences>,
    savedStateHandle: SavedStateHandle,
    private val encryptedSharedPreferences: SharedPreferences,
//    private val chatClient: ChatClient
) : ViewModel() {
    private val profileAPI = ProfileAPI()
    private val postAPI = PostAPI()

    val token = encryptedSharedPreferences.getString("AccessToken", "") ?: ""

    val chatClient = ChatClient.instance()

    val usernameFlow = userIdUsernameDataStore.data.map {
        it[PreferencesKey.USERNAME] ?: ""
    }
    private val userIDFlow = userIdUsernameDataStore.data.map {
        it[PreferencesKey.USERID] ?: ""
    }
    val accountsFlow = userInfoDataStore.data.map {
        it[PreferencesKey.ACCOUNTS] ?: setOf()
    }
    val profileFlow = userInfoDataStore.data.map {
        it[PreferencesKey.PROFILE] ?: ""
    }

    var myProfile = MutableStateFlow<Profile>(
        Profile(createdAt = "", username = "", sportHashtag = listOf(), name = "", profileImage = "", content = "", place = "", isTeam = false)
    )
    var newTeamProfile = CreateTeamProfile(
        "", "", null, "", "", "", "", null)
    var updatedUserProfile = MutableStateFlow<UpdateProfile>(
        UpdateProfile("", "", null, "", "", "",
            "")
    )

    var accounts = MutableStateFlow<MutableList<Profile>>(mutableStateListOf())

    var postsList = MutableStateFlow<MutableList<MutableList<Post>>>(mutableStateListOf())
    var postsData = mutableListOf<Post>()
    var postNum = 6

    val teamCompleteBtnEnabled = MutableStateFlow(false)
    val updateCompleteBtnEnabled = MutableStateFlow(true)

    val showErrorText1 = MutableStateFlow(false)
    val showErrorText2 = MutableStateFlow(false)

    val loading = MutableStateFlow(false)
    val usernameLoading = MutableStateFlow(false)
    val postLoading = MutableStateFlow(false)

    // updatePost
    val content = MutableStateFlow("")
    var postToUpdate = MutableStateFlow(Post("", "", "", "", "", "", listOf(), "", listOf(), "", "", ""))
    var updatedPost = MutableStateFlow(UpdatePost("", "", "", listOf(), "", "", ""))
    var postToUpdateListIndex = 0
    var postToUpdatePostIndex = 0

    var userID = ""
    var username = ""

    init {
        viewModelScope.launch {
            profileFlow.collect {
                if (it.isNotEmpty()) {
                    val profile = Json.decodeFromString<Profile>(it)
                    myProfile.value = profile
                }
            }
        }
        viewModelScope.launch {
            usernameFlow.collect {
                getMyProfile(it)
                coroutineContext.job.cancel()
            }
        }
        viewModelScope.launch {
            usernameFlow.collect { usernameFlow ->
                if (usernameFlow.isNotEmpty()) {
                    username = usernameFlow
                    userIDFlow.collect { userIDFlow ->
                        if (userIDFlow.isNotEmpty()) {
                            userID = userIDFlow
                            getUserPosts(usernameFlow)
                        }
                        coroutineContext.job.cancel()
                    }
                }
//                coroutineContext.job.cancel()
            }
        }
    }

    private suspend fun getMyProfile(username: String) {
        kotlin.runCatching {
            profileAPI.getMyProfile(token, username)
        }.onSuccess { profile ->
            // 다른 변수에 it을 담아도 공유가된다
            userInfoDataStore.edit {
                it[PreferencesKey.PROFILE] = Json.encodeToString(profile)
            }
            setUpdatedUserProfile(profile)

            // accounts가져오고 accounts이용해서 현재 profile에 chatId설정하고 chat연결
            getMyAccounts()

            Log.d("success", "$profile")
        }.onFailure {
            Log.d("FAIL", "message: $it")
        }
    }

    private suspend fun getUserPosts(username: String) {
        kotlin.runCatching {
            postLoading.value = true
            postNum = 6
            postsList.value.clear()
            profileAPI.getUserPosts(userID, username)
        }.onSuccess {
            if (it.isEmpty()) {
                postLoading.value = false
                return
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
                profileAPI.getMoreUserPosts(userID, username, lastPost.postCreatedAt)
            }.onSuccess {
                postsData = (postsData + it).toMutableList()
                loadMorePost()
                Log.d("getMorePost", "$it")
            }.onFailure {
                Log.d("getMorePost", "message: $it")
            }
        }
    }

    fun createTeamProfile(profileImage: Uri?) {
        loading.value = true
        viewModelScope.launch {
            kotlin.runCatching {
                val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.KOREA)
                newTeamProfile.createdAt = formatter.format(Date())

                val imageFile = profileImage?.let { UriUtil.toFile(context, it) }
                val compressedFile = imageFile?.let { Compressor.compress(context, it) }

                val followObj = FollowObj(
                    userID = myProfile.value.userID!!,
                    createdAt = myProfile.value.createdAt,
                    profileImage = myProfile.value.profileImage,
                    username = myProfile.value.username,
                )
                newTeamProfile.follow = followObj

                profileAPI.createTeamProfile(token, newTeamProfile, compressedFile)
            }.onSuccess { profile ->
                Log.d("Sdfsdf", profile.toString())
                // teamProfile로 바뀌기 전 createdAt
                val hostCreatedAt = myProfile.value.createdAt.replace(":", "")

                // post초기화
                postsList.value = mutableListOf()
                postNum = 6
                postsData = mutableListOf()

                // profile 정보 업데이트
                userIdUsernameDataStore.edit {
                    it[PreferencesKey.USERNAME] = profile.username
                }
                userInfoDataStore.edit {
                    it[PreferencesKey.PROFILE] = Json.encodeToString(profile)
                }
                setUpdatedUserProfile(profile)

                // get accounts and set chat
                getMyAccounts(
                    createTeamChannel = true,
                    hostChatId = myProfile.value.userID!!.replace("[@.]".toRegex(), "") + "_${hostCreatedAt}"
                )

                // teamProfileCreateView 초기화
                newTeamProfile = CreateTeamProfile(
                    "", "", null, "", "", "", "", null)
                loading.value = false
                teamCompleteBtnEnabled.value = false

                Log.d("success", "$profile")
            }.onFailure {
                loading.value = false
                Log.d("FAIL", "message: $it")
            }
        }
    }

    fun updateProfile(profileImage: Uri?, close: () -> Unit) {
        loading.value = true
        if (!checkUpdateContent(profileImage)) {
            viewModelScope.launch {
                kotlin.runCatching {
                    val imageFile = profileImage?.let {
                        // 기본이미지로 변경 후 다시 새로운 이미지 넣었을때
                        updatedUserProfile.value.shouldUpdateDefaultImage = false

                        UriUtil.toFile(context, it)
                    }
                    val compressedFile = imageFile?.let { Compressor.compress(context, it) }

                    val requestUpdateProfile = RequestUpdateProfile(updatedUserProfile.value, myProfile.value)
                    profileAPI.updateProfile(token, requestUpdateProfile, compressedFile)
                }.onSuccess { profile ->
                    // profile이 team일때 team 채팅방 name, profileImage update
                    // update된 chatID가없는 profile로 edit하기 전에 먼저 channel update
                    if (profile.isTeam) {
                        updateTeamChannel(profile.name, profile.profileImage)
                    }

                    userIdUsernameDataStore.edit {
                        it[PreferencesKey.USERNAME] = profile.username
                    }
                    userInfoDataStore.edit {
                        it[PreferencesKey.PROFILE] = Json.encodeToString(profile)
                    }
                    setUpdatedUserProfile(profile)

                    // main post refresh and my post refresh
                    getUserPosts(profile.username)

                    // get accounts and set chat
                    getMyAccounts()

                    close()
                    loading.value = false
                    updateCompleteBtnEnabled.value = true
                    Log.d("success", "$profile")
                }.onFailure {
                    // 실패했을때 alert보여주기
                    loading.value = false
                    Log.d("FAIL", "message: $it")
                }
            }
        }
    }

    private fun getMyAccounts(createTeamChannel: Boolean = false, hostChatId: String = "") {
        viewModelScope.launch {
            kotlin.runCatching {
                profileAPI.getMyAccounts(token)
            }.onSuccess { response ->
                setChatIdAndConnectChat(createTeamChannel, hostChatId)

                accounts.value = response.toMutableList()
                val encodedAccounts = response.map {
                    Json.encodeToString(it)
                }
                userInfoDataStore.edit {
                    it[PreferencesKey.ACCOUNTS] = encodedAccounts.toSet()
                }
                Log.d("myAccounts", "$response")
            }.onFailure {
                Log.d("FAIL", "message: $it")
            }
        }
    }

    fun collectAccounts() {
        viewModelScope.launch {
            accountsFlow.collect {
                if (it.isNotEmpty()) {
                    accounts.value.clear()
                    for (account in it) {
                        val decoded = Json.decodeFromString<Profile>(account)
                        accounts.value.add(decoded)
                    }
                }
                coroutineContext.job.cancel()
            }
        }
    }

    fun checkUsername(username: String) {
        val usernameRegex = "^[A-Za-z_.[0-9]]{1,30}$"
        showErrorText1.value = !Pattern.matches(usernameRegex, username)

        checkCompleteBtn(false)
        if (!showErrorText1.value && username != myProfile.value.username) {
            checkUsername2(username, false)
        }
    }

    fun checkTeamUsername(username: String) {
        val usernameRegex = "^[A-Za-z_.[0-9]]{1,30}$"
        if (username.isNotEmpty()) {
            showErrorText1.value = !Pattern.matches(usernameRegex, username)
        }

        checkCompleteBtn(true)
        if (!showErrorText1.value) {
            checkUsername2(username, true)
        }
    }

    fun checkUsername2(username: String, isTeam: Boolean) {
        val joinAPI = JoinAPI()

        usernameLoading.value = true
        viewModelScope.launch {
            kotlin.runCatching {
                joinAPI.checkUsername(username)
            }.onSuccess {
                usernameLoading.value = false
                showErrorText2.value = it.message != "available"
                checkCompleteBtn(isTeam)
                Log.d("username2", "$it")
            }.onFailure {
                usernameLoading.value = false
                showErrorText2.value = true
                checkCompleteBtn(isTeam)
                Log.d("username2", "message: $it")
            }
        }
    }

    fun changeProfile(username: String) {
        viewModelScope.launch {
            userIdUsernameDataStore.edit {
                it[PreferencesKey.USERNAME] = username
            }
            getMyProfile(username)
            getUserPosts(username)
        }
    }

    private fun makeImageRequest(posts: MutableList<Post>): MutableList<Post> {
        var newPosts = mutableListOf<Post>()
        for (post in posts) {
            var imageList = mutableListOf<ImageRequest>()
            for (obj in post.mediaObj) {
                if (obj.type == "image") {
                    val request = ImageRequest.Builder(context)
                        .data(obj.url)
                        .crossfade(true)
                        .build()
                    imageList.add(request)
                }
            }
            post.imageRequest = imageList
            newPosts.add(post)
        }
        return newPosts
    }

    fun checkCompleteBtn(isTeam: Boolean) {
        if (isTeam) {
            teamCompleteBtnEnabled.value = (!showErrorText1.value && !showErrorText2.value) && newTeamProfile.username.isNotEmpty() && newTeamProfile.name.isNotEmpty()
        } else {
            updateCompleteBtnEnabled.value = !showErrorText1.value && !showErrorText2.value
        }
    }

    fun resetTeamProfile() {
        newTeamProfile = CreateTeamProfile(
            "", "", null, "", "", "", "", null)
        showErrorText1.value = false
        showErrorText2.value = false
        teamCompleteBtnEnabled.value = false
    }

    fun resetUpdateProfile() {
        setUpdatedUserProfile(myProfile.value)
        showErrorText1.value = false
        showErrorText2.value = false
        updateCompleteBtnEnabled.value = true
    }

    fun checkTeamContent(image: Uri?): Boolean {
        val profile = CreateTeamProfile(
            "", "", null, "", "", "", "", null)
        return image == null && profile == newTeamProfile
    }

    fun checkUpdateContent(image: Uri?): Boolean {
        var profile = UpdateProfile("", "", null, "", "", "", "")

        profile.createdAt = myProfile.value.createdAt
        profile.username = myProfile.value.username
        profile.sportHashtag = myProfile.value.sportHashtag
        profile.name = myProfile.value.name
        profile.profileImage = myProfile.value.profileImage
        profile.content = myProfile.value.content
        profile.place = myProfile.value.place

        return image == null && profile == updatedUserProfile.value
    }

    fun setUpdatedUserProfile(profile: Profile) {
        updatedUserProfile.value.createdAt = profile.createdAt
        updatedUserProfile.value.username = profile.username
        updatedUserProfile.value.sportHashtag = profile.sportHashtag
        updatedUserProfile.value.name = profile.name
        updatedUserProfile.value.profileImage = profile.profileImage
        updatedUserProfile.value.content = profile.content
        updatedUserProfile.value.place = profile.place
    }

    fun like(post: Post, username: String) {
        viewModelScope.launch {
            kotlin.runCatching {
                val like = LikeObj("", "", myProfile.value.username, post.userID, post.postCreatedAt)
                postAPI.like(like)
            }.onSuccess {
                postsList.value[postToUpdateListIndex][postToUpdatePostIndex].like = it
                Log.d("like", "$it")
            }.onFailure {
                Log.d("like", "message: $it")
            }
        }
    }

    fun unlike(post: Post, username: String) {
        viewModelScope.launch {
            kotlin.runCatching {
                val like = LikeObj("", "", myProfile.value.username, post.userID, post.postCreatedAt)
                postAPI.unlike(like)
            }.onSuccess {
                postsList.value[postToUpdateListIndex][postToUpdatePostIndex].like = it
                Log.d("unlike", "$it")
            }.onFailure {
                Log.d("unlike", "message: $it")
            }
        }
    }

    fun logout(cb: () -> Unit = {}) {
        viewModelScope.launch {
            encryptedSharedPreferences.edit().putString("AccessToken", "").apply()
            encryptedSharedPreferences.edit().putString("RefreshToken", "").apply()
            cb()
        }
    }

    fun deleteProfile(cb: () -> Unit) {
        loading.value = true
        viewModelScope.launch {
            kotlin.runCatching {
                profileAPI.deleteProfile(token, myProfile.value)
            }.onSuccess {
                if (myProfile.value.isTeam) {
                    if (accounts.value.isNotEmpty()) {
                        changeProfile(accounts.value.first().username)
                        cb()
                    }
                } else {
                    logout()
                    userInfoDataStore.edit {
                        it[PreferencesKey.PROFILE] = ""
                        it[PreferencesKey.ACCOUNTS] = setOf()
                    }
                    locationDataStore.edit {
                        it[PreferencesKey.CURRENTLOCATION] =
                            ""
                        it[PreferencesKey.LOCATIONLIST] = setOf()
                    }
                    userIdUsernameDataStore.edit {
                        it[PreferencesKey.USERID] = ""
                        it[PreferencesKey.USERNAME] = ""
                    }
                }

                loading.value = false
                Log.d("deleteProfile", "$it")
            }.onFailure {
                loading.value = false
                Log.d("deleteProfile", "$it")
            }
        }
    }

    fun updatePost(cb: () -> Unit) {
        loading.value = true
        viewModelScope.launch {
            kotlin.runCatching {
                postAPI.updatePost(token, updatedPost.value)
            }.onSuccess {
                postsList.value[postToUpdateListIndex][postToUpdatePostIndex] = it
                cb()
                loading.value = false
                Log.d("updatePost", "$it")
            }.onFailure {
                loading.value = false
                Log.d("updatePost", "$it")
            }
        }
    }

    fun checkUpdatedPostContent(): Boolean {
        return (updatedPost.value.sportHashtag == postToUpdate.value.sportHashtag &&
                updatedPost.value.place == postToUpdate.value.place &&
                updatedPost.value.content == postToUpdate.value.content)
    }

    fun deletePost(post: Post, listIndex: Int, postIndex: Int, cb: () -> Unit) {
        loading.value = true
        viewModelScope.launch {
            kotlin.runCatching {
                postAPI.deletePost(token, post)
            }.onSuccess {
                postsList.value[listIndex].removeAt(postIndex)

                loading.value = false
                cb()
                Log.d("deletePost", "$it")
            }.onFailure {
                loading.value = false
                cb()
                Log.d("deletePost", "$it")
            }
        }
    }

    // chat
    private suspend fun setChatIdAndConnectChat(createTeamChannel: Boolean = false, hostChatId: String = "") {
        val chatId = myProfile.value.userID!!.replace("[@.]".toRegex(), "") +
                "_${myProfile.value.createdAt.replace(":", "")}"
        myProfile.value.chatID = chatId

        viewModelScope.launch {
            userInfoDataStore.edit {
                it[PreferencesKey.PROFILE] = Json.encodeToString(myProfile.value)
            }
        }

        if (chatClient.getCurrentUser() != null) {
            chatClient.disconnect(false).enqueue {
                val user = User(
                    id = chatId,
                    name = myProfile.value.username,
                    image = myProfile.value.profileImage
                )
                myProfile.value.chatToken?.let { token ->
                    chatClient.connectUser(user, token).enqueue { result ->
                        if (result.isSuccess) {
                            if (createTeamChannel) {
                                createTeamChannel(chatId, hostChatId)
                            }
                        } else {
                            Log.d("chat connect", "${result.error()}")
                            return@enqueue
                        }
                    }
                }
            }
        } else {
            val user = User(
                id = chatId,
                name = myProfile.value.username,
                image = myProfile.value.profileImage
            )
            myProfile.value.chatToken?.let { token ->
                chatClient.connectUser(user, token).enqueue { result ->
                    if (result.isSuccess) {
                        if (createTeamChannel) {
                            createTeamChannel(chatId, hostChatId)
                        }
                    } else {
                        Log.d("chat connect", "${result.error()}")
                        return@enqueue
                    }
                }
            }
        }
    }

    private fun createTeamChannel(channelId: String, hostChatId: String) {
        chatClient.createChannel(
            channelType = "messaging",
            channelId = channelId,
            memberIds = listOf(channelId, hostChatId),
            extraData = mapOf(
                "name" to myProfile.value.name,
                "image" to myProfile.value.profileImage
            )
        ).enqueue()
    }

    private fun updateTeamChannel(name: String, profileImage: String) {
        chatClient.updateChannel(
            channelType = "messaging",
            channelId = myProfile.value.chatID!!,
            updateMessage = null,
            channelExtraData = mapOf(
                "name" to name,
                "image" to profileImage
            )
        ).enqueue()
    }
}