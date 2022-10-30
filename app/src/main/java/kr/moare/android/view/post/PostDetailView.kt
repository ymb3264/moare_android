package kr.moare.android.view.post

import android.content.Intent
import android.net.Uri
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
import coil.compose.AsyncImage
import kr.moare.android.R
import kr.moare.android.utils.innerShadow
import kr.moare.android.entities.Post
import kr.moare.android.viewmodel.post.PostViewModel
import com.google.accompanist.pager.*

@OptIn(ExperimentalPagerApi::class)
@Composable
fun PostDetailView(
    navController: NavController,
    post: Post,
    number: Int?,
    postVM: PostViewModel = hiltViewModel()
) {
    val pagerState = rememberPagerState()
    val coroutinScope = rememberCoroutineScope()

    val image = post.imageRequest
    val video = post.mediaUrl.video

    val contentResolver = LocalContext.current.contentResolver

    val onePost by postVM.onePost.collectAsState()

    // share
    val sendIntent: Intent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, "hi")
        type = "text/plain"
    }
    val shareIntent = Intent.createChooser(sendIntent, null)
    val context = LocalContext.current

    number?.let {
        postVM.getPost(number)
//        post = onePost
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomStart
    ) {
        HorizontalPager(
            count = if (image != null) image.size + video.size else video.size,
            state = pagerState,
            modifier = Modifier
        ) { page ->
//                if (page == 0) {
//                    VideoPlayer(uri = Uri.parse("http://www.exit109.com/~dnn/clips/RW20seconds_1.mp4"))
//                } else {
            for (elements in video) {
                if (page+1 == elements.num) {
                    VideoPlayer(Uri.parse(elements.url))
                }
            }

            if (image != null) {
                for (elements in post.mediaUrl.image) {
                    if (page+1 == elements.num) {
//                        Image(
//                            painter = rememberImagePainter(data = mediaUriList[page]),
//                            contentDescription = "image",
//                            modifier = Modifier
//                                .fillMaxSize()
//                                .innerShadow(
//                                    blur = 20.dp,
//                                    color = Color.Black.copy(0.7f),
//                                    offsetX = 60.dp,
//                                    offsetY = 260.dp,
//                                    spread = 10.dp,
//                                    offset = Offset(0F, 0F),
//                                    size = Size(1300F, 2400f)
//                                ),
//                            contentScale = ContentScale.Fit
//                        )
                        AsyncImage(
                            model = image[page],
                            contentDescription = "image",
                            contentScale = ContentScale.Fit,
                            modifier = Modifier
                                .fillMaxSize()
                                .innerShadow(
                                    blur = 20.dp,
                                    color = Color.Black.copy(0.7f),
                                    offsetX = 60.dp,
                                    offsetY = 260.dp,
                                    spread = 10.dp,
                                    offset = Offset(0F, 0F),
                                    size = Size(1300F, 2400f)
                                ),
                        )
                    }
                }
            }
//                }
        }
        Column(
            modifier = Modifier.padding(horizontal = 10.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 5.dp)
            ) {
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .size(40.dp)
                        .background(MaterialTheme.colors.primary)
                )
                Text(text = "moare", color = Color.White, modifier = Modifier.padding(start = 10.dp))

                Spacer(modifier = Modifier.weight(1f))

                HorizontalPagerIndicator(
                    pagerState = pagerState,
                    modifier = Modifier
                        .padding(16.dp),
                    activeColor = MaterialTheme.colors.primary,
                    inactiveColor = Color.White.copy(0.8f)
                )

                Spacer(modifier = Modifier.weight(1f))

                Icon(
                    painter = painterResource(id = R.drawable.ic_like),
                    contentDescription = "like",
                    modifier = Modifier.padding(end = 10.dp),
                    tint = Color.White
                )
                Icon(
                    painter = painterResource(id = R.drawable.ic_comment),
                    contentDescription = "comment",
                    modifier = Modifier.padding(end = 10.dp),
                    tint = Color.White
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

            Text(text = "test", color = Color.White)
            Text(text = "test", color = Color.White)
            Text(text = "test", color = Color.White)
            Text(text = "test", color = Color.White, modifier = Modifier.padding(bottom = 10.dp))
        }
    }
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
//        PostDetailView(rememberNavController())
//    }
//}