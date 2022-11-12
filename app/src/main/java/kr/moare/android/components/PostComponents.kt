package kr.moare.android.components

import android.content.Context
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.decode.VideoFrameDecoder
import kr.moare.android.R
import kr.moare.android.entities.Post
import kr.moare.android.utils.PostNavItem
import kr.moare.android.utils.innerShadow

// PostView
fun LazyListScope.PostListView(
    postList: List<List<Post>>,
    context: Context,
    subNavController: NavController,
) {

//    if (postList.isNotEmpty()) {
    items(postList) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(2.dp),
            modifier = Modifier.padding(top = 2.dp)
        ) {
            PostListItemView(subNavController = subNavController, post = it[0])
            if (it.count() > 1) {
                PostListItemView(subNavController = subNavController, post = it[1])
            } else {
                EmptyPostView()
            }
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(2.dp),
            modifier = Modifier.padding(top = 2.dp)
        ) {
            if (it.count() > 2) {
                PostListItemView(subNavController = subNavController, post = it[2])
                if (it.count() > 3) {
                    PostListItemView(subNavController = subNavController, post = it[3])
                } else {
                    EmptyPostView()
                }
            }
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(2.dp),
            modifier = Modifier.padding(top = 2.dp)
        ) {
            if (it.count() > 4) {
                PostListItemView(subNavController = subNavController, post = it[4])
                if (it.count() > 5) {
                    PostListItemView(subNavController = subNavController, post = it[5])
                } else {
                    EmptyPostView()
                }
            }
        }

//        Row(
//            horizontalArrangement = Arrangement.spacedBy(2.dp),
//            modifier = Modifier.padding(top = 2.dp)
//        ) {
//            Column(
//                verticalArrangement = Arrangement.spacedBy(2.dp),
//                modifier = Modifier.weight(1f)
//            ) {
//                Box(
//                    modifier = Modifier
//                        .background(Color.Gray)
//                        .fillMaxWidth()
//                        .aspectRatio(1f)
//                        .clickable {
//                            subNavController.currentBackStackEntry?.savedStateHandle?.set(
//                                "post",
//                                it[2]
//                            )
//                            subNavController.navigate(PostNavItem.POSTDETAIL.name)
//                        },
//                    contentAlignment = Alignment.BottomStart
//                ) {
//                    if (it.count() > 2) {
//                        AsyncImage(
//                            model = it[2].imageRequest?.get(0),
//                            placeholder = painterResource(R.drawable.ic_search),
//                            contentDescription = "image",
//                            contentScale = ContentScale.Crop,
//                            modifier = Modifier.clip(RectangleShape)
//                        )
//                    }
//                    PostListItemView()
//                }
//                Box(
//                    modifier = Modifier
//                        .background(Color.Gray)
//                        .fillMaxWidth()
//                        .aspectRatio(1f)
//                        .clickable {
//                            subNavController.currentBackStackEntry?.savedStateHandle?.set(
//                                "post",
//                                it[3]
//                            )
//                            subNavController.navigate(PostNavItem.POSTDETAIL.name)
//                        },
//                    contentAlignment = Alignment.BottomStart
//                ) {
//                    if (it.count() > 3) {
//                        AsyncImage(
//                            model = it[3].imageRequest?.get(0),
//                            placeholder = painterResource(R.drawable.ic_search),
//                            contentDescription = "image",
//                            contentScale = ContentScale.Crop,
//                            modifier = Modifier.clip(RectangleShape)
//                        )
//                    }
//                    PostListItemView()
//                }
//            }
//            BoxWithConstraints(
//                modifier = Modifier.weight(1f)
//            ) {
//                val width = this.maxWidth
//                Box(
//                    modifier = Modifier
//                        .size(width = width, height = width * 2 + 2.dp)
//                        .background(Color.Gray)
//                        .clickable {
//                            subNavController.currentBackStackEntry?.savedStateHandle?.set(
//                                "post",
//                                it[4]
//                            )
//                            subNavController.navigate(PostNavItem.POSTDETAIL.name)
//                        }
//                ) {
//                    VideoPlayer(Uri.parse(it[4].mediaUrl.video[0].url))
//                }
//            }
//        }
    }
//    }
}

@Composable
fun RowScope.PostListItemView(
    subNavController: NavController,
    post: Post
) {
    Box(
        modifier = Modifier
            .background(Color.Transparent)
            .weight(1f)
            .aspectRatio(0.5625f)
            .clickable {
                subNavController.currentBackStackEntry?.savedStateHandle?.set(
                    "post",
                    post
                )
                subNavController.navigate(PostNavItem.POSTDETAIL.name+"/0")
            },
        contentAlignment = Alignment.BottomStart
    ) {
        AsyncImage(
            model = post.imageRequest?.get(0),
            placeholder = painterResource(R.drawable.ic_search),
            contentDescription = "image",
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .clip(RectangleShape)
                .align(Alignment.Center)

        )
        PostListItemShadowView(post)
    }
}

@Composable
fun PostListItemShadowView(post: Post) {
    Box(
        modifier = Modifier
            .clip(RectangleShape)
            .fillMaxSize()
            .innerShadow(
                blur = 20.dp,
                color = Color.Black.copy(0.6f),
                offsetX = 30.dp,
                offsetY = 80.dp,
                spread = 10.dp,
                offset = Offset(0F, 0F),
                size = Size(700F, 1100F)
            )
    )
    Row(
        modifier = Modifier
            .padding(horizontal = 5.dp)
            .padding(bottom = 5.dp)
    ) {
        Text(
            text = post.content,
            color = Color.White,
            style = MaterialTheme.typography.body2,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(0.65f)
        )
        Spacer(modifier = Modifier.weight(0.1f))
        Text(
            text = post.username,
            color = Color.White,
            style = MaterialTheme.typography.body2,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(0.25f),
            textAlign = TextAlign.Right
        )
    }
}

@Composable
fun RowScope.EmptyPostView() {
    Box(
        modifier = Modifier
            .background(Color.Transparent)
            .weight(1f)
            .aspectRatio(0.5625f),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "게시물이 더이상 없습니다.",
            style = MaterialTheme.typography.body1,
            color = Color.Gray
        )
    }
}


// PostCreateView
@Composable
fun BoxScope.PhotoPickerView(
    description: String,
    content: String?,
    username: String?,
    uri: Uri?,
    isPreview: Boolean = false,
    isVideo: Boolean = false,
    onClick: () -> Unit,
) {
    val imageLoader = ImageLoader.Builder(LocalContext.current)
        .components {
            add(VideoFrameDecoder.Factory())
        }
        .build()

    Button(
        onClick = onClick,
        contentPadding = PaddingValues(0.dp),
        shape = RectangleShape,
        colors = ButtonDefaults.buttonColors(backgroundColor = Color.Transparent),
        elevation = ButtonDefaults.elevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                PhotoPickerViewDot()
                PhotoPickerViewDot()
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                PhotoPickerViewDot()
                PhotoPickerViewDot()
            }
        }
    }

    Text(text = description, color = Color.LightGray)

    if (uri != null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White),
            contentAlignment = Alignment.BottomStart
        ) {
            if (isPreview) {
                AsyncImage(
                    model = uri,
                    contentDescription = "image",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit
                )
                content?.let {
                    username?.let {
                        PhotoPickerShadowView(content, username)
                    }
                }
            } else {
                AsyncImage(
                    model = uri,
                    contentDescription = "image",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit
                )
            }
        }
    }
}

@Composable
fun PhotoPickerViewDot() {
    Box(modifier = Modifier
        .clip(CircleShape)
        .size(5.dp)
        .background(Color.LightGray))
}

@Composable
fun PhotoPickerShadowView(
    content: String,
    username: String
) {
    Box(
        modifier = Modifier
            .clip(RectangleShape)
            .fillMaxSize()
            .innerShadow(
                blur = 20.dp,
                color = Color.Black.copy(0.6f),
                offsetX = 30.dp,
                offsetY = 85.dp,
                spread = 10.dp,
                offset = Offset(0F, 0F),
                size = Size(700F, 1050F)
            )
    )
    Row(
        modifier = Modifier
            .padding(horizontal = 5.dp)
            .padding(bottom = 5.dp)
    ) {
        Text(
            text = content,
            color = Color.White,
            style = MaterialTheme.typography.body2,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(0.65f)
        )
        Spacer(modifier = Modifier.weight(0.1f))
        Text(
            text = username,
            color = Color.White,
            style = MaterialTheme.typography.body2,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(0.25f),
            textAlign = TextAlign.Right
        )
    }
}