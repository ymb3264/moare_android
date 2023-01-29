package kr.moare.android.view.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import kr.moare.android.R
import kr.moare.android.components.*
import kr.moare.android.entities.BottomSheet
import kr.moare.android.ui.theme.MoareTheme
import kr.moare.android.utils.*
import kr.moare.android.viewmodel.profile.MyProfileViewModel

@Composable
fun MyProfileView(
    mainNavController: NavController,
    myProfileNavController: NavController,
    bottomSheet: BottomSheet,
    profileVM: MyProfileViewModel,
) {
    val profile by profileVM.myProfile.collectAsState()
//    val encodedProfile by profileVM.profileFlow.collectAsState("")
//    val decodedProfile  = if (encodedProfile.isNotEmpty()) Json.decodeFromString<Profile>(encodedProfile) else null
    val postList by profileVM.postsList.collectAsState()

    val listState = rememberLazyListState()
    val scrollingUp = listState.isScrollingUp()
    var offset by remember { mutableStateOf(0) }
    var newOffset by remember { mutableStateOf(0) }

    var overflowed by remember { mutableStateOf(false)}
    var moreContent by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    TextButton(
                        onClick = {
                            profileVM.collectAccounts()
                            bottomSheet.mainOpenSheet(MainCurrentBottomSheet.MyAccounts)
                        },
                        colors = ButtonDefaults.textButtonColors(contentColor = Color.Black)
                    ) {
                        Text(text = profile.username, style = MaterialTheme.typography.subtitle1)
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
                                myProfileNavController.navigate(MyProfileNavItem.SETTINGS.name)
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
                    .padding(horizontal = 10.dp)
                    .padding(bottom = 12.dp),
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
                            text = if (profile.teamOrMember != null) profile.teamOrMember?.size.toString() else "0",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Normal
                        )
                        Text(text = if (profile.isTeam) StringResources.member else StringResources.team, fontSize = 16.sp, fontWeight = FontWeight.Normal)
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
                            text = if (profile.follower != null) profile.follower?.size.toString() else "0",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Normal
                        )
                        Text(text = StringResources.follower, fontSize = 16.sp, fontWeight = FontWeight.Normal)
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
                ProfileButton(
                    modifier = Modifier,
                    text = if (profile.isTeam) StringResources.message else StringResources.teamProfileCreateButton
                ) {
                    if (profile.isTeam) {
                        mainNavController.navigate(MainNavItem.MESSAGELIST.name)
                    } else {
                        bottomSheet.mainOpenSheet(MainCurrentBottomSheet.CreateTeamProfile)
                    }
                }

                Spacer(Modifier.weight(0.1f))

                ProfileButton(
                    modifier = Modifier,
                    text = StringResources.profileUpdateNavigationTitle
                ) {
                    bottomSheet.mainOpenSheet(MainCurrentBottomSheet.UpdateProfile)
                }
            }

            ProfileDivideLine()

//            if (scrollingUp) {
//                offset = listState.firstVisibleItemScrollOffset
//                if (((offset - newOffset) / 200) > 0) {
//                    postVM.getMorePost()
//                    newOffset = offset
//                }
//            } else {
//                offset = listState.firstVisibleItemScrollOffset
//                newOffset = offset
//            }

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(2.dp),
                state = listState
            ) {
                ProfilePostListView(postList, myProfileNavController)
            }
        }

    } // Scaffold

    if (bottomSheet.mainSheet == MainCurrentBottomSheet.MyAccounts) {
        Box(Modifier
            .fillMaxSize()
            .background(Color.Gray.copy(0.5f))
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) { bottomSheet.mainCloseSheet() }
        )
    }
}

//@Preview(showBackground = true)
//@Composable
//fun MyProfileViewPreview() {
//    MoareTheme {
//        MyProfileView(rememberNavController(), rememberNavController())
//    }
//}