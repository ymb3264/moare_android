package kr.moare.android.viewmodel.common

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kr.moare.android.entities.Attachment
import kr.moare.android.entities.SelectedMedia
import kr.moare.android.utils.StorageHelper
import javax.inject.Inject

@HiltViewModel
class GalleryViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
) : ViewModel() {
    private val storageHelper = StorageHelper()

    var attachments = MutableStateFlow<List<Attachment>>(mutableStateListOf())

    // for profile
    var selectedImage = MutableStateFlow<SelectedMedia?>(null)
    var croppedImage = MutableStateFlow<Uri?>(null)
    // for post
    var selectedMediaList = MutableStateFlow<MutableList<SelectedMedia>>(mutableStateListOf())

    init {
        loadAttachments()
    }

    fun loadAttachments() {
        val images = storageHelper.getMediaAttachments(context, true)
        this.attachments.value = images
    }

    fun selectProfileImage(
        attachment: Attachment,
        cropImage: (Uri) -> Unit
    ) {
        val seletedMedia = SelectedMedia(attachment.uri!!, attachment.type!!)

        selectedImage.value = seletedMedia
        selectedImage.value?.let { cropImage(it.uri) }
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
}