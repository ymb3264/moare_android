package kr.moare.android.components

import android.net.Uri
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import kr.moare.android.entities.Post

@Composable
fun ProfileImageAddButton(
    url: String,
    uri: Uri?,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            backgroundColor = Color.Transparent
        ),
        elevation = ButtonDefaults.elevation(
            defaultElevation = 0.dp
        ),
        modifier = Modifier
            .padding(bottom = 10.dp)
    ) {
        Box(modifier = Modifier
            .clip(CircleShape)
            .size(180.dp)
            .border(
                width = 1.dp,
                color = if (uri != null) Color.Transparent else Color.Gray,
                shape = CircleShape
            ),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .size(70.dp)
                    .border(
                        width = 1.dp,
                        color = Color.Gray,
                        shape = CircleShape
                    )
            )
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = Modifier
                    .clip(RectangleShape)
                    .height(2.dp)
                    .width(54.dp)
                    .background(Color.Transparent))
                Box(modifier = Modifier
                    .clip(RectangleShape)
                    .height(70.dp)
                    .width(36.dp)
                    .background(Color.White))
                Box(modifier = Modifier
                    .clip(RectangleShape)
                    .height(70.dp)
                    .width(35.dp)
                    .background(Color.Transparent))
                Box(modifier = Modifier
                    .clip(RectangleShape)
                    .height(2.dp)
                    .width(55.dp)
                    .background(Color.Gray))
            }

            Text(text = "사진 추가", color = Color.Gray, fontSize = 13.sp)

            if (uri != null || url.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White),
                    contentAlignment = Alignment.BottomStart
                ) {
                    AsyncImage(
                        model = uri ?: url,
                        contentDescription = "image",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit
                    )
                }
            }
        }
    }
}

@Composable
fun ProfileTextField(
    modifier: Modifier = Modifier,
    placeholder: String,
    text: String,
    expandedHeight: Dp = 36.dp,
    required: Boolean = true,
    expanded: Boolean = false,
    readOnly: Boolean = false,
    loading: Boolean = false,
    onTextChange: (String) -> Unit
) {
    val focusRequester by remember { mutableStateOf(FocusRequester()) }
    var isFocused by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(36.dp),
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
                    Row() {
                        innerTextField()
                        if (loading) {
                            Spacer(modifier = Modifier.weight(1f))
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Color.Gray
                            )
                        }
                    }

                }
            },
            readOnly = readOnly,
            singleLine = true,
        )
    }
}

@Composable
fun RowScope.ProfileButton(
    modifier: Modifier,
    text: String,
    enabled: Boolean = true,
    loading: Boolean = false,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            contentColor = MaterialTheme.colors.primary,
            backgroundColor = Color.Transparent,
            disabledBackgroundColor = Color.Transparent,
            disabledContentColor = Color.Gray
        ),
        elevation = ButtonDefaults.elevation(
            defaultElevation = 0.dp
        ),
        modifier = modifier
            .weight(1f),
//        enabled = enabled,
        contentPadding = PaddingValues(0.dp)
    ) {
        ProfileButtonLine(Modifier, enabled)
        Spacer(Modifier.weight(1f))
        if (loading) {
            CircularProgressIndicator()
        } else {
            Text(text = text, color = if (enabled) MaterialTheme.colors.primary else Color.Gray)
        }
        Spacer(Modifier.weight(1f))
        ProfileButtonLine(Modifier, enabled)
    }
}

@Composable
fun ProfileButtonLine(
    modifier: Modifier,
    enabled: Boolean
) {
    Box(
        modifier = modifier
            .clip(RectangleShape)
            .size(width = 2.dp, height = 26.dp)
            .background(
                if (enabled) MaterialTheme.colors.primary
                else Color.Gray
            )
    )
}

@Composable
fun ProfileDivideLine() {
    Box(
        Modifier
            .padding(top = 4.dp)
            .height(1.dp)
            .fillMaxWidth()
            .background(Color.LightGray)
            .clip(RectangleShape)
    )
}

fun LazyListScope.ProfilePostListView(
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