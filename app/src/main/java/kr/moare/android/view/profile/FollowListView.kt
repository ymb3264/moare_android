package kr.moare.android.view.profile

import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.google.accompanist.pager.*
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kr.moare.android.R
import kr.moare.android.entities.FollowObj
import kr.moare.android.entities.Profile
import kr.moare.android.utils.UserProfileNavItem

@OptIn(ExperimentalPagerApi::class)
@Composable
fun FollowListView(
    navController: NavController,
    profile: Profile,
    page: Int
) {
    val tabPagerState = rememberPagerState(page)
    val coroutinScope = rememberCoroutineScope()
    val tabs = FollowTab.values().toList()

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            TabRow(
                selectedTabIndex = tabPagerState.currentPage,
                backgroundColor = Color.Transparent,
                indicator = { tabPositions ->
                    val transition = updateTransition(targetState = tabPagerState, label = "")
                    val indicatorLeft by transition.animateDp(label = "") { page ->
                        tabPositions[page.currentPage].left
                    }
                    val indicatorRight by transition.animateDp(label = "") { page ->
                        tabPositions[page.currentPage].right
                    }
                    Box(modifier =
                    Modifier
                        .fillMaxSize()
                        .wrapContentSize(align = Alignment.BottomStart)
                        .offset(x = indicatorLeft)
                        .width(indicatorRight - indicatorLeft)
                        .padding(4.dp)
                        .fillMaxSize()
                        .border(
                            BorderStroke(1.dp, MaterialTheme.colors.primary),
                            RoundedCornerShape(50.dp)
                        )
                    )
                },
                divider = {}
            ) {
                tabs.forEachIndexed { index, tabItem ->
                    Tab(
                        text = { Text(text = tabItem.title, fontSize = 16.sp) },
                        selected = tabPagerState.currentPage == index,
                        selectedContentColor = MaterialTheme.colors.primary,
                        unselectedContentColor = Color.Gray,
                        onClick = {
                            coroutinScope.launch {
                                tabPagerState.animateScrollToPage(index)
                            }
                        }
                    )
                }
            }

            HorizontalPager(
                count = tabs.size,
                state = tabPagerState
            ) { page ->
                when (page) {
                    0 -> FollowList(profile.teamOrMember, navController)
                    1 -> FollowList(profile.follower, navController)
                    else -> FollowList(profile.following, navController)
                }
            }
        }
    }
}

enum class FollowTab(val title: String) {
    TEAM("team"),
    FOLLOWER("follower"),
    FOLLOWING("following")
}

@Composable
fun FollowList(list: List<FollowObj>, navController: NavController) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 10.dp)
            .padding(top = 5.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        items(list) { obj ->
            TextButton(
                onClick = {
                    navController.navigate("${UserProfileNavItem.USERPROFILE.name}/${obj.username}")
                },
                contentPadding = PaddingValues(0.dp),
                colors = ButtonDefaults.textButtonColors(contentColor = Color.Black),
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .size(40.dp)
                ) {
                    if (obj.profileImage.isEmpty()) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_person),
                            contentDescription = "",
                            tint = Color.Gray,
                            modifier = Modifier
                                .size(40.dp)
                                .background(Color.LightGray),
                        )
                    } else {
                        AsyncImage(
                            model = obj.profileImage,
                            contentDescription = "image",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Fit
                        )
                    }
                }

                Text(
                    text = "${obj.username}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Normal,
                    modifier = Modifier.fillMaxWidth().padding(start = 8.dp)
                )
            }
        }
    }
}

//@Preview(showBackground = true)
//@Composable
//fun FollowListViewPreview() {
//    MoareTheme {
//        FollowListView(navController = rememberNavController())
//    }
//}