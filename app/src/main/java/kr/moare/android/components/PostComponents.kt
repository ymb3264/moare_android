package kr.moare.android.components

import android.content.Context
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
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
import kotlinx.coroutines.flow.MutableStateFlow
import kr.moare.android.R
import kr.moare.android.entities.Post
import kr.moare.android.utils.PostNavItem
import kr.moare.android.utils.StringResources
import kr.moare.android.utils.VideoPlayer
import kr.moare.android.utils.innerShadow
import kr.moare.android.viewmodel.post.PostViewModel

// PostView
fun LazyListScope.PostListView(
    postList: List<List<Post>>,
    subNavController: NavController
) {
    itemsIndexed(postList) { index, list ->
        Row(
            horizontalArrangement = Arrangement.spacedBy(2.dp),
            modifier = Modifier.padding(top = 2.dp)
        ) {
            if (list.isNotEmpty()) {
                PostListItemView(subNavController = subNavController, post = list[0], listIndex = index, postIndex = 0)
                if (list.count() > 1) {
                    PostListItemView(subNavController = subNavController, post = list[1], listIndex = index, postIndex = 1)
                } else {
                    EmptyPostView()
                }
            }
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(2.dp),
            modifier = Modifier.padding(top = 2.dp)
        ) {
            if (list.count() > 2) {
                PostListItemView(subNavController = subNavController, post = list[2], listIndex = index, postIndex = 2)
                if (list.count() > 3) {
                    PostListItemView(subNavController = subNavController, post = list[3], listIndex = index, postIndex = 3)
                } else {
                    EmptyPostView()
                }
            }
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(2.dp),
            modifier = Modifier.padding(top = 2.dp)
        ) {
            if (list.count() > 4) {
                PostListItemView(subNavController = subNavController, post = list[4], listIndex = index, postIndex = 4)
                if (list.count() > 5) {
                    PostListItemView(subNavController = subNavController, post = list[5], listIndex = index, postIndex = 5)
                } else {
                    EmptyPostView()
                }
            }
        }
    }
}

@Composable
fun RowScope.PostListItemView(
    subNavController: NavController,
    listIndex: Int,
    postIndex: Int,
    post: Post,
) {
    Box(
        modifier = Modifier
            .background(Color.Transparent)
            .weight(1f)
            .aspectRatio(0.5625f)
            .clickable {
                subNavController.currentBackStackEntry?.savedStateHandle?.set(
                    "listIndex",
                    listIndex
                )
                subNavController.currentBackStackEntry?.savedStateHandle?.set(
                    "postIndex",
                    postIndex
                )
                subNavController.currentBackStackEntry?.savedStateHandle?.set(
                    "post",
                    post
                )
                subNavController.navigate(PostNavItem.POSTDETAIL.name)
            },
    ) {
        if (post.mediaObj.first().type == "image") {
            AsyncImage(
                model = post.mediaObj.first().url,
                placeholder = painterResource(R.drawable.ic_search),
                contentDescription = "image",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .clip(RectangleShape)
                    .align(Alignment.Center)

            )
            PostListItemShadowView(post)
        } else {
            VideoPlayer(uri = Uri.parse(post.mediaObj.first().url))
            PostListItemShadowView(post)
        }
    }
}

@Composable
fun PostListItemShadowView(post: Post) {
    val placeArr = post.place.split(" ")
    val placeName = placeArr[placeArr.lastIndex-1]

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
    Column {
        if (post.mediaObj.size > 1) {
            Row() {
                Spacer(modifier = Modifier.weight(1f))
                Box(
                    Modifier
                        .padding(end = 4.dp, top = 4.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color.DarkGray.copy(0.4f))
                ) {
                    Text(
                        text = "+${post.mediaObj.size}",
                        color = Color.White,
                        modifier = Modifier.padding(4.dp),
                        style = MaterialTheme.typography.caption
                    )
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Row(
            modifier = Modifier
                .padding(horizontal = 4.dp)
                .padding(bottom = 4.dp)
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
                text = placeName,
                color = Color.White,
                style = MaterialTheme.typography.body2,
                maxLines = 1,
                modifier = Modifier.weight(0.25f),
                textAlign = TextAlign.Right
            )
        }
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
            text = StringResources.noMorePost,
            style = MaterialTheme.typography.body1,
            color = Color.Gray
        )
    }
}


// PostCreateView
@Composable
fun BoxScope.PhotoPickerView(
    description: String,
    infoRequired: Boolean = false,
    infoText: String = "",
    content: String?,
    place: String?,
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
    var dropMenuExpanded by remember { mutableStateOf(false) }

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

    Row(verticalAlignment = Alignment.CenterVertically) {
        if (infoRequired) {
            Column() {
                Icon(
                    painter = painterResource(id = R.drawable.ic_info),
                    contentDescription = "info",
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .clickable { dropMenuExpanded = true },
                    tint = Color.Gray
                )

                MaterialTheme(shapes = MaterialTheme.shapes.copy(medium = RoundedCornerShape(20.dp))) {
                    DropdownMenu(
                        expanded = dropMenuExpanded,
                        onDismissRequest = { dropMenuExpanded = false },
                        modifier = Modifier
                            .background(Color.White, RoundedCornerShape(20.dp))
                            .border(BorderStroke(1.dp, Color.Gray), RoundedCornerShape(20.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = infoText,
                            color = Color.Gray,
                            style = MaterialTheme.typography.caption
                        )
                    }
                }
            }
        }

        Text(
            text = description,
            style = MaterialTheme.typography.body2,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )
    }

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
                    imageLoader = imageLoader,
                    contentDescription = "image",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit
                )
                content?.let {
                    PhotoPickerShadowView(content, place ?: "")
                }
            } else {
                AsyncImage(
                    model = uri,
                    imageLoader = imageLoader,
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
    place: String
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
            text = if (place.isNotEmpty()) {
                place.split(" ")[place.split(" ").lastIndex - 1] } else "",
            color = Color.White,
            style = MaterialTheme.typography.body2,
            maxLines = 1,
            modifier = Modifier.weight(0.25f),
            textAlign = TextAlign.Right
        )
    }
}