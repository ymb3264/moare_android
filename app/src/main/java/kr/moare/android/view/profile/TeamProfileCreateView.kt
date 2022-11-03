package kr.moare.android.view.profile

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kr.moare.android.components.*
import kr.moare.android.entities.BottomSheet
import kr.moare.android.view.common.FindLocationView
import kr.moare.android.utils.SubCurrentBottomSheet
import kr.moare.android.view.common.ProfileGalleryView
import kr.moare.android.view.common.SportAddView
import kr.moare.android.viewmodel.common.GalleryViewModel
import kr.moare.android.viewmodel.profile.ProfileViewModel
import kr.moare.android.viewmodel.start.JoinViewModel

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun TeamProfileCreateView(
    bottomSheet: BottomSheet,
    profileVM: ProfileViewModel,
    galleryVM: GalleryViewModel = hiltViewModel(),
    joinVM: JoinViewModel = hiltViewModel()
) {
    var teamUsername by remember { mutableStateOf("") }
    var teamName by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }

    val croppedImage by galleryVM.croppedImage.collectAsState()

    val showErrorText by joinVM.showErrorText.collectAsState()
    val showErrorText2 by joinVM.showErrorText2.collectAsState()
    val usernameBtn by joinVM.usernameBtn.collectAsState()

    val errorText1 = "이미 사용중인 이름입니다"
    val errorText2 = "사용자 이름에는 영어 대/소문자, 숫자,\n밑줄(_) 및 마침표(.)만 사용할 수 있습니다."

    val sport = profileVM.newTeamProfile.sportHashtag
    val place = profileVM.newTeamProfile.place

    val completeBtnEnabled = usernameBtn && teamUsername.isNotEmpty() && teamName.isNotEmpty()

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
                        bottomSheet = bottomSheet
                    ) { sport ->
                        profileVM.newTeamProfile.sportHashtag = sport
                    }
                    SubCurrentBottomSheet.FindLocation -> FindLocationView(
                        bottomSheet = bottomSheet,
                        postCreateVM = null,
                        profileVM = profileVM
                    ) { place ->
                        profileVM.newTeamProfile.place = place
                    }
                    SubCurrentBottomSheet.Gallery -> ProfileGalleryView(
                        bottomSheet = bottomSheet,
                        galleryVM = galleryVM
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
            ProfileImageAddButton("", croppedImage) {
                bottomSheet.subOpenSheet(SubCurrentBottomSheet.Gallery)
            }

            SearchViewButton(
                sport = sport,
                place = null,
                required = false
            ) {
                bottomSheet.subOpenSheet(SubCurrentBottomSheet.SearchSport)
            }
            SearchViewButton(
                sport = null,
                place = place,
                required = false
            ) {
                bottomSheet.subOpenSheet(SubCurrentBottomSheet.FindLocation)
            }

            CustomPlainTextField2(
                modifier = Modifier,
                placeholder = "팀 정보(소개글)",
                text = content,
                onTextChange = {
                    content = it
                    profileVM.newTeamProfile.content = it
                },
                required = false
            )

            CustomPlainTextField1(
                modifier = Modifier
                    .padding(vertical = 6.dp),
                placeholder = "생성자(관리자)",
                text = profileVM.username,
                onTextChange = {},
                expanded = profileVM.username.isNotEmpty(),
                readOnly = true
            )

            if (showErrorText || showErrorText2) {
                Text(text = if (showErrorText) errorText2 else errorText1,
                    color = Color.Red,
                    style = MaterialTheme.typography.caption,
                    modifier = Modifier.padding(bottom = 10.dp))
            }

            CustomPlainTextField1(
                modifier = Modifier
                    .padding(vertical = 6.dp),
                placeholder = "팀 사용자 명",
                text = teamUsername,
                onTextChange = {
                    teamUsername = it
                    profileVM.newTeamProfile.username = it
                    joinVM.checkUsername(teamUsername)
                    joinVM.checkUsername2(teamUsername)
                },
                expanded = usernameBtn
            )
            CustomPlainTextField1(
                modifier = Modifier
                    .padding(vertical = 6.dp),
                placeholder = "팀 명",
                text = teamName,
                onTextChange = {
                    teamName = it
                    profileVM.newTeamProfile.name = it
                },
                expanded = teamName.isNotEmpty()
            )

            CompleteButton(text = "생성", enabled = completeBtnEnabled) {
                profileVM.createTeamProfile(croppedImage)
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