package kr.moare.android.view.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import coil.compose.AsyncImage
import kr.moare.android.R
import kr.moare.android.entities.BottomSheet
import kr.moare.android.viewmodel.profile.ProfileViewModel

@Composable
fun MyAccountsView(
    bottomSheet: BottomSheet,
    profileVM: ProfileViewModel
) {
    val myAccountsLoading by profileVM.myAccountsLoading.collectAsState()

    if (myAccountsLoading) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 120.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                itemsIndexed(profileVM.myAccounts) { index, account ->
                    TextButton(
                        onClick = {
                            profileVM.changeProfile(account.username)
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
                            fontSize = 16.sp)

                        if (profileVM.profile.value.username == account.username) {
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

                    if (index != profileVM.myAccounts.size - 1) {
                        Box(
                            Modifier
                                .padding(horizontal = 8.dp)
                                .fillMaxWidth()
                                .height(1.dp)
                                .background(Color.LightGray))
                    }
                } // items
            } // LazyColumn
    }
}