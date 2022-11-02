package kr.moare.android.view.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
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
import kr.moare.android.R
import kr.moare.android.components.*
import kr.moare.android.entities.BottomSheet
import kr.moare.android.utils.*
import kr.moare.android.view.*
import kr.moare.android.viewmodel.profile.ProfileViewModel

@Composable
fun MyProfileView(
    mainNavController: NavController,
    myProfileNavController: NavController,
    bottomSheet: BottomSheet,
    profileVM: ProfileViewModel,
) {
    val profile by profileVM.profile.collectAsState()
    val postList by profileVM.postList.collectAsState()

    val listState = rememberLazyListState()
    val scrollingUp = listState.isScrollingUp()
    var offset by remember { mutableStateOf(0) }
    var newOffset by remember { mutableStateOf(0) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    TextButton(
                        onClick = {
                            profileVM.getMyAccounts()
                            bottomSheet.mainOpenSheet(MainCurrentBottomSheet.MyAccounts)
                        },
                        colors = ButtonDefaults.textButtonColors(contentColor = Color.Black)
                    ) {
                        Text(text = profile.username)
                        Icon(
                            painter = painterResource(id = R.drawable.ic_arrow_down),
                            contentDescription = "myAccounts"
                        )
                    }
                },
                backgroundColor = Color.White,
                elevation = 0.dp,
                actions = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_message),
                        contentDescription = "messageIcon",
                        modifier = Modifier
                            .size(28.dp)
                            .clickable {
                                mainNavController.navigate(MainNavItem.MESSAGELIST.name)
                            },
                    )
                    Icon(
                        painter = painterResource(id = R.drawable.ic_settings),
                        contentDescription = "settingsIcon",
                        modifier = Modifier
                            .padding(start = 10.dp, end = 5.dp)
                            .size(28.dp)
                            .clickable {

                            },
                    )
                },
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .padding(horizontal = 10.dp)
                        .clip(CircleShape)
                        .size(120.dp)
                ) {
                    if (profile.profileImage.isEmpty()) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_person),
                            contentDescription = "",
                            tint = Color.Gray,
                            modifier = Modifier
//                                .clip(CircleShape)
                                .size(120.dp)
                                .background(Color.LightGray),
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Transparent),
                        ) {
                            AsyncImage(
                                model = profile.profileImage,
                                contentDescription = "image",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Fit
                            )
                        }
                    }
                }


                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(text = profile.name)
                    Text(text = profile.content)
                    Text(text = profile.place)
                }
            }

            Text(
                text = profile.sport.joinToString(" "),
                modifier = Modifier
                    .padding(start = 20.dp, end = 10.dp, bottom = 10.dp),
                color = MaterialTheme.colors.primary
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = {
                        myProfileNavController.currentBackStackEntry?.savedStateHandle?.set("profile", profile)
                        myProfileNavController.navigate("${UserProfileNavItem.FOLLOWLIST.name}/${0}")
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = Color.Black
                    )
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(
                            text = profile.teamOrMember.size.toString(),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Normal
                        )
                        Text(text = "team", fontSize = 16.sp, fontWeight = FontWeight.Normal)
                    }
                }
                TextButton(
                    onClick = {
                        myProfileNavController.currentBackStackEntry?.savedStateHandle?.set("profile", profile)
                        myProfileNavController.navigate("${UserProfileNavItem.FOLLOWLIST.name}/${1}")
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = Color.Black
                    )
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(
                            text = profile.follower.size.toString(),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Normal
                        )
                        Text(text = "follower", fontSize = 16.sp, fontWeight = FontWeight.Normal)
                    }
                }
                TextButton(
                    onClick = {
                        myProfileNavController.currentBackStackEntry?.savedStateHandle?.set("profile", profile)
                        myProfileNavController.navigate("${UserProfileNavItem.FOLLOWLIST.name}/${2}")
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = Color.Black
                    )
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(
                            text = profile.following.size.toString(),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Normal
                        )
                        Text(text = "following", fontSize = 16.sp, fontWeight = FontWeight.Normal)
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ProfileButton(modifier = Modifier, text = "팀 프로필 생성") { bottomSheet.mainOpenSheet(MainCurrentBottomSheet.CreateTeamProfile) }
                Spacer(Modifier.weight(0.1f))
                ProfileButton(modifier = Modifier, text = "프로필 편집") { bottomSheet.mainOpenSheet(MainCurrentBottomSheet.UpdateProfile) }
            }

            ProfileDivideLine()

            if (scrollingUp) {
                offset = listState.firstVisibleItemScrollOffset
                if (((offset - newOffset) / 200) > 0) {
//                    postVM.getMorePost()
                    newOffset = offset
                }
            } else {
                offset = listState.firstVisibleItemScrollOffset
                newOffset = offset
            }

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(2.dp),
                state = listState
            ) {
                ProfilePostListView(postList, myProfileNavController)
            }
        }
    }
}

//@Preview(showBackground = true)
//@Composable
//fun MyProfileViewPreview() {
//    MoareTheme {
//        MyProfileView(navController = rememberNavController())
//    }
//}