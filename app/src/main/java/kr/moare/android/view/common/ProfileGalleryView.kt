package kr.moare.android.view.common

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.decode.VideoFrameDecoder
import com.canhub.cropper.*
import kr.moare.android.entities.BottomSheet
import kr.moare.android.viewmodel.profile.ProfileViewModel

@Composable
fun ProfileGalleryView(
    bottomSheet: BottomSheet,
    profileVM: ProfileViewModel
) {
    val imageLoader = ImageLoader.Builder(LocalContext.current)
        .components {
            add(VideoFrameDecoder.Factory())
        }
        .build()

    val attachments by profileVM.attachments.collectAsState()
    val selectedImage by profileVM.selectedImage.collectAsState()
//    val mediaUriList by postVM.mediaUriList.collectAsState()

    val imageCropLauncher = rememberLauncherForActivityResult(CropImageContract()) { result ->
        if (result.isSuccessful) {
            bottomSheet.subCloseSheet()
            profileVM.croppedImage.value = result.uriContent?.let { it }
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
                    Text(text = "취소")
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
                    Text(text = "완료")
                }
            }
        }
    ) {
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
                                profileVM.selectProfileImage(attachment) {
//                                    imagePickerLauncher.launch("image/*")
                                    val cropOptions = CropImageOptions()
                                    cropOptions.allowRotation = false
                                    cropOptions.cropShape = CropImageView.CropShape.OVAL
                                    cropOptions.fixAspectRatio = true
                                    cropOptions.allowFlipping = false
                                    cropOptions.cropMenuCropButtonTitle = "완료"
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
            }
        }
    }
}