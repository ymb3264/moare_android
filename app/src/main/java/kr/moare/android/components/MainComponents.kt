package kr.moare.android.components

import android.content.Context
import android.net.Uri
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.decode.VideoFrameDecoder
import kr.moare.android.R
import kr.moare.android.entities.Post
import kr.moare.android.utils.innerShadow
import kr.moare.android.utils.PostNavItem

//@Composable
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

@Composable
fun SearchViewButton(
    sport: List<String>?,
    place: String?,
    required: Boolean = true,
    expanded: Boolean = false,
    onClick: () -> Unit,
) {
    Button(onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
            .height(50.dp),
        shape = RectangleShape,
        colors = ButtonDefaults.buttonColors(
            backgroundColor = Color.Transparent
        ),
        elevation = ButtonDefaults.elevation(
            defaultElevation = 0.dp
        ),
        contentPadding = PaddingValues(0.dp)
    ) {
        if (required) {
            Box(
                Modifier
//                    .padding(vertical = 4.dp)
                    .padding(end = 8.dp)
                    .background(MaterialTheme.colors.primary)
                    .width(2.dp)
                    .animateContentSize(tween(500))
                    .height(if (expanded) 50.dp else 5.dp)
                    .align(Alignment.CenterVertically)
            )
        } else {
            Box(
                Modifier
                    .padding(start = 12.dp)
                    .background(Color.Transparent)
                    .size(2.dp))
        }

        sport?.let {
            if (sport.isEmpty()) {
                Text(text = "운동종목", color = Color.Gray)
            } else {
                for (i in 0..sport.lastIndex) {
                    Text(text = sport[i],
                        color = Color.Black)
                    if (i != sport.lastIndex) {
                        TextDivideLine()
                    }
                }
//                sport.forEachIndexed { index, sport ->
//                    Text(text = sport,
//                        color = Color.Black)
//                    if (index != sport.lastIndex) {
//                        TextDivideLine()
//                    }
//                }
            }
        }

        place?.let {
            if (place.isEmpty()) {
                Text(text = "장소", color = Color.Gray)
            } else {
                Text(text = place, color = Color.Black)
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Icon(
            painter = painterResource(R.drawable.ic_arrow_right),
            contentDescription = "arrowRight",
            tint = Color.Gray,
            modifier = Modifier
//                .padding(end = 6.dp)
                .size(20.dp),
        )
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun CustomPlainTextField1(
    modifier: Modifier = Modifier,
    placeholder: String,
    text: String,
    expandedHeight: Dp = 36.dp,
    required: Boolean = true,
    expanded: Boolean = false,
    readOnly: Boolean = false,
    onTextChange: (String) -> Unit
) {
    val focusRequester by remember { mutableStateOf(FocusRequester()) }
    var isFocused by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier.fillMaxWidth().height(36.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (required) {
            Box(
                Modifier
                    .padding(start = 12.dp)
//                    .padding(top = if (expanded) 0.dp else 18.dp)
                    .background(MaterialTheme.colors.primary)
                    .width(2.dp)
                    .animateContentSize(tween(500))
                    .height(if (expanded) expandedHeight else 5.dp)
                    .align(Alignment.CenterVertically)
            )
        } else {
            Box(
                Modifier
                    .padding(start = 12.dp)
                    .background(Color.Transparent)
                    .size(2.dp))
        }

        BasicTextField(
            value = text,
            onValueChange = onTextChange,
            modifier = modifier
                .padding(start = 8.dp, end = 12.dp)
                .fillMaxWidth()
                .weight(1f)
                .focusRequester(focusRequester = focusRequester)
                .onFocusChanged { isFocused = it.isFocused }
                .align(Alignment.CenterVertically),
            decorationBox = { innerTextField ->
                if (text.isEmpty() && !isFocused) {
                    Text(
                        text = placeholder,
                        style = MaterialTheme.typography.button,
                        color = Color.Gray,
                        modifier = Modifier.align(Alignment.CenterVertically)
                    )
                } else {
                    innerTextField()
                }
            },
            readOnly = readOnly,
            singleLine = true,
        )
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun CustomPlainTextField2(
    modifier: Modifier = Modifier,
    placeholder: String,
    text: String,
    expandedHeight: Dp = 86.dp,
    required: Boolean = true,
    expanded: Boolean = false,
    onTextChange: (String) -> Unit
) {
    val focusRequester by remember { mutableStateOf(FocusRequester()) }
    var isFocused by remember { mutableStateOf(false) }

    Row() {
        if (required) {
            Box(
                Modifier
                    .padding(start = 12.dp)
                    .padding(bottom = 8.dp)
                    .background(MaterialTheme.colors.primary)
                    .width(2.dp)
                    .animateContentSize(tween(500))
                    .height(if (expanded) expandedHeight else 5.dp)
                    .align(Alignment.CenterVertically)
            )
        } else {
            Box(
                Modifier
                    .padding(start = 12.dp)
                    .background(Color.Transparent)
                    .size(2.dp))
        }

        Column(
            Modifier
                .height(94.dp)
                .fillMaxWidth()
        ) {
            ContentTextFieldLine(Modifier.padding(vertical = 8.dp))

            BasicTextField(
                value = text,
                onValueChange = onTextChange,
                modifier = modifier
                    .padding(start = 10.dp, end = 12.dp)
                    .height(60.dp)
                    .fillMaxWidth()
                    .weight(1f)
                    .focusRequester(focusRequester = focusRequester)
                    .onFocusChanged { isFocused = it.isFocused },
                decorationBox = { innerTextField ->
                    if (text.isEmpty() && !isFocused) {
                        Text(
                            text = placeholder,
                            style = MaterialTheme.typography.button,
                            color = Color.Gray
                        )
                    } else {
                        innerTextField()
                    }
                }
            )

            ContentTextFieldLine(Modifier.padding(vertical = 8.dp))
        }
    }
}

@Composable
fun ContentTextFieldLine(modifier: Modifier = Modifier) {
    Box(
        modifier
            .padding(end = 12.dp)
            .background(Color.Gray)
            .fillMaxWidth()
            .height(1.dp)
    )
}

@Composable
fun TextDivideLine() {
    Box(
        Modifier
            .padding(vertical = 16.dp, horizontal = 8.dp)
            .clip(RectangleShape)
            .background(Color.Gray)
            .width(1.dp)
            .fillMaxHeight()
    )
}