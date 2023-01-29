package kr.moare.android.view.profile

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import kr.moare.android.R
import kr.moare.android.entities.BottomSheet
import kr.moare.android.utils.MainNavItem
import kr.moare.android.utils.PostNavItem
import kr.moare.android.viewmodel.post.PostViewModel
import kr.moare.android.viewmodel.profile.MyProfileViewModel

@Composable
fun MyAccountsView(
    bottomSheet: BottomSheet,
    profileVM: MyProfileViewModel,
    postVM: PostViewModel,
    postNavController: NavController
) {

    val accounts by profileVM.accounts.collectAsState()

    BackHandler() {
        bottomSheet.mainCloseSheet()
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        itemsIndexed(accounts.toList()) { index, account ->
            TextButton(
                onClick = {
                    profileVM.changeProfile(account.username)
                    postNavController.popBackStack(PostNavItem.POST.name, inclusive = false)
                    postVM.showSearchView.value = false
                    bottomSheet.mainCloseSheet()
                },
                colors = ButtonDefaults.textButtonColors(contentColor = Color.Black),
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    Modifier
                        .padding(end = 8.dp)
                        .clip(CircleShape)
                        .size(40.dp)
                ) {
                    if (account.profileImage.isEmpty()) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_person),
                            contentDescription = "",
                            tint = Color.Gray,
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.LightGray),
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Transparent)
                        ) {
                            AsyncImage(
                                model = account.profileImage,
                                contentDescription = "image",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Fit
                            )
                        }
                    }
                }
                Text(
                    text = account.username,
                    fontSize = 16.sp
                )

                if (profileVM.myProfile.value.username == account.username) {
                    Box(
                        Modifier
                            .padding(start = 12.dp)
                            .clip(CircleShape)
                            .size(10.dp)
                            .background(MaterialTheme.colors.primary)
                    )
                }
                Spacer(Modifier.weight(1f))
            } // TextButton

            if (index != accounts.size - 1) {
                Box(
                    Modifier
                        .padding(horizontal = 8.dp)
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(Color.LightGray)
                )
            }
        } // items
    } // LazyColumn
}