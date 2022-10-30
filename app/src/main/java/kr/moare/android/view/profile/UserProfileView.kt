package kr.moare.android.view.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import kr.moare.android.components.ProfileButton
import kr.moare.android.components.ProfileButtonLine
import kr.moare.android.viewmodel.profile.FollowViewModel
import kr.moare.android.viewmodel.profile.UserProfileViewModel

@Composable
fun UserProfileView(
    navController: NavController,
    profileVM: UserProfileViewModel = hiltViewModel(),
//    profileVM: ProfileViewModel,
    followVM: FollowViewModel = hiltViewModel()
) {
    val profile by profileVM.profile.collectAsState()

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
                Box(modifier = Modifier
                    .padding(horizontal = 10.dp)
                    .clip(CircleShape)
                    .size(120.dp)
                    .background(MaterialTheme.colors.primary)
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(text = profile.name)
                    Text(text = profile.content)
                    Text(text = profile.place)
                    Text(text = "www.youtube.com")
                }
            }

            Text(text = profile.sport.joinToString(" "),
                modifier = Modifier
                    .padding(start = 20.dp, end = 10.dp, bottom = 20.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = profile.teamOrMember.size.toString())
                    Text(text = if (profile.isTeam) "member" else "team")
                }
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = profile.follower.size.toString())
                    Text(text = "follower")
                }
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = profile.following.size.toString())
                    Text(text = "following")
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ProfileButtonLine(Modifier)
                ProfileButton(
                    modifier = Modifier,
                    text = "팔로우",
                    enabled = if (profile.teamOrMember.contains(profileVM.host)) false else true
                ) {
                    followVM.follow(profile.username)
                }
                ProfileButtonLine(Modifier.padding(end = 8.dp))

                ProfileButtonLine(Modifier)
                ProfileButton(modifier = Modifier, text = "메시지") {

                }
                ProfileButtonLine(Modifier)
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