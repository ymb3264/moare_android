package kr.moare.android.viewmodel.post

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kr.moare.android.entities.Attachment
import kr.moare.android.entities.MediaUrl
import kr.moare.android.entities.Post
import kr.moare.android.entities.SelectedMedia
import kr.moare.android.network.PostAPI
import kr.moare.android.utils.StorageHelper
import kr.moare.android.utils.UriUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import id.zelory.compressor.Compressor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class PostAddViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val dataStore: DataStore<Preferences>,
    private val encryptedSharedPreferences: SharedPreferences
) : ViewModel() {
    val api = PostAPI()
    val storageHelper = StorageHelper()

    val username = encryptedSharedPreferences.getString("username", "") ?: ""

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

    var attachments = MutableStateFlow<List<Attachment>>(mutableStateListOf())
    var selectedMediaList = MutableStateFlow<MutableList<SelectedMedia>>(mutableStateListOf())

    var post = Post(username, "", "", MediaUrl(listOf(), listOf()),
        "", mutableListOf(), "", "", "")

    init {
        loadAttachments()
        viewModelScope.launch {
            placeFlow.collect() {
                if (it != "") {
                    post.place = it
                }
            }
            xFlow.collect() {
                post.x = it
            }
            yFlow.collect() {
                post.y = it
            }
        }
    }

    fun loadAttachments() {
        val images = storageHelper.getMediaAttachments(context)
        this.attachments.value = images
        Log.d("filesss", images.toString())
    }

    fun selectMediaItems(index: Int, attachment: Attachment) {
        val dataSet = attachments.value
        val newFiles = dataSet.toMutableList()
        val newItem: Attachment
        val seletedMedia = SelectedMedia(attachment.uri!!, attachment.type!!)

        if (selectedMediaList.value.indexOf(seletedMedia) == -1) {
            selectedMediaList.value.add(seletedMedia)
            newItem = dataSet[index].copy(
                isSelected = !newFiles[index].isSelected
            )
        } else {
            selectedMediaList.value.removeAt(selectedMediaList.value.indexOf(seletedMedia))
            newItem = dataSet[index].copy(
                isSelected = !newFiles[index].isSelected
            )
        }

        newFiles.removeAt(index)
        newFiles.add(index, newItem)

        attachments.value = newFiles
    }

    fun createPost(content: String) {
        viewModelScope.launch {
            kotlin.runCatching {
                val fileList = mutableListOf<File>()
                selectedMediaList.value.forEach { media ->
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
                api.createPost(content, fileList)
            }.onSuccess {
                Log.d("addSuccess", "$it")
            }.onFailure {
                Log.d("addFail", "message: $it")
            }
        }
        selectedMediaList.value[0]?.let { video ->
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
}