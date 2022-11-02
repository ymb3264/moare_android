package kr.moare.android.view.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import kr.moare.android.entities.BottomSheet
import kr.moare.android.entities.SelectedMedia
import kr.moare.android.viewmodel.common.GalleryViewModel
import kr.moare.android.viewmodel.post.PostCreateViewModel

@Composable
fun GalleryView(
    bottomSheet: BottomSheet,
    galleryVM: GalleryViewModel,
//    PostCreateVM: PostCreateViewModel
) {
    val attachments by galleryVM.attachments.collectAsState()
    val selectedMediaList by galleryVM.selectedMediaList.collectAsState()

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
            itemsIndexed(attachments) { index, attachment ->
                val selectedMedia = SelectedMedia(attachment.uri!!, attachment.type!!)

                Box(
                    modifier = Modifier
                        .clip(RectangleShape)
                        .background(Color.Transparent)
                        .aspectRatio(1f),
                    contentAlignment = Alignment.TopEnd
                ) {
                    AsyncImage(
                        model = attachment.uri,
                        contentDescription = "image",
                        modifier = Modifier
                            .fillMaxSize()
                            .clickable {
//                                postVM.selectMediaItems(index, attachment.uri!!)
                                galleryVM.selectMediaItems(index, attachment)
                            },
                        contentScale = ContentScale.Crop
                    )

                    if (attachment.type == "video") {
                        Text(
                            text = "${attachment.videoLength}",
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
                            Text("${selectedMediaList.indexOf(selectedMedia)+1}", color = Color.White)
                        }
                    }
                }
            }
        }
    }
}