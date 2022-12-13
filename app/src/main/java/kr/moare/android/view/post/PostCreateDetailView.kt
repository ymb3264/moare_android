package kr.moare.android.view.post

import android.os.ProxyFileDescriptorCallback
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
import kr.moare.android.viewmodel.post.PostCreateViewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.HorizontalPagerIndicator
import com.google.accompanist.pager.rememberPagerState
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kr.moare.android.entities.Profile
import kr.moare.android.entities.SelectedMedia
import kr.moare.android.utils.innerShadow
import kr.moare.android.viewmodel.common.GalleryViewModel

@OptIn(ExperimentalPagerApi::class)
@Composable
fun PostCreateDetailView(
    navController: NavController,
    postCreateVM: PostCreateViewModel,
//    galleryVM: GalleryViewModel
    selectedMediaList: Array<SelectedMedia>,
//    content: String,
//    username: String,
//    place: String,
) {
    val pagerState = rememberPagerState()

    // 더보기
    var overflowed by remember { mutableStateOf(false)}
    var moreContent by remember { mutableStateOf(false) }
    var contentHeight by remember { mutableStateOf(0.dp) }

    // profileImage param으로 가져오기
    val profile by postCreateVM.profileFlow.collectAsState(initial = "")

//    val selectedMediaList by galleryVM.selectedMediaList.collectAsState()

    val placeArr = postCreateVM.post.place.split(" ")
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
            if (selectedMediaList[page].type == "video") {
                VideoPlayer(uri = selectedMediaList[page].uri)
            } else {
                AsyncImage(
                    model = selectedMediaList[page].uri,
                    contentDescription = "image",
                    contentScale = ContentScale.Fit,
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
                        )
                )
            }
        }

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
                        if (profile.isNotEmpty()) {
                            if (Json.decodeFromString<Profile>(profile).profileImage.isEmpty()) {
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
                                    model = Json.decodeFromString<Profile>(profile).profileImage,
                                    contentDescription = "image",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Fit
                                )
                            }
                        }
                    }
                    Text(
                        text = postCreateVM.username,
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
                        painter = painterResource(id = R.drawable.ic_like),
                        contentDescription = "like",
                        modifier = Modifier.padding(end = 10.dp),
                        tint = Color.White
                    )
                    Icon(
                        painter = painterResource(id = R.drawable.ic_share),
                        contentDescription = "share",
                        tint = Color.White
                    )
                }
            }

            Row(
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                Text(
                    text = postCreateVM.post.sportHashtag.joinToString(" "),
                    style = MaterialTheme.typography.body2,
                    color = MaterialTheme.colors.primary,
                    maxLines = 1
                )
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
                        text = postCreateVM.post.content,
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
                    text = placeName,
                    style = MaterialTheme.typography.body2,
                    color = Color.White,
                    maxLines = 1,
                    modifier = Modifier.weight(0.2f),
                    textAlign = TextAlign.Right
                )
            }
        }
    }
}


//@Preview(showBackground = true)
//@Composable
//fun PostAddDetailViewPreview() {
//    MoareTheme {
//        PostCreateDetailView(navController = rememberNavController(), postCreateVM = hiltViewModel())
//    }
//}