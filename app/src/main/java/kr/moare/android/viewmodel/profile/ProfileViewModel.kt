package kr.moare.android.viewmodel.profile

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil.request.ImageRequest
import kr.moare.android.entities.*
import kr.moare.android.network.ProfileAPI
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import id.zelory.compressor.Compressor
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.client.models.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.job
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kr.moare.android.network.JoinAPI
import kr.moare.android.network.PostAPI
import kr.moare.android.utils.PreferencesKey
import kr.moare.android.utils.UriUtil
import kr.moare.android.utils.UserInfoDataStore
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    @UserInfoDataStore private val userInfoDataStore: DataStore<Preferences>,
    savedStateHandle: SavedStateHandle,
    private val encryptedSharedPreferences: SharedPreferences,
    private val chatClient: ChatClient
) : ViewModel() {
    val api = ProfileAPI()

    val token = encryptedSharedPreferences.getString("token", "") ?: ""

    private val usernameFlow = userInfoDataStore.data.map {
        it[PreferencesKey.USERNAME] ?: ""
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
        "", "", null, "", "", "", "", userCreatedAt = "")
    var updatedUserProfile = MutableStateFlow<UpdateProfile>(
        UpdateProfile("", "", null, "", "", "",
            "")
    )
    // checkTeamContent 비교를위해 newTeamProfile.userCreatedAt는 빈값으로두고 creatTeamProfile할때 넣는다
    var userCreatedAt = ""

    var accounts = MutableStateFlow<MutableList<Profile>>(mutableStateListOf())

    var postsList = MutableStateFlow<MutableList<List<Post>>>(mutableStateListOf())
    var postsData = mutableListOf<Post>()
    var postNum = 6

    val teamCompleteBtnEnabled = MutableStateFlow(false)
    val updateCompleteBtnEnabled = MutableStateFlow(true)

    val showErrorText1 = MutableStateFlow(false)
    val showErrorText2 = MutableStateFlow(false)

    val loading = MutableStateFlow(false)
    val usernameLoading = MutableStateFlow(false)
    val postLoading = MutableStateFlow(false)

    init {
        viewModelScope.launch {
            profileFlow.collect {
                if (it.isNotEmpty()) {
                    val profile = Json.decodeFromString<Profile>(it)
                    myProfile.value = profile
                    userCreatedAt = profile.createdAt
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
            usernameFlow.collect {
                getUserPosts(it)
            }
        }
        getMyAccounts()
    }

    private suspend fun getMyProfile(username: String) {
        kotlin.runCatching {
            api.getMyProfile(token, username)
        }.onSuccess { profile ->
            // 다른 변수에 it을 담아도 공유가된다
            userInfoDataStore.edit {
                it[PreferencesKey.PROFILE] = Json.encodeToString(profile)
            }
            userInfoDataStore.edit {
                it[PreferencesKey.USERNAME] = profile.username
            }
            setUpdatedUserProfile(profile)

            // newTeamProfile follow에 username넣기
            newTeamProfile.following = listOf(username)
            newTeamProfile.follower = listOf(username)
            newTeamProfile.teamOrMember = listOf(username)

            // 처음 chat연결
            val user = User(
                id = profile.username,
                name = profile.username,
                image = profile.profileImage
            )
            profile.chatToken?.let { token ->
                chatClient.connectUser(user, token).enqueue()
            }

            Log.d("success", "$profile")
        }.onFailure {
            Log.d("FAIL", "message: $it")
        }
    }

    private suspend fun getUserPosts(username: String) {
        kotlin.runCatching {
            postLoading.value = true
            postsList.value.clear()
            api.getUserPosts(username)
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

            val newPosts = makeImageRequest(postsData.take(num).toMutableList())

            postLoading.value = false
            postsList.value.add(newPosts)
            Log.d("getUserPosts", "$it")
        }.onFailure {
            postLoading.value = false
            Log.d("getUserPosts", "message: $it")
        }
    }

    fun createTeamProfile(profileImage: Uri?) {
        loading.value = true
        viewModelScope.launch {
            kotlin.runCatching {
                val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.KOREA)
                newTeamProfile.createdAt = formatter.format(Date())
                newTeamProfile.userCreatedAt = userCreatedAt

                val imageFile = profileImage?.let { UriUtil.toFile(context, it) }
                val compressedFile = imageFile?.let { Compressor.compress(context, it) }
                api.createTeamProfile(token, newTeamProfile, compressedFile)
            }.onSuccess { profile ->
                // teamProfile로 바뀌기 전 username
                val beforeUsername = myProfile.value.username

                // post초기화
                postsList.value = mutableListOf()
                postNum = 6
                postsData = mutableListOf()

                // profile 정보 업데이트
                userInfoDataStore.edit {
                    it[PreferencesKey.USERNAME] = profile.username
                }
                userInfoDataStore.edit {
                    it[PreferencesKey.PROFILE] = Json.encodeToString(profile)
                }
                setUpdatedUserProfile(profile)

                // 생성된 account추가
                accounts.value.add(profile)
                accountsFlow.collect { accounts ->
                    val newAccounts = accounts.toMutableList()
                    val encodedProfile = Json.encodeToString(profile)
                    newAccounts.add(encodedProfile)
                    userInfoDataStore.edit {
                        it[PreferencesKey.ACCOUNTS] = newAccounts.toSet()
                    }
                    coroutineContext.job.cancel()
                }

                // teamProfileCreateView 초기화
                newTeamProfile = CreateTeamProfile(
                    "", "", null, "", "", "", "", userCreatedAt = "")
                loading.value = false
                teamCompleteBtnEnabled.value = false

                // chatClient update
                val user = User(
                    id = profile.username,
                    name = profile.username,
                    image = profile.profileImage
                )
                profile.chatToken?.let { token ->
                    chatClient.disconnect(true).await()
                    chatClient.connectUser(user, token).enqueue {
                        if (it.isSuccess) {
                            createChannel(profile.username)
                        }
                    }
                }

                Log.d("success", "$profile")
            }.onFailure {
                loading.value = false
                Log.d("FAIL", "message: $it")
            }
        }
    }

    private fun createChannel(username: String) {
        chatClient.createChannel(
            channelType = "messaging",
            channelId = username,
            memberIds = listOf(username, myProfile.value.username),
            extraData = mapOf("name" to "test")
        ).enqueue()
    }

    fun updateProfile(profileImage: Uri?, close: () -> Unit) {
        loading.value = true
        if (!checkUpdateContent(profileImage)) {
            viewModelScope.launch {
                kotlin.runCatching {
                    val imageFile = profileImage?.let { UriUtil.toFile(context, it) }
                    val compressedFile = imageFile?.let { Compressor.compress(context, it) }
                    api.updateProfile(token, updatedUserProfile.value, compressedFile)
                }.onSuccess { profile ->
                    userInfoDataStore.edit {
                        it[PreferencesKey.USERNAME] = profile.username
                    }
                    userInfoDataStore.edit {
                        it[PreferencesKey.PROFILE] = Json.encodeToString(profile)
                    }
                    setUpdatedUserProfile(profile)

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
        } else {
            close()
            loading.value = false
            updateCompleteBtnEnabled.value = true
        }
    }

    private fun getMyAccounts() {
        viewModelScope.launch {
            kotlin.runCatching {
                api.getMyAccounts(token)
            }.onSuccess { response ->
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

    fun changeProfile(profile: Profile) {
        viewModelScope.launch {
            userInfoDataStore.edit {
                it[PreferencesKey.PROFILE] = Json.encodeToString(profile)
            }
            userInfoDataStore.edit {
                it[PreferencesKey.USERNAME] = profile.username
            }
            setUpdatedUserProfile(profile)

            // newTeamProfile follow에 username넣기
            newTeamProfile.following = listOf(profile.username)
            newTeamProfile.follower = listOf(profile.username)
            newTeamProfile.teamOrMember = listOf(profile.username)

            // chat연결
            val user = User(
                id = profile.username,
                name = profile.username,
                image = profile.profileImage
            )
            profile.chatToken?.let { token ->
                chatClient.disconnect(true).await()
                chatClient.connectUser(user, token).enqueue()
            }
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
            "", "", null, "", "", "", "", userCreatedAt = "")
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
            "", "", null, "", "", "", "", userCreatedAt = "")
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

    fun like(post: Post, completion: (List<String>) -> Unit) {
        viewModelScope.launch {
            kotlin.runCatching {
                val postAPI = PostAPI()
                val like = LikeObj(myProfile.value.username, post.username, post.createdAt)
                postAPI.like(like)
            }.onSuccess {
                completion(it)
                Log.d("like", "$it")
            }.onFailure {
                Log.d("like", "message: $it")
            }
        }
    }

    fun unlike(post: Post, completion: (List<String>) -> Unit) {
        viewModelScope.launch {
            kotlin.runCatching {
                val postAPI = PostAPI()
                val like = LikeObj(myProfile.value.username, post.username, post.createdAt)
                postAPI.unlike(like)
            }.onSuccess {
                completion(it)
                Log.d("unlike", "$it")
            }.onFailure {
                Log.d("unlike", "message: $it")
            }
        }
    }
}