package kr.moare.android.components

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
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
fun RowScope.ProfileButton(
    modifier: Modifier,
    text: String,
    enabled: Boolean = true,
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
        enabled = enabled,
        contentPadding = PaddingValues(0.dp)
    ) {
        ProfileButtonLine(Modifier, enabled)
        Spacer(Modifier.weight(1f))
        Text(text = text)
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
            .padding(top = 12.dp)
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
    items(postList) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(2.dp),
            modifier = Modifier.padding(top = 2.dp)
        ) {
            PostListItemView(subNavController = subNavController, post = it[0])
            if (it.count() > 2) {
                PostListItemView(subNavController = subNavController, post = it[1])
                PostListItemView(subNavController = subNavController, post = it[2])
            } else if (it.count() > 1){
                PostListItemView(subNavController = subNavController, post = it[1])
                EmptyPostView()
            } else {
                EmptyPostView()
                EmptyPostView()
            }
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(2.dp),
            modifier = Modifier.padding(top = 2.dp)
        ) {
            if (it.count() > 3) {
                PostListItemView(subNavController = subNavController, post = it[3])
                if (it.count() > 5) {
                    PostListItemView(subNavController = subNavController, post = it[4])
                    PostListItemView(subNavController = subNavController, post = it[5])
                } else if (it.count() > 4){
                    PostListItemView(subNavController = subNavController, post = it[4])
                    EmptyPostView()
                } else {
                    EmptyPostView()
                    EmptyPostView()
                }
            }
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(2.dp),
            modifier = Modifier.padding(top = 2.dp)
        ) {
            if (it.count() > 6) {
                PostListItemView(subNavController = subNavController, post = it[6])
                if (it.count() > 8) {
                    PostListItemView(subNavController = subNavController, post = it[7])
                    PostListItemView(subNavController = subNavController, post = it[8])
                } else if (it.count() > 7){
                    PostListItemView(subNavController = subNavController, post = it[7])
                    EmptyPostView()
                } else {
                    EmptyPostView()
                    EmptyPostView()
                }
            }
        }
    }
}