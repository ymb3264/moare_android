package kr.moare.android.view.post

import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.FrameLayout
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import kr.moare.android.R
import kr.moare.android.utils.innerShadow
import kr.moare.android.entities.Post
import kr.moare.android.viewmodel.post.PostViewModel
import com.google.accompanist.pager.*
import kr.moare.android.components.StartViewTextField
import kr.moare.android.entities.MediaObj
import kr.moare.android.ui.theme.MoareTheme

@OptIn(ExperimentalPagerApi::class)
@Composable
fun PostDetailView(
    navController: NavController,
    postVM: PostViewModel,
    post: Post,
    listIndex: Int,
    postIndex: Int
) {
    val pagerState = rememberPagerState()

    //...더보기
    var overflowed by remember { mutableStateOf(false)}
    var moreContent by remember { mutableStateOf(false) }
    var contentHeight by remember { mutableStateOf(0.dp) }

    val postList by postVM.postsList.collectAsState()
    val postLike by postVM.postLike.collectAsState()
    val post = postList[listIndex][postIndex]
    postVM.postLike.value = postList[listIndex][postIndex].like ?: listOf()


    // share
    val sendIntent: Intent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, "hi")
        type = "text/plain"
    }
    val shareIntent = Intent.createChooser(sendIntent, null)
    val context = LocalContext.current

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomStart
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
                            offsetY = 220.dp + contentHeight,
                            spread = 10.dp,
                            offset = Offset(0F, 0F),
                            size = Size(1300F, 2400f)
                        ),
                )
            } else {
                VideoPlayer(uri = Uri.parse(post.mediaObj[page].url))
            }
        } // pager

        Column(
            modifier = Modifier.padding(horizontal = 12.dp)
        ) {
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
                            if (postLike.contains(postVM.username)) R.drawable.ic_heart_filled else R.drawable.ic_heart
                        ),
                        contentDescription = "like",
                        tint = if (postLike.contains(postVM.username)) MaterialTheme.colors.primary else Color.White,
                        modifier = Modifier
                            .padding(end = 10.dp)
                            .clickable {
                                if (postLike.contains(postVM.username)) {
                                    if (listIndex != null) {
                                        if (postIndex != null) {
                                            postVM.unlike(listIndex, postIndex, post)
                                        }
                                    }
                                } else {
                                    if (listIndex != null) {
                                        if (postIndex != null) {
                                            postVM.like(listIndex, postIndex, post)
                                        }
                                    }
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
                if (postLike.isNotEmpty()) {
                    Text(
                        text = "좋아요 ${postLike.size}개",
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
                            contentHeight = (it.lineCount * 16).dp
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
            }
        } // column
    } // box
}

@Composable
fun VideoPlayer(uri: Uri) {
    val context = LocalContext.current
    
    val exoPlayer = remember {
        ExoPlayer.Builder(context)
            .build()
            .apply { 
                val defaultDataSourceFactory = DefaultDataSource.Factory(context)
                val dataSourceFactory: DataSource.Factory = DefaultDataSource.Factory(
                    context,
                    defaultDataSourceFactory
                )
                val source = ProgressiveMediaSource.Factory(dataSourceFactory)
                    .createMediaSource(MediaItem.fromUri(uri))
                
                setMediaSource(source)
                prepare()
            }
    }
    
    exoPlayer.playWhenReady = true
    exoPlayer.videoScalingMode = C.VIDEO_SCALING_MODE_SCALE_TO_FIT
//    exoPlayer.repeatMode = Player.REPEAT_MODE_ONE
    exoPlayer.repeatMode = Player.REPEAT_MODE_OFF
    
    DisposableEffect(
        AndroidView(factory = {
            PlayerView(context).apply { 
                hideController()
                useController = false
                resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM
                player = exoPlayer
                layoutParams = FrameLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT)
            }
        })
    ) {
        onDispose { exoPlayer.release() }
    }
}

//@Preview(showBackground = true)
//@Composable
//fun PostDetailViewPreview() {
//    MoareTheme {
//        PostDetailView(rememberNavController(),
//            Post("username", "", "", "",
//                listOf(), "", listOf("#축구"), "김포시 장기동", "", "", listOf("ss")))
//    }
//}