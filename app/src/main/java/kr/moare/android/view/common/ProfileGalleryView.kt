package kr.moare.android.view.common

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.canhub.cropper.*
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.isGranted
import kr.moare.android.entities.BottomSheet
import kr.moare.android.utils.StringResources
import kr.moare.android.viewmodel.common.GalleryViewModel

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ProfileGalleryView(
    bottomSheet: BottomSheet,
    galleryVM: GalleryViewModel,
    permissionRequested: Boolean,
    permissionState: PermissionState,
    isDefaultImage: () -> Unit,
) {
    val attachments by galleryVM.attachments.collectAsState()
    val selectedImage by galleryVM.selectedImage.collectAsState()

    val imageCropLauncher = rememberLauncherForActivityResult(CropImageContract()) { result ->
        if (result.isSuccessful) {
            isDefaultImage()
            bottomSheet.subCloseSheet()
            galleryVM.croppedImage.value = result.uriContent?.let { it }
        }
    }

    val context = LocalContext.current

    LaunchedEffect(permissionState.status.isGranted) {
        if (permissionState.status.isGranted) {
            galleryVM.loadAttachments(true)
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
                        bottomSheet.subCloseSheet()
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
                    items(attachments) { attachment ->
                        Box(
                            modifier = Modifier
                                .clip(RectangleShape)
                                .background(Color.Transparent)
                                .aspectRatio(1f)
                        ) {
                            AsyncImage(
                                model = attachment.uri,
                                contentDescription = "image",
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clickable {
                                        galleryVM.selectProfileImage(attachment) {
//                                    imagePickerLauncher.launch("image/*")
                                            val cropOptions = CropImageOptions()
                                            cropOptions.allowRotation = false
                                            cropOptions.cropShape = CropImageView.CropShape.OVAL
                                            cropOptions.fixAspectRatio = true
                                            cropOptions.allowFlipping = false
                                            cropOptions.cropMenuCropButtonTitle = StringResources.complete
                                            cropOptions.initialCropWindowPaddingRatio = 0f
                                            cropOptions.minCropResultHeight = 500
                                            cropOptions.minCropResultWidth = 500
                                            cropOptions.autoZoomEnabled = false

                                            val cropContractOptions = CropImageContractOptions(it, cropOptions)

                                            imageCropLauncher.launch(cropContractOptions)
                                        }
                                    },
                                contentScale = ContentScale.Crop
                            )
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
    }
}