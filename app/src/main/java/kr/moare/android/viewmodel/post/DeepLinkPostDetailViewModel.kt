package kr.moare.android.viewmodel.post

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.job
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kr.moare.android.entities.LikeObj
import kr.moare.android.entities.Post
import kr.moare.android.entities.Profile
import kr.moare.android.network.PostAPI
import kr.moare.android.utils.LocationDataStore
import kr.moare.android.utils.PreferencesKey
import kr.moare.android.utils.UserIdUsernameDataStore
import kr.moare.android.utils.UserInfoDataStore
import javax.inject.Inject

@HiltViewModel
class DeepLinkPostDetailViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    @LocationDataStore private val locationDataStore: DataStore<Preferences>,
    @UserIdUsernameDataStore private val userIdUsernameDataStore: DataStore<Preferences>,
    @UserInfoDataStore private val userInfoDataStore: DataStore<Preferences>,
    private val encryptedSharedPreferences: SharedPreferences,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    val api = PostAPI()

    val token = encryptedSharedPreferences.getString("AccessToken", "") ?: ""

    private val yearAndMonth = savedStateHandle["yearAndMonth"] ?: ""
    private val postCreatedAt = savedStateHandle["postCreatedAt"] ?:""

    private val usernameFlow = userIdUsernameDataStore.data.map {
        it[PreferencesKey.USERNAME] ?: ""
    }
    private val profileFlow = userInfoDataStore.data.map {
        it[PreferencesKey.PROFILE] ?: ""
    }
    var username = ""

    val post = MutableStateFlow(Post("", "", "", "", "", "", listOf(), "", listOf(), "", "", ""))

    init {
        getPost()
    }

    private fun getPost() {
        viewModelScope.launch {
            kotlin.runCatching {
                api.getPost(yearAndMonth, postCreatedAt)
            }.onSuccess {
                post.value = it
                Log.d("getPostSuccess", "$it")
            }.onFailure {
                Log.d("getPostFail", "message: $it")
            }
        }
    }

    fun like() {
        viewModelScope.launch {
            kotlin.runCatching {
                val like = LikeObj("", "", username, post.value.userID, post.value.postCreatedAt)
                api.like(like)
            }.onSuccess {
                post.value.like = it
                Log.d("like", "$it")
            }.onFailure {
                Log.d("like", "message: $it")
            }
        }
    }

    fun unlike() {
        viewModelScope.launch {
            kotlin.runCatching {
                val like = LikeObj("", "", username, post.value.userID, post.value.postCreatedAt)
                api.unlike(like)
            }.onSuccess {
                post.value.like = it
                Log.d("like", "$it")
            }.onFailure {
                Log.d("like", "message: $it")
            }
        }
    }

    fun reportPost(post: Post, cb: () -> Unit) {
        viewModelScope.launch {
            profileFlow.collect { encodedProfile ->
                if (encodedProfile.isNotEmpty()) {
                    kotlin.runCatching {
                        val profile = Json.decodeFromString<Profile>(encodedProfile)
                        val obj = mapOf(
                            "userID" to post.userID,
                            "createdAt" to post.postCreatedAt,
                            "userCreatedAt" to profile.createdAt
                        )
                        api.reportPost(token, obj)
                    }.onSuccess {
                        if (it.message == "report success") {
                            cb()
                        }
                    }.onFailure {

                    }
                } // if
                coroutineContext.job.cancel()
            } // collect
        } // launch
    }
}