package kr.moare.android.view.profile

import android.app.Activity
import android.app.ActivityOptions
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import kotlinx.coroutines.flow.collect
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kr.moare.android.R
import kr.moare.android.components.ProfileButton
import kr.moare.android.components.ProfileButtonLine
import kr.moare.android.entities.Profile
import kr.moare.android.utils.MainNavItem
import kr.moare.android.utils.UserProfileNavItem
import kr.moare.android.utils.isScrollingUp
import kr.moare.android.view.message.MessagesActivity
import kr.moare.android.viewmodel.profile.FollowViewModel
import kr.moare.android.viewmodel.profile.UserProfileViewModel

@Composable
fun UserProfileView(
    mainNavController: NavController,
    subNavController: NavController,
    profileVM: UserProfileViewModel = hiltViewModel(),
    followVM: FollowViewModel = hiltViewModel()
) {
    val profile by profileVM.userProfile.collectAsState()
    val postList by profileVM.postsList.collectAsState()
    val myUsername by profileVM.usernameFlow.collectAsState("")
    val myProfile by profileVM.profileFlow.collectAsState("")
    val accounts by profileVM.accountsFlow.collectAsState(setOf())
    val loading by profileVM.followLoading.collectAsState()

    val accountsUsername = accounts.map { Json.decodeFromString<Profile>(it).username }

    val listState = rememberLazyListState()
    val scrollingUp = listState.isScrollingUp()
    var offset by remember { mutableStateOf(0) }
    var newOffset by remember { mutableStateOf(0) }

    val context = LocalContext.current

    Scaffold(
        topBar = { TopAppBar(title = { Text(text = profile.username) },
            backgroundColor = Color.White,
            elevation = 0.dp)
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
                        .size(100.dp)
                ) {
                    if (profile.profileImage.isEmpty()) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_person),
                            contentDescription = "",
                            tint = Color.Gray,
                            modifier = Modifier
                                .size(120.dp)
                                .background(Color.LightGray),
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxSize(),
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
                        .height(100.dp)
                        .padding(horizontal = 10.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = profile.name,
                        style = MaterialTheme.typography.body2,
                        modifier = Modifier.padding(top = 8.dp)
                    )

                    Text(
                        text = profile.content,
                        style = MaterialTheme.typography.body2
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_place),
                            contentDescription = "place",
                            modifier = Modifier.size(20.dp).padding(end = 4.dp)
                        )
                        Text(
                            text = profile.place,
                            style = MaterialTheme.typography.button,
                            color = Color.DarkGray
                        )
                    }
                }
            }

            profile.sportHashtag?.let {
                Text(
                    text = it.joinToString(" "),
                    modifier = Modifier
                        .padding(start = 20.dp, end = 10.dp, bottom = 10.dp),
                    color = MaterialTheme.colors.primary
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
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
                            text = if (profile.teamOrMember != null) profile.teamOrMember?.size.toString() else "0",
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
                            text = if (profile.follower != null) profile.follower?.size.toString() else "0",
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
                            text = if (profile.following != null) profile.following?.size.toString() else "0",
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
                if (!accountsUsername.contains(profile.username)) {
                    ProfileButton(
                        modifier = Modifier,
                        text = if (profile.follower?.contains(myUsername) == true) "팔로우 취소" else "팔로우",
                        enabled = profile.follower?.contains(myUsername) == false,
                        loading = loading
                    ) {
                        if (profile.follower?.contains(myUsername) == true) {
                            profileVM.unfollow(Json.decodeFromString(myProfile))
                        } else {
                            profileVM.follow(Json.decodeFromString(myProfile))
                        }
                    }
                }
                Spacer(Modifier.weight(0.1f))
                ProfileButton(modifier = Modifier, text = "메시지") {
                    profileVM.createChannel {
                        context.startActivity(
                            MessagesActivity.getIntent(context, "messaging:${profile.username}")
//                            ActivityOptions.makeSceneTransitionAnimation(context as Activity?).toBundle()
                        )
                    }
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