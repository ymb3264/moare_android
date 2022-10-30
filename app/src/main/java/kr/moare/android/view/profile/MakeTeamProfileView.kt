package kr.moare.android.view.profile

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kr.moare.android.components.*
import kr.moare.android.entities.BottomSheet
import kr.moare.android.view.common.FindLocationView
import kr.moare.android.entities.TeamProfile
import kr.moare.android.utils.SubCurrentBottomSheet
import kr.moare.android.view.common.ProfileGalleryView
import kr.moare.android.view.common.SportAddView
import kr.moare.android.viewmodel.profile.ProfileViewModel

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MakeTeamProfileView(
    bottomSheet: BottomSheet,
    profileVM: ProfileViewModel
) {
    var host by remember { mutableStateOf("") }
    var teamUsername by remember { mutableStateOf("") }
    var teamName by remember { mutableStateOf("") }
     var introduction by remember { mutableStateOf("") }

    val selectedImage by profileVM.selectedImage.collectAsState()
    val croppedImage by profileVM.croppedImage.collectAsState()

    val sport = profileVM.newTeamProfile.sport
    val place = profileVM.newTeamProfile.place

    val completeBtnEnabled = profileVM.username.isNotEmpty() && teamUsername.isNotEmpty() && teamName.isNotEmpty()

    val teamProfile = TeamProfile("", "", "", "", "",
        "", listOf(), listOf(), listOf(), listOf())

    BottomSheetScaffold(
        scaffoldState = bottomSheet.subSheetScaffoldState,
        topBar = {
            TopAppBar(
                title = { Text(text = "새 팀 프로필") },
                backgroundColor = Color.White,
                elevation = 0.dp,
                actions = {
                    TextButton(
                        onClick = {
                            bottomSheet.mainCloseSheet()
                        },
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text(text = "취소")
                    }
                },
            )
        },
        sheetPeekHeight = 0.dp,
        sheetContent = {
            bottomSheet.subSheet?.let {
                when (it) {
                    SubCurrentBottomSheet.SearchSport -> SportAddView(
                        bottomSheet = bottomSheet,
                        postAddVM = null,
                        profileVM = profileVM,
                    )
                    SubCurrentBottomSheet.FindLocation -> FindLocationView(
                        bottomSheet = bottomSheet,
                        postAddVM = null,
                        profileVM = profileVM
                    )
                    SubCurrentBottomSheet.Gallery -> ProfileGalleryView(
                        bottomSheet = bottomSheet,
                        profileVM = profileVM
                    )
                    SubCurrentBottomSheet.Empty -> EmptyView()
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ProfileImageAddButton(
                croppedImage
            ) {
                bottomSheet.subOpenSheet(SubCurrentBottomSheet.Gallery)
            }

            SearchViewButton(
                sport = sport,
                place = null,
                required = false,
                expanded = sport.isNotEmpty()
            ) {
                bottomSheet.subOpenSheet(SubCurrentBottomSheet.SearchSport)
            }
            SearchViewButton(
                sport = null,
                place = place,
                required = false,
                expanded = place.isNotEmpty()
            ) {
                bottomSheet.subOpenSheet(SubCurrentBottomSheet.FindLocation)
            }

            CustomPlainTextField2(
                modifier = Modifier,
                placeholder = "팀 정보(소개글)",
                text = introduction,
                required = false,
                expanded = introduction.isNotEmpty(),
                onTextChange = {
                    introduction = it
                })

            CustomPlainTextField1(
                modifier = Modifier
                    .padding(vertical = 6.dp),
                placeholder = "생성자(관리자)",
                text = profileVM.username,
                expanded = profileVM.username.isNotEmpty(),
                onTextChange = {},
                readOnly = true
            )
            CustomPlainTextField1(
                modifier = Modifier
                    .padding(vertical = 6.dp),
                placeholder = "팀 사용자 명",
                text = teamUsername,
                expanded = teamUsername.isNotEmpty(),
                onTextChange = {
                    teamUsername = it
                    profileVM.newTeamProfile.username = it
                }
            )
            CustomPlainTextField1(
                modifier = Modifier
                    .padding(vertical = 6.dp),
                placeholder = "팀 명",
                text = teamName,
                expanded = teamName.isNotEmpty(),
                onTextChange = {
                    teamName = it
                    profileVM.newTeamProfile.name = it
                }
            )

            CompleteButton(
                text = "생성",
                enabled = completeBtnEnabled
            ) {
                profileVM.makeTeamProfile(teamProfile)
                bottomSheet.mainCloseSheet()
            }
        }
    }
}

//@OptIn(ExperimentalMaterialApi::class)
//@Preview(showBackground = true)
//@Composable
//fun MakeTeamProfileViewPreview() {
//    MoareTheme {
//        MakeTeamProfileView(bottomSheetScaffoldState = rememberBottomSheetScaffoldState())
//    }
//}