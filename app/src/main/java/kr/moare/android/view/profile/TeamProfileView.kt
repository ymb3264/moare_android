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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import kr.moare.android.ui.theme.MoareTheme
import kr.moare.android.components.ProfileButton
import kr.moare.android.components.ProfileButtonLine
import kr.moare.android.viewmodel.profile.FollowViewModel
import kr.moare.android.viewmodel.profile.UserProfileViewModel

@Composable
fun TeamProfileView(
    navController: NavController,
    profileVM: UserProfileViewModel = hiltViewModel(),
    followVM: FollowViewModel = hiltViewModel()
) {
    val teamProfile by profileVM.teamProfile.collectAsState()
    val username = teamProfile.username.substringBefore("-t")

    Scaffold(
        topBar = { TopAppBar(title = { Text(text = username) },
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
                    Text(text = teamProfile.name)
                    Text(text = teamProfile.introduction)
                    Text(text = teamProfile.place)
                    Text(text = "www.youtube.com")
                }
            }

            Text(text = teamProfile.sport.joinToString(" "),
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
                    Text(text = teamProfile.member.size.toString())
                    Text(text = "member")
                }
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = teamProfile.follower.size.toString())
                    Text(text = "follower")
                }
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = teamProfile.following.size.toString())
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
                    enabled = if (teamProfile.member.contains(profileVM.host)) false else true
                ) {
                    followVM.follow(teamProfile.username)
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

@Preview(showBackground = true)
@Composable
fun TeamProfileViewPreview() {
    MoareTheme {
        TeamProfileView(navController = rememberNavController())
    }
}