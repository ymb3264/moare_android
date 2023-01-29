package kr.moare.android.entities

import android.net.Uri
import java.io.File

data class Attachment(
    var uri: Uri? = null,
    var type: String? = null,
    var mimeType: String?  = null,
    var title: String? = null,
    var file: File? = null,
    var size: Long? = null,
    var isSelected: Boolean = false,
    var selectedPosition: Int? = 0,
    var videoStringLength: String? = null,
    var videoIntLength: Int? = null
)