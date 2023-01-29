package kr.moare.android.utils

import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import kr.moare.android.entities.Attachment
import java.text.SimpleDateFormat
import java.util.*

class StorageHelper {
    public fun getMediaAttachments(context: Context, profile: Boolean = false): List<Attachment> {
        val columns = arrayOf(
            MediaStore.Files.FileColumns._ID,
            MediaStore.Files.FileColumns.DISPLAY_NAME,
            MediaStore.Files.FileColumns.MIME_TYPE,
            MediaStore.Files.FileColumns.SIZE,
            MediaStore.Files.FileColumns.DURATION
        )
        val selection = (
                MediaStore.Files.FileColumns.MEDIA_TYPE + "=" +
                        MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE +
                        " OR " +
                        MediaStore.Files.FileColumns.MEDIA_TYPE + "=" +
                        MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO
                )
        context.contentResolver.query(
            MediaStore.Files.getContentUri("external"),
            columns,
            selection,
            null,
            "${MediaStore.Files.FileColumns.DATE_ADDED} DESC"
        )?.use { cursor ->
            return mutableListOf<Attachment>().apply {
                while (cursor.moveToNext()) {
                    val attachment = getAttachmentFromCursor(cursor)
                    if (profile) {
                        if (attachment.type != "file" && attachment.type != "video") {
                            add(getAttachmentFromCursor(cursor))
                        }
                    } else {
//                        if (attachment.type != "file") {
                            add(getAttachmentFromCursor(cursor))
//                        }
                    }
                }
            }
        }
        return emptyList()
    }

    public fun deleteFile(context: Context, fileName: String) {
        val columns = arrayOf(
            MediaStore.Files.FileColumns._ID,
            MediaStore.Files.FileColumns.DISPLAY_NAME
        )
        context.contentResolver.query(
            MediaStore.Files.getContentUri("external"),
            columns,
            null,
            null,
            null
        )?.use { cursor ->
            val displayNameIndex = cursor.getColumnIndex(MediaStore.Files.FileColumns.DISPLAY_NAME)
            while (cursor.moveToNext()) {
                val id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID))
                val displayName = cursor.getString(displayNameIndex)
                if (displayName == fileName) {
                    val contentUri = Uri.withAppendedPath(MediaStore.Files.getContentUri("external"), id.toString())
                    context.contentResolver.delete(contentUri, null, null)
                    return
                }
            }
        }
    }

    private fun getAttachmentFromCursor(cursor: Cursor): Attachment {
        val displayNameIndex = cursor.getColumnIndex(MediaStore.Files.FileColumns.DISPLAY_NAME)
        val fileSizeIndex = cursor.getColumnIndex(MediaStore.Files.FileColumns.SIZE)
        val mimeTypeIndex = cursor.getColumnIndex(MediaStore.Files.FileColumns.MIME_TYPE)
        val durationIndex = cursor.getColumnIndex(MediaStore.Files.FileColumns.DURATION)

        val id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID))

        val displayName = if (displayNameIndex != -1 && !cursor.isNull(displayNameIndex)) {
            cursor.getString(displayNameIndex)
        } else null

        val fileSize = if (fileSizeIndex != -1 && !cursor.isNull(fileSizeIndex)) {
            cursor.getLong(fileSizeIndex)
        } else 0L

        val mimeType = if (mimeTypeIndex != -1 && !cursor.isNull(mimeTypeIndex)) {
            cursor.getString(mimeTypeIndex)
        } else null

        val duration = if (durationIndex != -1 && !cursor.isNull(fileSizeIndex)) {
            cursor.getLong(durationIndex)
        } else 0L

        val date  = Date(duration)
        val formatter = SimpleDateFormat("mm:ss")

        return Attachment(
            uri = getContentUri(mimeType, id),
            type = getMediaType(mimeType),
            mimeType = mimeType,
            title = displayName,
            size = fileSize,
            videoStringLength = formatter.format(date),
            videoIntLength = (duration / 1000).toInt()
        )
    }

    private fun getContentUri(mimeType: String?, id: Long): Uri {
        val contentUri: Uri = when {
            isImage(mimeType) -> MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            isVideo(mimeType) -> MediaStore.Video.Media.EXTERNAL_CONTENT_URI
            else -> MediaStore.Files.getContentUri("external")
        }
        return ContentUris.withAppendedId(contentUri, id)
    }

    private fun getMediaType(mimeType: String?): String {
        return when {
            isImage(mimeType) -> "image"
            isVideo(mimeType) -> "video"
            else -> "file"
        }
    }

    private fun isImage(mimeType: String?): Boolean {
        return mimeType?.startsWith("image") ?: false
    }

    private fun isVideo(mimeType: String?): Boolean {
        return mimeType?.startsWith("video") ?: false
    }
}