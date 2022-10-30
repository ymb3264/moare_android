package kr.moare.android.view.post

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import kr.moare.android.R
import kr.moare.android.ui.theme.MoareTheme
import kr.moare.android.viewmodel.post.PostAddViewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.HorizontalPagerIndicator
import com.google.accompanist.pager.rememberPagerState
import kr.moare.android.utils.innerShadow

@OptIn(ExperimentalPagerApi::class)
@Composable
fun PostAddDetailView(
    navController: NavController,
    postAddVM: PostAddViewModel,
//    selectedMediaList: Array<SelectedMedia>,
//    content: String,
//    username: String,
//    place: String,
) {
    val pagerState = rememberPagerState()
    val color = listOf<Color>(Color.Blue, Color.Red, Color.Green, Color.Gray)

    var overflowed by remember { mutableStateOf(false)}
    var moreContent by remember { mutableStateOf(false) }
    var contentHeight by remember { mutableStateOf(0.dp) }

    val selectedMediaList by postAddVM.selectedMediaList.collectAsState()

    val placeArr = postAddVM.post.place.split(" ")
    val placeName =  placeArr[placeArr.lastIndex-1]

    val contentResolver = LocalContext.current.contentResolver

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomStart
    ) {
        HorizontalPager(
            count = selectedMediaList.size,
            state = pagerState,
            modifier = Modifier
        ) { page ->
//                if (page == 0) {
//                    VideoPlayer(uri = Uri.parse("http://www.exit109.com/~dnn/clips/RW20seconds_1.mp4"))
//                } else {
            if (selectedMediaList[page].type == "video") {
                VideoPlayer(uri = selectedMediaList[page].uri)
            } else {
                AsyncImage(
                    model = selectedMediaList[page].uri,
                    contentDescription = "image",
                    modifier = Modifier
                        .fillMaxSize()
                        .innerShadow(
                            blur = 20.dp,
                            color = Color.Black.copy(0.7f),
                            offsetX = 60.dp,
                            offsetY = 184.dp + contentHeight,
                            spread = 10.dp,
                            offset = Offset(0F, 0F),
                            size = Size(1300F, 2400f)
                        ),
                    contentScale = ContentScale.Fit
                )
            }
//                }
        }
        Column(
            modifier = Modifier.padding(horizontal = 12.dp)
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
                Text(text = postAddVM.username, color = Color.White, modifier = Modifier.padding(start = 10.dp))

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
                    tint = Color.White
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(bottom = 8.dp)
                    .fillMaxWidth()
            ) {
                Spacer(Modifier.weight(0.5f))
                Text(
                    text = postAddVM.post.sportHashtag.joinToString(" "),
                    style = MaterialTheme.typography.body2,
                    color = MaterialTheme.colors.primary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .fillMaxWidth(),
                    textAlign = TextAlign.Right
                )
            }

            Row(
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .weight(0.6f)
                        .clickable { moreContent = !moreContent }
                ) {
                    Text(
                        text = postAddVM.post.content,
                        style = MaterialTheme.typography.body2,
                        color = Color.White,
                        maxLines = if (moreContent) Int.MAX_VALUE else 1,
                        overflow = TextOverflow.Clip,
                        onTextLayout = {
                            overflowed = it.hasVisualOverflow
                            contentHeight = (it.lineCount * 16).dp
                            Log.d("content", contentHeight.toString())
                        }
                    )

                    if (overflowed) {
                        Text(
                            text = " ...더보기",
                            style = MaterialTheme.typography.caption,
//                            modifier = Modifier.padding(start = 2.dp)
                        )
                    }
                }

                Spacer(Modifier.weight(0.2f))

                Column(
                    Modifier.weight(0.2f)
                ) {
                    Text(
                        text = placeName,
                        style = MaterialTheme.typography.body2,
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .fillMaxWidth(),
                        textAlign = TextAlign.Right
                    )
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun PostAddDetailViewPreview() {
    MoareTheme {
        PostAddDetailView(navController = rememberNavController(), postAddVM = hiltViewModel())
    }
}