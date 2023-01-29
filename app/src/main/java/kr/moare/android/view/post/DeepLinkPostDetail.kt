package kr.moare.android.view.post

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.HorizontalPagerIndicator
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kr.moare.android.R
import kr.moare.android.entities.Post
import kr.moare.android.utils.*
import kr.moare.android.viewmodel.post.DeepLinkPostDetailViewModel
import kr.moare.android.viewmodel.post.PostViewModel

@OptIn(ExperimentalPagerApi::class)
@Composable
fun DeepLinkPostDetailView(
    navController: NavController,
    postVM: DeepLinkPostDetailViewModel = hiltViewModel()
) {
    val pagerState = rememberPagerState()
    val coroutineScope = rememberCoroutineScope()

    val post by postVM.post.collectAsState()

    //...더보기
    val configuration = LocalConfiguration.current
    val localDensity = LocalDensity.current
    val screenHeight = configuration.screenHeightDp
    var overflowed by remember { mutableStateOf(false) }
    var moreContent by remember { mutableStateOf(false) }
    var contentHeight by remember { mutableStateOf(0.dp) }
    var dropMenuExpanded by remember { mutableStateOf(false) }

    // 좋아요 즉각반응용 변수
    var postLike by remember { mutableStateOf(post.like) }

    // share
    val sendIntent: Intent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, "https://moare.kr/post/one?yearAndMonth=${post.yearAndMonth}&postCreatedAt=${post.postCreatedAt}")
        type = "text/plain"
    }
    val shareIntent = Intent.createChooser(sendIntent, null)
    val context = LocalContext.current

    var playButtonPresented by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier.fillMaxSize(),
    ) {
        HorizontalPager(
            count = post.mediaObj.size,
            state = pagerState,
            modifier = Modifier
        ) { page ->
            if (post.mediaObj[page].type == "image") {
                AsyncImage(
                    model = post.mediaObj[page].url,
                    contentDescription = "image",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxSize()
                        .innerShadow(
                            blur = 20.dp,
                            color = Color.Black.copy(0.7f),
                            offsetX = 60.dp,
                            offsetY = contentHeight + 100.dp,
                            spread = 10.dp,
                            offset = Offset(0F, 0F),
                            size = Size(1300F, 2400F)
                        ),
                )
            } else {
                var isPaused by remember { mutableStateOf(false) }

                VideoPlayer(uri = Uri.parse(post.mediaObj[page].url), isPaused)
                Box(
                    modifier = Modifier
                        .clip(RectangleShape)
                        .fillMaxSize()
                        .innerShadow(
                            blur = 20.dp,
                            color = Color.Black.copy(0.7f),
                            offsetX = 60.dp,
                            offsetY = contentHeight + 100.dp,
                            spread = 10.dp,
                            offset = Offset(0F, 0F),
                            size = Size(1300F, 2400F)
                        )
                        .noRippleClickable {
                            coroutineScope.launch {
                                isPaused = !isPaused
                                playButtonPresented = !playButtonPresented

                                delay(500)
                                playButtonPresented = !playButtonPresented
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    AnimatedVisibility(
                        visible = playButtonPresented,
                        exit = fadeOut(animationSpec = tween(400, easing = FastOutLinearInEasing)),
                        enter = fadeIn(animationSpec = tween(100, easing = LinearEasing))
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .size(50.dp)
                                .background(Color.Black.copy(0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isPaused) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_pause),
                                    contentDescription = "pause",
                                    modifier = Modifier.size(30.dp),
                                    tint = Color.White
                                )
                            } else {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_play_arrow),
                                    contentDescription = "play",
                                    modifier = Modifier.size(30.dp),
                                    tint = Color.White
                                )
                            }
                        }
                    }
                }
            }
        } // pager

        Column(
            modifier = Modifier.padding(horizontal = 12.dp)
        ) {
            Row() {
                Spacer(modifier = Modifier.weight(1f))

                Column() {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color.Black.copy(0.2f))
                            .size(30.dp)
                            .clickable { dropMenuExpanded = true }
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                Modifier
                                    .clip(CircleShape)
                                    .size(4.dp)
                                    .background(Color.White)
                            )
                            Box(
                                Modifier
                                    .padding(vertical = 2.dp)
                                    .clip(CircleShape)
                                    .size(4.dp)
                                    .background(Color.White)
                            )
                            Box(
                                Modifier
                                    .clip(CircleShape)
                                    .size(4.dp)
                                    .background(Color.White)
                            )
                        }
                    }

                    DropdownMenu(
                        expanded = dropMenuExpanded,
                        onDismissRequest = { dropMenuExpanded = false }
                    ) {
                        DropdownMenuItem(onClick = {
                            dropMenuExpanded = false
                            postVM.reportPost(post) {
//                                alert
                            }
                        }) {
                            Text(StringResources.report)
                        }
                    }
                }
            }

            Spacer(
                modifier = Modifier
                    .weight(1f)
                    .onGloballyPositioned { coordinates ->
                        val height = with(localDensity) { coordinates.size.height.toDp() }
                        contentHeight = screenHeight.dp - height
                    }
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .size(36.dp)
                    ) {
                        if (post.profileImage.isEmpty()) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_person),
                                contentDescription = "",
                                tint = Color.Gray,
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(Color.LightGray),
                            )
                        } else {
                            AsyncImage(
                                model = post.profileImage,
                                contentDescription = "image",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Fit
                            )
                        }
                    }
                    Text(
                        text = post.username,
                        color = Color.White,
                        maxLines = 1,
                        modifier = Modifier.padding(start = 10.dp)
                    )
                }

                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.Center
                ) {
                    HorizontalPagerIndicator(
                        pagerState = pagerState,
                        modifier = Modifier
                            .padding(16.dp),
                        activeColor = MaterialTheme.colors.primary,
                        inactiveColor = Color.White.copy(0.8f)
                    )
                }


                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.End
                ) {
                    Icon(
                        painter = painterResource(id =
                        if (post.like?.contains(postVM.username) == true) R.drawable.ic_heart_filled else R.drawable.ic_heart
                        ),
                        contentDescription = "like",
                        tint = if (post.like?.contains(postVM.username) == true) MaterialTheme.colors.primary else Color.White,
                        modifier = Modifier
                            .padding(end = 10.dp)
                            .clickable {
                                if (postLike.contains(postVM.username)) {
                                    postVM.unlike()

                                    val newLikeList = postLike.toMutableList()
                                    newLikeList.remove(postVM.username)
                                    postLike = newLikeList
                                } else {
                                    postVM.like()

                                    val newLikeList = postLike.toMutableList()
                                    newLikeList.add(postVM.username)
                                    postLike = newLikeList
                                }
                            }
                    )
                    Icon(
                        painter = painterResource(id = R.drawable.ic_share),
                        contentDescription = "share",
                        tint = Color.White,
                        modifier = Modifier
                            .clickable {
                                context.startActivity(shareIntent)
                            }
                    )
                }
            }

            Row(
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                Text(
                    text = post.sportHashtag.joinToString(" "),
                    color = MaterialTheme.colors.primary,
                    style = MaterialTheme.typography.body2,
                    maxLines = 1,
                )
                Spacer(modifier = Modifier.weight(1f))
                post.like?.let {
                    Text(
                        text = "좋아요 ${it.size}개",
                        color = Color.White,
                        style = MaterialTheme.typography.body2
                    )
                }
            }

            Row(
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .weight(0.8f)
                        .clickable { moreContent = !moreContent }
                ) {
                    Text(
                        text = post.content,
                        style = MaterialTheme.typography.body2,
                        color = Color.White,
                        maxLines = if (moreContent) Int.MAX_VALUE else 1,
                        overflow = TextOverflow.Clip,
                        modifier = Modifier.weight(0.8f, false),
                        onTextLayout = {
                            overflowed = it.hasVisualOverflow
                        }
                    )

                    if (overflowed) {
                        Text(
                            text = " ...더보기",
                            modifier = Modifier.weight(0.2f),
                            style = MaterialTheme.typography.caption
                        )
                    }
                }

                Text(
                    text = if (post.place.isNotEmpty()) {
                        post.place.split(" ")[post.place.split(" ").lastIndex - 1]
                    } else "",
                    style = MaterialTheme.typography.body2,
                    color = Color.White,
                    maxLines = 1,
                    modifier = Modifier.weight(0.2f),
                    textAlign = TextAlign.Right
                )

                Text(
                    text = " · ",
                    style = MaterialTheme.typography.body2,
                    color = Color.White,
                )

                Text(
                    text = DateHelper.getDays(post.postCreatedAt),
                    style = MaterialTheme.typography.body2,
                    color = Color.White,
                )
            }
        } // column
    } // box
}