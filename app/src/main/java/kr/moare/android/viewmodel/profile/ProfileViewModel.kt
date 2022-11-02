package kr.moare.android.viewmodel.profile

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil.request.ImageRequest
import kr.moare.android.entities.*
import kr.moare.android.network.ProfileAPI
import kr.moare.android.utils.StorageHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import id.zelory.compressor.Compressor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kr.moare.android.utils.UriUtil
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val dataStore: DataStore<Preferences>,
    savedStateHandle: SavedStateHandle,
    private val encryptedSharedPreferences: SharedPreferences
) : ViewModel() {
    val api = ProfileAPI()
    val now = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.KOREA)

    val token = encryptedSharedPreferences.getString("token", "") ?: ""
    val username = encryptedSharedPreferences.getString("username", "") ?: ""

    val PLACE = stringPreferencesKey("place")
    val placeFlow: Flow<String> = dataStore.data.map { preferences ->
        preferences[PLACE] ?: ""
    }

    var profile = MutableStateFlow<UserProfile>(
        UserProfile("", "", listOf(), "", "", "",
            "", false, listOf(), listOf(), listOf())
    )

    var newTeamProfile = UserProfile(now.toString(), "", listOf(), "", "", "",
        "", true, listOf(), listOf(), listOf())
    var newUserProfile = MutableStateFlow<UpdateProfile>(
        UpdateProfile("", "", listOf(), "", "", "",
            "")
    )

    var myAccounts: List<UserProfile> = listOf()

    var postList = MutableStateFlow<MutableList<List<Post>>>(mutableStateListOf())
    val isRefreshing = MutableStateFlow(false)
    val updateLoading = MutableStateFlow(false)
    val myAccountsLoading = MutableStateFlow(false)
    var allPost = mutableListOf<Post>()
    var num = 0

    init {
        viewModelScope.launch {
            getUserPosts()
        }
        viewModelScope.launch {
            placeFlow.collect() {
                if (it != "") {
                    newTeamProfile.place = it
                }
            }
        }
        getUserProfile()
    }

    private fun getUserProfile() {
        viewModelScope.launch {
            kotlin.runCatching {
                val username = encryptedSharedPreferences.getString("username", "") ?: ""
                api.getMyProfile(token, username)
            }.onSuccess {
                // 다른 변수에 it을 담아도 공유가된다?!?!?!?!?
                profile.value = it
                newUserProfile.value.createdAt = it.createdAt
                newUserProfile.value.username = it.username
                newUserProfile.value.sport = it.sport
                newUserProfile.value.name = it.name
                newUserProfile.value.profileImage = it.profileImage
                newUserProfile.value.content = it.content
                newUserProfile.value.place = it.place
                Log.d("success", "$it")
            }.onFailure {
                Log.d("FAIL", "message: $it")
            }
        }
    }

    suspend fun getUserPosts() {
        kotlin.runCatching {
            isRefreshing.value = true
            postList.value.clear()
            api.getUserPosts(username)
        }.onSuccess {
            allPost = it as MutableList<Post>
            var firstList = mutableListOf<Post>()
            allPost.forEachIndexed { index, post ->
                val image = post.mediaUrl.image
                if (index < 9 && image.isNotEmpty()) {
                    var imageList = mutableListOf<ImageRequest>()
                    for (element in image) {
                        val request = ImageRequest.Builder(context)
                            .data(element.url)
                            .crossfade(true)
                            .build()
                        imageList.add(request)
                    }
                    post.imageRequest = imageList
                    firstList.add(post)
                }
            }
            Log.d("success", "$it")
            postList.value.add(firstList)
            num += 9
            isRefreshing.value = false
            Log.d("post", "${postList.value}")
        }.onFailure {
            isRefreshing.value = false
            Log.d("FAIL", "message: $it")
        }
    }

    fun createTeamProfile(profileImage: Uri?) {
        viewModelScope.launch {
            kotlin.runCatching {
                val imageFile = profileImage?.let { UriUtil.toFile(context, it) }
                val compressedFile = imageFile?.let { Compressor.compress(context, it) }
                api.createTeamProfile(token, newTeamProfile, compressedFile)
            }.onSuccess {
                Log.d("success", "$it")
            }.onFailure {
                Log.d("FAIL", "message: $it")
            }
        }
    }

    fun updateProfile(profileImage: Uri?, closeSheet: () -> Unit) {
        viewModelScope.launch {
            kotlin.runCatching {
                updateLoading.value = true
                val imageFile = profileImage?.let { UriUtil.toFile(context, it) }
                val compressedFile = imageFile?.let { Compressor.compress(context, it) }
                api.updateProfile(token, newUserProfile.value, compressedFile)
            }.onSuccess {
                profile.value = it
                closeSheet()
                updateLoading.value = false
                Log.d("success", "$it")
            }.onFailure {
                // 실패했을때 alert보여주기
                closeSheet()
                updateLoading.value = false
                Log.d("FAIL", "message: $it")
            }
        }
    }

    fun getMyAccounts() {
        viewModelScope.launch {
            kotlin.runCatching {
                myAccountsLoading.value = true
                api.getMyAccounts(token)
            }.onSuccess {
                myAccounts = it
                myAccountsLoading.value = false
                Log.d("myAccounts", "$it")
            }.onFailure {
                myAccountsLoading.value = false
                Log.d("FAIL", "message: $it")
            }
        }
    }

    fun changeProfile(username: String) {
        myAccounts.forEach {
            if (it.username == username) {
                profile.value = it
                return
            }
        }
    }
}