package kr.moare.android.view.common

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.compose.rememberImagePainter
import coil.decode.VideoFrameDecoder
import coil.request.ImageRequest
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.isGranted
import kotlinx.coroutines.launch
import kr.moare.android.entities.BottomSheet
import kr.moare.android.entities.SelectedMedia
import kr.moare.android.utils.StringResources
import kr.moare.android.viewmodel.common.GalleryViewModel
import kr.moare.android.viewmodel.post.PostCreateViewModel

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun GalleryView(
    bottomSheet: BottomSheet,
    galleryVM: GalleryViewModel,
    permissionRequested: Boolean,
    permissionState: PermissionState,
    onMediaChange: () -> Unit = {}
) {
    val attachments by galleryVM.attachments.collectAsState()
    val tempSelectedMediaList by galleryVM.tempSelectedMediaList.collectAsState()

    var alert by remember { mutableStateOf(false) }

    val imageLoader = ImageLoader.Builder(LocalContext.current)
        .components {
            add(VideoFrameDecoder.Factory())
        }
        .build()

    val context = LocalContext.current

    LaunchedEffect(permissionState.status.isGranted) {
        if (permissionState.status.isGranted) {
            galleryVM.loadAttachments(false)
        }
    }

    LaunchedEffect(Unit) {
        if (!permissionRequested && !permissionState.status.isGranted) {
            permissionState.launchPermissionRequest()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                backgroundColor = Color.Transparent,
                elevation = 0.dp
            ) {
                Button(
                    onClick = {
                        // stateFlow가 공유되지않게 새로운 변수 선언
                        val new = galleryVM.selectedMediaList.value.toMutableList()
                        galleryVM.tempSelectedMediaList.value = new
                        bottomSheet.subCloseSheet()
                    },
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color.Transparent
                    ),
                    elevation = ButtonDefaults.elevation(
                        defaultElevation = 0.dp
                    )
                ) {
                    Text(text = StringResources.cancel)
                }

                Row(
                    modifier = Modifier
                        .weight(1f),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "사진 및 영상 선택",
                        style = MaterialTheme.typography.h6,
                    )
                }

                Button(
                    onClick = {
                        val new = tempSelectedMediaList.toMutableList()
                        galleryVM.selectedMediaList.value = new
                        bottomSheet.subCloseSheet()
                        onMediaChange()
                    },
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color.Transparent
                    ),
                    elevation = ButtonDefaults.elevation(
                        defaultElevation = 0.dp
                    )
                ) {
                    Text(text = StringResources.complete)
                }
            }
        }
    ) {
        when (permissionState.status) {
            PermissionStatus.Granted -> {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(2.dp),
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    itemsIndexed(attachments) { index, attachment ->
                        val selectedMedia = SelectedMedia(attachment.uri!!, attachment.type!!, index)

                        Box(
                            modifier = Modifier
                                .clip(RectangleShape)
                                .background(Color.Transparent)
                                .aspectRatio(1f),
                            contentAlignment = Alignment.TopEnd
                        ) {
                            AsyncImage(
                                model = attachment.uri,
                                imageLoader = imageLoader,
                                contentDescription = "image",
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clickable {
                                        if (attachment.videoIntLength != null) {
                                            if (attachment.videoIntLength!! > 30) {
                                                alert = true
                                            } else {
                                                galleryVM.selectMediaItems(index, attachment)
                                            }
                                        } else {
                                            galleryVM.selectMediaItems(index, attachment)
                                        }
                                    },
                                contentScale = ContentScale.Crop
                            )

                            if (attachment.type == "video") {
                                Text(
                                    text = "${attachment.videoStringLength}",
                                    modifier = Modifier
                                        .background(Color.Black.copy(0.4f))
                                        .padding(4.dp)
                                        .align(Alignment.BottomEnd),
                                    style = MaterialTheme.typography.body2,
                                    color = Color.White,
                                )
                            }
                            Box(
                                Modifier
                                    .padding(top = 4.dp, end = 4.dp)
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (attachment.isSelected) MaterialTheme.colors.primary else Color.Black.copy(
                                            0.3f
                                        )
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                if (attachment.isSelected) {
                                    Text("${tempSelectedMediaList.indexOf(selectedMedia)+1}", color = Color.White)
                                }
                            }
                        }
                    } // itemindexed
                } // lazyverticalgrid
            } // permission.granted
            is PermissionStatus.Denied -> {
                TextButton(onClick = {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        data = Uri.fromParts("package", context.packageName, null)
                    }
                    context.startActivity(intent)
                }) {
                    Text(text = "미디어 파일 접근권한을 허용해주세요")
                }
            }
        }

        if (alert) {
            AlertDialog(
                onDismissRequest = { alert = false },
                confirmButton = {
                    TextButton(onClick = {
                        alert = false
                    }) {
                        Text(text = StringResources.confirm)
                    }
                },
                title = { Text(text = StringResources.videoLengthLimitAlertTitle) },
                text = { Text(text = StringResources.videoLenthLimitAlertMessage) }
            )
        } // if showAlert
    }
}