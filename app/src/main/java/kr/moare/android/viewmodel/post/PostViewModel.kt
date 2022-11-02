package kr.moare.android.viewmodel.post

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil.request.ImageRequest
import kr.moare.android.entities.MediaUrl
import kr.moare.android.entities.Post
import kr.moare.android.entities.UserDefaultPlace
import kr.moare.android.network.PostAPI
import kr.moare.android.utils.StorageHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PostViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val dataStore: DataStore<Preferences>,
    private val encryptedSharedPreferences: SharedPreferences
) : ViewModel() {

    var postList = MutableStateFlow<MutableList<List<Post>>>(mutableStateListOf())
    var showSearchView = MutableStateFlow(false)
    val api = PostAPI()
    val username = encryptedSharedPreferences.getString("username", "") ?: ""

    val storageHelper = StorageHelper()

    val PLACE = stringPreferencesKey("place")
    val X = stringPreferencesKey("x")
    val Y = stringPreferencesKey("y")
    val placeFlow: Flow<String> = dataStore.data.map { preferences ->
        preferences[PLACE] ?: ""
    }
    val xFlow: Flow<String> = dataStore.data.map { preferences ->
        preferences[X] ?: ""
    }
    val yFlow: Flow<String> = dataStore.data.map { preferences ->
        preferences[Y] ?: ""
    }

    var place = UserDefaultPlace("", "", "")
    var allPost = mutableListOf<Post>()
    var num = 0

    var post = MutableStateFlow<Post>(
        Post(username, "", "", MediaUrl(listOf(), listOf()),
            "", mutableListOf(), "", "", "")
    )

    var showingGallry = MutableStateFlow(false)

    val isRefreshing = MutableStateFlow(false)

    var onePost = MutableStateFlow<Post>(
        Post(username, "", "", MediaUrl(listOf(), listOf()),
            "", mutableListOf(), "", "", "")
    )

    init {
        viewModelScope.launch {
//            if (placeFlow.toString() != "") {
                initUserLocation()
//                getAllPost()
//            }
        }
    }

    suspend fun getAllPost() {
        kotlin.runCatching {
            isRefreshing.value = true
            postList.value.clear()
            api.getPosts("2022/10",place, username, "date")
        }.onSuccess {
            allPost = it as MutableList<Post>
            var firstList = mutableListOf<Post>()
            allPost.forEachIndexed { index, post ->
                val image = post.mediaUrl.image
                if (index < 6 && image.isNotEmpty()) {
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
            num += 6
            isRefreshing.value = false
            Log.d("post", "${postList.value}")
        }.onFailure {
            isRefreshing.value = false
            Log.d("FAIL", "message: $it")
        }
    }

    fun getMorePost() {
        var newList = mutableListOf<Post>()
        val count = allPost.count()

        if (count in (num + 1) until (num + 6)) {
            for (post in num until count) {
                var post = allPost[post]
                val image = post.mediaUrl.image
                val video = post.mediaUrl.video
                if (image.isNotEmpty()) {
                    var imageList = mutableListOf<ImageRequest>()
                    for (element in image) {
                        val request = ImageRequest.Builder(context)
                            .data(element.url)
                            .crossfade(true)
                            .build()
                        imageList.add(request)
                    }
                    post.imageRequest = imageList
                    newList.add(post)
                }
            }
            postList.value.add(newList)
            num += 6
        } else if (count >= (num + 6)) {
            for (post in num until num+6) {
                var post = allPost[post]
                val image = post.mediaUrl.image
                val video = post.mediaUrl.video
                if (image.isNotEmpty()) {
                    var imageList = mutableListOf<ImageRequest>()
                    for (element in image) {
                        val request = ImageRequest.Builder(context)
                            .data(element.url)
                            .crossfade(true)
                            .build()
                        imageList.add(request)
                    }
                    post.imageRequest = imageList
                    newList.add(post)
                }
            }
            postList.value.add(newList)
            num += 6
        }
        Log.d("post", "${postList.value[1]}")
    }

    suspend fun initUserLocation() {
        placeFlow.collect {
            if (it != "") {
                place.address = it
                xFlow.collect {
                    place.x = it
                    yFlow.collect {
                        place.y = it
                        getAllPost()
                    }
                }
            }
        }
    }

    fun getPost(number: Int) {
        viewModelScope.launch {
            kotlin.runCatching {
                api.getPost(number)
            }.onSuccess {
                onePost.value = it
                Log.d("getPostSuccess", "$it")
            }.onFailure {
                Log.d("getPostFail", "message: $it")
            }
        }
    }

    suspend fun deletePlace() {
        dataStore.edit {
            it[PLACE] = ""
        }
    }

    fun removeToken() {
        encryptedSharedPreferences.edit().putString("token", "").apply()
        val token = encryptedSharedPreferences.getString("token", "") ?: ""
        Log.d("token", "$token")
    }
}