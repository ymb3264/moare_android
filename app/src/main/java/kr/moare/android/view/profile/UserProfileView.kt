package kr.moare.android.view.profile

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import kr.moare.android.R
import kr.moare.android.components.ProfileButton
import kr.moare.android.components.ProfileButtonLine
import kr.moare.android.utils.MainNavItem
import kr.moare.android.utils.UserProfileNavItem
import kr.moare.android.utils.isScrollingUp
import kr.moare.android.viewmodel.profile.FollowViewModel
import kr.moare.android.viewmodel.profile.UserProfileViewModel

@Composable
fun UserProfileView(
    mainNavController: NavController,
    subNavController: NavController,
    profileVM: UserProfileViewModel = hiltViewModel(),
    followVM: FollowViewModel = hiltViewModel()
) {
    val profile by profileVM.profile.collectAsState()
    val postList by profileVM.postList.collectAsState()

    val listState = rememberLazyListState()
    val scrollingUp = listState.isScrollingUp()
    var offset by remember { mutableStateOf(0) }
    var newOffset by remember { mutableStateOf(0) }

    Scaffold(
        topBar = { TopAppBar(title = { Text(text = profile.username) },
            backgroundColor = Color.White,
            elevation = 0.dp)
        }
    ) { padding ->
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
                text = profile.sportHashtag.joinToString(" "),
                modifier = Modifier
                    .padding(start = 20.dp, end = 10.dp, bottom = 20.dp),
                color = MaterialTheme.colors.primary
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = {
                        subNavController.currentBackStackEntry?.savedStateHandle?.set("profile", profile)
                        subNavController.navigate("${UserProfileNavItem.FOLLOWLIST.name}/${0}")
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
                        Text(text = if (profile.isTeam) "member" else "team", fontSize = 16.sp, fontWeight = FontWeight.Normal)
                    }
                }
                TextButton(
                    onClick = {
                        subNavController.currentBackStackEntry?.savedStateHandle?.set("profile", profile)
                        subNavController.navigate("${UserProfileNavItem.FOLLOWLIST.name}/${1}")
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
                        subNavController.currentBackStackEntry?.savedStateHandle?.set("profile", profile)
                        subNavController.navigate("${UserProfileNavItem.FOLLOWLIST.name}/${2}")
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
                ProfileButton(
                    modifier = Modifier,
                    text = "팔로우",
                    enabled = profile.username != profileVM.me && !profile.follower.contains(profileVM.me)
                ) {
                    followVM.follow(profile.username)
                }
                Spacer(Modifier.weight(0.1f))
                ProfileButton(modifier = Modifier, text = "메시지") {
                    mainNavController.navigate(MainNavItem.MESSAGELIST.name)
                }
            }
        }
    }
}

//@Preview(showBackground = true)
//@Composable
//fun UserProfileViewPreview() {
//    MoareTheme {
//        UserProfileView(navController = rememberNavController())
//    }
//}