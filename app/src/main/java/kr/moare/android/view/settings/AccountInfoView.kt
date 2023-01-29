package kr.moare.android.view.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kr.moare.android.components.ContentTextFieldLine
import kr.moare.android.utils.LoadingNavItem
import kr.moare.android.utils.MyProfileNavItem
import kr.moare.android.utils.StringResources
import kr.moare.android.viewmodel.profile.MyProfileViewModel

@Composable
fun AccountInfoView(
    loadingNavController: NavController,
    myProfileNavController: NavController,
    profileVM: MyProfileViewModel
) {
    val profile by profileVM.myProfile.collectAsState()
    val loading by profileVM.loading.collectAsState()

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(text = StringResources.account)
                    },
                    backgroundColor = Color.White,
                    elevation = 0.dp
                )
            },
        ) {
            Column() {
                Row(
                    modifier = Modifier
                        .padding(horizontal = 12.dp)
                        .padding(bottom = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = StringResources.emailPlaceholder, fontWeight = FontWeight.SemiBold, modifier = Modifier.align(Alignment.Top))

                    Column {
                        Text(
                            text = profile.userID ?: "",
                            fontSize = 14.sp,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                        ContentTextFieldLine(Modifier.padding(top = 6.dp, start = 8.dp))
                    }
                }

                TextButton(
                    onClick = {
                        profileVM.deleteProfile {
                            if (profile.isTeam) {
                                myProfileNavController.popBackStack(MyProfileNavItem.MYPROFILE.name, inclusive = false)
                            } else {
                                profileVM.logout {
                                    loadingNavController.popBackStack()
                                    loadingNavController.navigate(LoadingNavItem.StartLoading.name)
                                }
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp),
                ) {
                    Text(text = if (profile.isTeam) StringResources.deleteTeamProfileButton else StringResources.deleteAccountButton, color = Color.Gray)
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        } // scaffold

        if (loading) {
            CircularProgressIndicator(color = MaterialTheme.colors.primary)
        }
    }

}
