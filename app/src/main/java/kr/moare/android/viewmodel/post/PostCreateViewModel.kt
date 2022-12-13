package kr.moare.android.viewmodel.post

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kr.moare.android.network.PostAPI
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import id.zelory.compressor.Compressor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.job
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kr.moare.android.entities.*
import kr.moare.android.utils.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class PostCreateViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    @LocationDataStore private val locationDataStore: DataStore<Preferences>,
    @UserInfoDataStore private val userInfoDataStore: DataStore<Preferences>,
    private val encryptedSharedPreferences: SharedPreferences
) : ViewModel() {
    val api = PostAPI()

    val token = encryptedSharedPreferences.getString("token", "") ?: ""

    private val usernameFlow = userInfoDataStore.data.map {
        it[PreferencesKey.USERNAME] ?: ""
    }
    val profileFlow = userInfoDataStore.data.map {
        it[PreferencesKey.PROFILE] ?: ""
    }
    var username = ""

    private val currentLocationFlow = locationDataStore.data.map {
        it[PreferencesKey.CURRENTLOCATION] ?: ""
    }
    private val locationListFlow = locationDataStore.data.map {
        it[PreferencesKey.LOCATIONLIST] ?: setOf()
    }

    val content = MutableStateFlow("")
    val loading = MutableStateFlow(false)
    val completeBtn = MutableStateFlow(false)

    var post = Post("", "", "", "", listOf(), "", listOf(), "", "", "", null)

    init {
        viewModelScope.launch {
            currentLocationFlow.collect { current ->
                if (current.isNotEmpty()) {
                    val location = Json.decodeFromString<UserDefaultLocation>(current)

                    post.place = location.address
                    post.x = location.x
                    post.y = location.y
                }
                coroutineContext.job.cancel()
            }
        }
        viewModelScope.launch {
            usernameFlow.collect {
                username = it
                post.username = it
                coroutineContext.job.cancel()
            }
        }
    }

    fun createPost(selectedMediaList: MutableList<SelectedMedia>, completion: () -> Unit) {
        loading.value = true
        viewModelScope.launch {
            kotlin.runCatching {
                val yearMonthFormatter = SimpleDateFormat("yyyy-MM", Locale.KOREA)
                val nowFormatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.KOREA)

                post.yearAndMonth = yearMonthFormatter.format(Date())
                post.createdAt = nowFormatter.format(Date())

                val fileList = mutableListOf<File>()
                selectedMediaList.forEach { media ->
                    if (media.type == "video") {
                        // 원본 비디오 보내기
                        val videoFile = UriUtil.toFile(context, media.uri)
                        fileList.add(videoFile)
                    } else {
                        val imageFile = UriUtil.toFile(context, media.uri)
                        val compressedFile = Compressor.compress(context, imageFile)
                        fileList.add(compressedFile)
                    }
                }

                api.createPost(token, post, fileList)
            }.onSuccess {
                loading.value = false

                // post reload
                viewModelScope.launch {
                    currentLocationFlow.collect { current ->
                        if (current.isNotEmpty()) {
                            val location = Json.decodeFromString<UserDefaultLocation>(current)

                            locationDataStore.edit {
                                it[PreferencesKey.CURRENTLOCATION] = ""
                            }
                            locationDataStore.edit {
                                it[PreferencesKey.CURRENTLOCATION] = current
                            }
                        }
                        coroutineContext.job.cancel()
                    }
                }

                // userPost reload
                viewModelScope.launch {
                    usernameFlow.collect { username ->
                        userInfoDataStore.edit {
                            it[PreferencesKey.USERNAME] = ""
                        }
                        userInfoDataStore.edit {
                            it[PreferencesKey.USERNAME] = username
                        }
                        coroutineContext.job.cancel()
                    }
                }

                completion()
                Log.d("addSuccess", "$it")
            }.onFailure {
                loading.value = false
                Log.d("addFail", "message: $it")
            }
        }

        selectedMediaList[0]?.let { video ->
            // check bitrate
//            val mediaMetadataRetriever = MediaMetadataRetriever()
//
//            try {
//                mediaMetadataRetriever.setDataSource(context, video.uri)
//            } catch (exception: IllegalArgumentException) {
//                CompressorUtils.printException(exception)
//            }
//            val bitrateData =
//                mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITRATE)
//
//            Log.d("bttttt", bitrateData.toString())

            if (video.type == "video") {
                // video compress
//                VideoCompressor.start(
//                    context = context,
//                    uris = listOf(video.uri),
//                    isStreamable = true,
//                    storageConfiguration = StorageConfiguration(
////                        saveAt = Environment.DIRECTORY_MOVIES,
//                        isExternal = false
//                    ),
//                    configureWith = Configuration(
//                        quality = VideoQuality.MEDIUM,
//                        isMinBitrateCheckEnabled = true,
////                        videoBitrate = null,
//                        disableAudio = false,
//                        keepOriginalResolution = false,
//                        videoWidth = 1280.0,
//                        videoHeight = 720.0,
//                    ),
//                    listener = object : CompressionListener {
//                        override fun onCancelled(index: Int) {
//                            Log.d("videoCompressStart", index.toString())
//                        }
//
//                        override fun onFailure(index: Int, failureMessage: String) {
//                            Log.d("videoCompressStart", failureMessage)
//                        }
//
//                        override fun onProgress(index: Int, percent: Float) {
//                            Log.d("videoCompressStart", percent.toString())
//                        }
//
//                        override fun onStart(index: Int) {
//                            Log.d("videoCompressStart", index.toString())
//                        }
//
//                        override fun onSuccess(index: Int, size: Long, path: String?) {
//
//                            if (path != null) {
//                                viewModelScope.launch {
//                                    kotlin.runCatching {
//                                        api.addPost(content, path)
//                                    }.onSuccess {
//                                        Log.d("addSuccess", "$it")
//                                    }.onFailure {
//                                        Log.d("addFail", "message: $it")
//                                    }
//                                }
//                            }
//                        }
//
//                    }
//                )
            }
        }
    }

    fun checkContent(selectedMediaList: MutableList<SelectedMedia>): Boolean {
        return selectedMediaList.isEmpty() && post.sportHashtag.isEmpty() && post.content.isEmpty()
    }

    fun checkCompleteBtn(selectedMediaList: MutableList<SelectedMedia>) {
        completeBtn.value = selectedMediaList.isNotEmpty() &&
                post.sportHashtag.isNotEmpty() && post.content.isNotEmpty() && post.place.isNotEmpty()
    }
}