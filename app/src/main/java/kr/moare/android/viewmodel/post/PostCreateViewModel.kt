package kr.moare.android.viewmodel.post

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abedelazizshe.lightcompressorlibrary.CompressionListener
import com.abedelazizshe.lightcompressorlibrary.VideoCompressor
import com.abedelazizshe.lightcompressorlibrary.VideoQuality
import com.abedelazizshe.lightcompressorlibrary.config.AppSpecificStorageConfiguration
import com.abedelazizshe.lightcompressorlibrary.config.Configuration
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import id.zelory.compressor.Compressor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.job
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kr.moare.android.entities.*
import kr.moare.android.network.PostAPI
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
    @UserIdUsernameDataStore private val userIdUsernameDataStore: DataStore<Preferences>,
    private val encryptedSharedPreferences: SharedPreferences
) : ViewModel() {
    val api = PostAPI()

    private val usernameFlow = userIdUsernameDataStore.data.map {
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

    var post = CreatePost("", "", "", "",
        "", listOf(), "", listOf(), "", "", "")
    var myProfile = Profile(createdAt = "", username = "", sportHashtag = listOf(), name = "", profileImage = "", content = "", place = "", isTeam = false)


    init {
        viewModelScope.launch {
            currentLocationFlow.collect { current ->
                if (current.isNotEmpty()) {
                    val location = Json.decodeFromString<UserDefaultLocation>(current)

                    post.place = location.address
                    post.x = location.x
                    post.y = location.y
                }
//                coroutineContext.job.cancel()
            }
        }
        viewModelScope.launch {
            usernameFlow.collect {
                username = it
                post.username = it
                coroutineContext.job.cancel()
            }
        }
        viewModelScope.launch {
            profileFlow.collect {
                if (it.isNotEmpty()) {
                    val profile = Json.decodeFromString<Profile>(it)
                    myProfile = profile
                }
                coroutineContext.job.cancel()
            }
        }
    }

    fun createPost(selectedMediaList: MutableList<SelectedMedia>, completion: () -> Unit) {
        val storageHelper = StorageHelper()

        loading.value = true

        val yearMonthFormatter = SimpleDateFormat("yyyy-MM", Locale.KOREA)
        val nowFormatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.KOREA)

        post.yearAndMonth = yearMonthFormatter.format(Date())
        post.postCreatedAt = nowFormatter.format(Date())
        post.userCreatedAt = myProfile.createdAt
        post.profileImage = myProfile.profileImage

        val partition = selectedMediaList.partition { it.type == "video" }

        var videoListSum = 0
        for ((i, v) in partition.first.withIndex()) {
            videoListSum += i
        }

        if (partition.first.isEmpty()) {
            viewModelScope.launch {
                kotlin.runCatching {
                    val fileList = mutableListOf<File>()
                    for (media in selectedMediaList) {
                        val imageFile = UriUtil.toFile(context, media.uri)
                        val compressedFile = Compressor.compress(context, imageFile)
                        fileList.add(compressedFile)
                    }

                    val token = encryptedSharedPreferences.getString("AccessToken", "") ?: ""
                    api.createPost(token, post, fileList)
                }.onSuccess {
                    loading.value = false

                    // post reload
                    viewModelScope.launch {
                        currentLocationFlow.collect { current ->
                            if (current.isNotEmpty()) {
                                locationDataStore.edit {
                                    it[PreferencesKey.CURRENTLOCATION] =
                                        ""
                                }
                                locationDataStore.edit {
                                    it[PreferencesKey.CURRENTLOCATION] =
                                        current
                                }
                            }
                            coroutineContext.job.cancel()
                        }
                    }

                    // userPost reload
                    viewModelScope.launch {
                        usernameFlow.collect { username ->
                            userIdUsernameDataStore.edit {
                                it[PreferencesKey.USERNAME] = ""
                            }
                            userIdUsernameDataStore.edit {
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
        } else {
            val videoUriList = partition.first.map { it.uri }
            val compressedVideoList = mutableListOf<File>()
            val compressedVideoListForDelete = mutableListOf<File>()
            val compressedVideoFileNameList = mutableListOf<String>()
            val compressedImageList = mutableListOf<File>()
            val fileList = mutableListOf<File>()
            var compressedVideoListSum = 0

            VideoCompressor.start(
                context = context,
                uris = videoUriList,
                isStreamable = false,
//                sharedStorageConfiguration = SharedStorageConfiguration(
//                    saveAt = SaveLocation.movies,
//                    videoName = "compressed_video"
//                ),
                appSpecificStorageConfiguration = AppSpecificStorageConfiguration(
                    videoName = "compressed_video"
                ),
                configureWith = Configuration(
                    quality = VideoQuality.MEDIUM,
                    isMinBitrateCheckEnabled = false,
                    videoBitrateInMbps = 5,
                    disableAudio = false,
                    keepOriginalResolution = false,
                    videoWidth = 1280.0,
                    videoHeight = 720.0,
                ),
                listener = object : CompressionListener {
                    override fun onProgress(index: Int, percent: Float) {
                        Log.d("videoCompressStart", percent.toString())
                    }

                    override fun onStart(index: Int) {
                        Log.d("videoCompressStartIndex", index.toString())
                    }

                    override fun onSuccess(index: Int, size: Long, path: String?) {
                        path?.let {

                            compressedVideoListSum += index

                            val videoFile = File(path)
                            compressedVideoList.add(videoFile)
                            compressedVideoListForDelete.add(videoFile)
//                            compressedVideoFileNameList.add(path.split("/").last())

                            if (compressedVideoListSum == videoListSum) {
                                viewModelScope.launch {
                                    if (partition.second.isNotEmpty()) {
                                        for (image in partition.second) {
                                            val imageFile = UriUtil.toFile(context, image.uri)
                                            val compressedFile =
                                                Compressor.compress(context, imageFile)
                                            compressedImageList.add(compressedFile)
                                        }
                                    }

                                    // 선택한 미디어 순서에 맞게 upload할 이미지와 비디오를 fileList에 추가한다
                                    for (media in selectedMediaList) {
                                        if (media.type == "image") {
                                            for ((index, image) in compressedImageList.withIndex()) {
                                                fileList.add(image)
                                                compressedImageList.removeAt(index)
                                                break
                                            }
                                        } else {
                                            for ((index, video) in compressedVideoList.withIndex()) {
                                                fileList.add(video)
                                                compressedVideoList.removeAt(index)
                                                break
                                            }
                                        }
                                    }

                                    kotlin.runCatching {
                                        val token = encryptedSharedPreferences.getString("AccessToken", "") ?: ""
                                        api.createPost(token, post, fileList)
                                    }.onSuccess {
                                        loading.value = false

                                        // post reload
                                        viewModelScope.launch {
                                            currentLocationFlow.collect { current ->
                                                if (current.isNotEmpty()) {
                                                    val location =
                                                        Json.decodeFromString<UserDefaultLocation>(
                                                            current
                                                        )

                                                    locationDataStore.edit {
                                                        it[PreferencesKey.CURRENTLOCATION] =
                                                            ""
                                                    }
                                                    locationDataStore.edit {
                                                        it[PreferencesKey.CURRENTLOCATION] =
                                                            current
                                                    }
                                                }
                                                coroutineContext.job.cancel()
                                            }
                                        }

                                        // userPost reload
                                        viewModelScope.launch {
                                            usernameFlow.collect { username ->
                                                userIdUsernameDataStore.edit {
                                                    it[PreferencesKey.USERNAME] = ""
                                                }
                                                userIdUsernameDataStore.edit {
                                                    it[PreferencesKey.USERNAME] = username
                                                }
                                                coroutineContext.job.cancel()
                                            }
                                        }

                                        completion()
//                                        for (fileName in compressedVideoFileNameList) {
//                                            storageHelper.deleteFile(context, fileName)
//                                        }
                                        for (file in compressedVideoListForDelete) {
                                            file.delete()
                                        }
                                        Log.d("addSuccess", "$it")
                                    }.onFailure {
                                        loading.value = false
//                                        for (fileName in compressedVideoFileNameList) {
//                                            storageHelper.deleteFile(context, fileName)
//                                        }
                                        for (file in compressedVideoListForDelete) {
                                            file.delete()
                                        }
                                        Log.d("addFail", "message: $it")
                                    }
                                }
                            }
                        } // let
                    }

                    override fun onFailure(index: Int, failureMessage: String) {
                        Log.d("videoCompressStart", failureMessage)
                    }

                    override fun onCancelled(index: Int) {
                        Log.d("videoCompressStart", index.toString())
                    }
                }
            )
        }

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

    }

    fun checkContent(selectedMediaList: MutableList<SelectedMedia>): Boolean {
        return selectedMediaList.isEmpty() && post.sportHashtag.isEmpty() && post.content.isEmpty()
    }

    fun checkCompleteBtn(selectedMediaList: MutableList<SelectedMedia>) {
        completeBtn.value = selectedMediaList.isNotEmpty() &&
                post.sportHashtag.isNotEmpty() && post.place.isNotEmpty()
    }
}