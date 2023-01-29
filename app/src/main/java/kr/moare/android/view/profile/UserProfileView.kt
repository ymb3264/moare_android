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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kr.moare.android.R
import kr.moare.android.components.PostListView
import kr.moare.android.components.ProfileButton
import kr.moare.android.components.ProfileDivideLine
import kr.moare.android.entities.Profile
import kr.moare.android.utils.StringResources
import kr.moare.android.utils.UserProfileNavItem
import kr.moare.android.utils.isScrollingUp
import kr.moare.android.view.message.MessagesActivity
import kr.moare.android.viewmodel.profile.UserProfileViewModel

@Composable
fun UserProfileView(
    mainNavController: NavController,
    subNavController: NavController,
    profileVM: UserProfileViewModel = hiltViewModel()
) {
    var dropMenuExpanded by remember { mutableStateOf(false) }

    val profile by profileVM.userProfile.collectAsState()
    val postList by profileVM.postsList.collectAsState()
    val myUsername by profileVM.usernameFlow.collectAsState("")
    val myProfile by profileVM.myProfile.collectAsState()
    val accounts by profileVM.accountsFlow.collectAsState(setOf())
    val loading by profileVM.followLoading.collectAsState()
    val followButtonEnabled by profileVM.followButtonEnabled.collectAsState()
    val alertTitle by profileVM.alertTitle.collectAsState()
    val alertMessage by profileVM.alertMessage.collectAsState()

    val accountsUsername = accounts.map { Json.decodeFromString<Profile>(it).username }

    val listState = rememberLazyListState()
    val scrollingUp = listState.isScrollingUp()
    var offset by remember { mutableStateOf(0) }
    var newOffset by remember { mutableStateOf(0) }

    var overflowed by remember { mutableStateOf(false)}
    var moreContent by remember { mutableStateOf(false) }

    val context = LocalContext.current

    var unfollowAlert by remember { mutableStateOf(false) }
    var reportUserAlert by remember { mutableStateOf(false) }
    var blockUserAlert by remember { mutableStateOf(false) }
    var reportUserSuccessAlert by remember { mutableStateOf(false) }
    var blockUserSuccessAlert by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = profile.username) },
                backgroundColor = Color.White,
                elevation = 0.dp,
                actions = {
                    Column() {
                        Box(
                            modifier = Modifier
                                .background(Color.Transparent)
                                .size(30.dp)
                                .clickable {
                                    if (!accountsUsername.contains(profile.username)) {
                                        dropMenuExpanded = true
                                    }
                                }
                        ) {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Box(
                                    Modifier
                                        .clip(CircleShape)
                                        .background(Color.Black)
                                        .size(4.dp)
                                )
                                Box(
                                    Modifier
                                        .padding(vertical = 2.dp)
                                        .clip(CircleShape)
                                        .background(Color.Black)
                                        .size(4.dp)
                                )
                                Box(
                                    Modifier
                                        .clip(CircleShape)
                                        .background(Color.Black)
                                        .size(4.dp)
                                )
                            }
                        }

                        DropdownMenu(
                            expanded = dropMenuExpanded,
                            onDismissRequest = { dropMenuExpanded = false }
                        ) {
                            DropdownMenuItem(onClick = {
                                dropMenuExpanded = false
                                blockUserAlert = true
                            }) {
                                Text(StringResources.block)
                            }
                            DropdownMenuItem(onClick = {
                                dropMenuExpanded = false
                                reportUserAlert = true
                            }) {
                                Text(StringResources.report)
                            }
                        }
                    }
                }
            )
        },
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp)
                    .padding(bottom = 12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .size(100.dp)
                        .background(Color.Gray)
                ) {
                    if (profile.profileImage.isEmpty()) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_person),
                            contentDescription = "",
                            tint = Color.Gray,
                            modifier = Modifier
                                .size(100.dp)
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
                        .padding(start = 10.dp),
                ) {
                    Text(
                        text = profile.name,
                        style = MaterialTheme.typography.body2,
                        modifier = Modifier
                            .padding(top = 8.dp, bottom = 4.dp)
                    )

                    Row(
                        modifier = Modifier
                            .padding(bottom = 8.dp)
                            .clickable { moreContent = !moreContent },
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Text(
                            text = profile.content,
                            style = MaterialTheme.typography.body2,
                            maxLines = if (moreContent) Int.MAX_VALUE else 3,
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

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (profile.place.isNotEmpty()) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_place),
                                contentDescription = "place",
                                modifier = Modifier
                                    .size(20.dp)
                                    .padding(end = 4.dp)
                            )
                        }
                        Text(
                            text = profile.place,
                            style = MaterialTheme.typography.button,
                        )
                    }
                }
            }

            profile.sportHashtag?.let {
                Text(
                    text = it.joinToString(" "),
                    modifier = Modifier
                        .padding(start = 20.dp, end = 10.dp, bottom = 4.dp),
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
                        Text(text = if (profile.isTeam) StringResources.member else StringResources.team, fontSize = 16.sp, fontWeight = FontWeight.Normal)
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
                        Text(text = StringResources.follower, fontSize = 16.sp, fontWeight = FontWeight.Normal)
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
                        Text(text = StringResources.following, fontSize = 16.sp, fontWeight = FontWeight.Normal)
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (myProfile.blockedUser != null) {
                    if (myProfile.blockedUser!!.contains(profile.userID!! + "+" + profile.createdAt)) {
                        ProfileButton(text = StringResources.unblock) {

                        }
                    } else {
                        if (!accountsUsername.contains(profile.username)) {
                            ProfileButton(
                                text = if (followButtonEnabled) StringResources.followButton else StringResources.unfollowButton,
                                enabled = followButtonEnabled,
                                loading = loading
                            ) {
                                if (followButtonEnabled) {
                                    profileVM.follow()
                                } else {
                                    profileVM.checkUnfollow {
                                        unfollowAlert = true
                                    }
                                }
                            }
                            Spacer(Modifier.weight(0.1f))
                        }

                        ProfileButton(text = StringResources.message) {
                            profileVM.createChannel {
                                context.startActivity(
                                    MessagesActivity.getIntent(context, it, profile)
//                            ActivityOptions.makeSceneTransitionAnimation(context as Activity?).toBundle()
                                )
                            }
                        }
                    }
                } else {
                    if (!accountsUsername.contains(profile.username)) {
                        ProfileButton(
                            text = if (followButtonEnabled) StringResources.followButton else StringResources.unfollowButton,
                            enabled = followButtonEnabled,
                            loading = loading
                        ) {
                            if (followButtonEnabled) {
                                profileVM.follow()
                            } else {
                                profileVM.checkUnfollow {
                                    unfollowAlert = true
                                }
                            }
                        }
                        Spacer(Modifier.weight(0.1f))
                    }

                    ProfileButton(modifier = Modifier, text = StringResources.message) {
                        profileVM.createChannel {
                            context.startActivity(
                                MessagesActivity.getIntent(context, it, profile)
//                            ActivityOptions.makeSceneTransitionAnimation(context as Activity?).toBundle()
                            )
                        }
                    }
                }
            }

            ProfileDivideLine()

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(2.dp),
                state = listState
            ) {
                PostListView(postList, subNavController)
            }

        } // column

        if (unfollowAlert) {
            AlertDialog(
                onDismissRequest = { unfollowAlert = false },
                confirmButton = {
                    TextButton(onClick = {
                        profileVM.unfollow()
                        unfollowAlert = false
                    }) {
                        Text(text = StringResources.confirm)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { unfollowAlert = false }) {
                        Text(text = StringResources.cancel)
                    }
                },
                title = { Text(text = alertTitle) },
                text = { Text(text = alertMessage) }
            )
        }

        if (blockUserAlert) {
            AlertDialog(
                onDismissRequest = { blockUserAlert = false },
                confirmButton = {
                    TextButton(onClick = {
                        profileVM.blockUser {
                            blockUserAlert = false
                            blockUserSuccessAlert = true
                        }
                    }) {
                        Text(text = StringResources.confirm)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { blockUserAlert = false }) {
                        Text(text = StringResources.cancel)
                    }
                },
                title = { Text(text = StringResources.blockUserAlertTitle) },
                text = { Text(text = StringResources.blockUserAlertMessage) }
            )
        }

        if (reportUserAlert) {
            AlertDialog(
                onDismissRequest = { reportUserAlert = false },
                confirmButton = {
                    TextButton(onClick = {
                        profileVM.reportUser {
                            reportUserAlert = false
                            reportUserSuccessAlert = true
                        }
                    }) {
                        Text(text = StringResources.confirm)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { reportUserAlert = false }) {
                        Text(text = StringResources.cancel)
                    }
                },
                title = { Text(text = StringResources.reportUserAlertTitle) },
                text = { Text(text = StringResources.reportUserAlertMessage) }
            )
        }

        if (blockUserSuccessAlert) {
            AlertDialog(
                onDismissRequest = { blockUserSuccessAlert = false },
                confirmButton = {
                    TextButton(onClick = {
                        blockUserSuccessAlert = false
                    }) {
                        Text(text = StringResources.confirm)
                    }
                },
                title = { Text(text = StringResources.blockUserAlertTitle) },
                text = { Text(text = profile.username + StringResources.blockUserSuccessMessgae) }
            )
        }

        if (reportUserSuccessAlert) {
            AlertDialog(
                onDismissRequest = { reportUserSuccessAlert = false },
                confirmButton = {
                    TextButton(onClick = {
                        reportUserSuccessAlert = false
                    }) {
                        Text(text = StringResources.confirm)
                    }
                },
                title = { Text(text = StringResources.reportUserAlertTitle) },
                text = { Text(text = StringResources.reportSuccessMessgae) }
            )
        }
    } // scaffold
}

//@Preview(showBackground = true)
//@Composable
//fun UserProfileViewPreview() {
//    MoareTheme {
//        UserProfileView(rememberNavController(), rememberNavController())
//    }
//}