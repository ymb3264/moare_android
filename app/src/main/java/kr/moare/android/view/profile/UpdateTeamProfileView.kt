package kr.moare.android.view.profile

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kr.moare.android.components.CompleteButton
import kr.moare.android.components.CustomPlainTextField1
import kr.moare.android.components.CustomPlainTextField2
import kr.moare.android.components.SearchViewButton
import kr.moare.android.entities.BottomSheet
import kr.moare.android.view.common.FindLocationView
import kr.moare.android.utils.SubCurrentBottomSheet
import kr.moare.android.view.common.GalleryView
import kr.moare.android.view.common.SportAddView
import kr.moare.android.viewmodel.profile.ProfileViewModel

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun UpdateTeamProfileView(
    bottomSheet: BottomSheet,
    profileVM: ProfileViewModel
) {
    var teamUsername by remember { mutableStateOf("") }
    var teamName by remember { mutableStateOf("") }
    var teamIntroduction by remember { mutableStateOf("") }

    BottomSheetScaffold(
        scaffoldState = bottomSheet.subSheetScaffoldState,
        topBar = {
            TopAppBar(
                title = { Text(text = "팀 프로필 편집") },
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
            when (bottomSheet.subSheet) {
                SubCurrentBottomSheet.SearchSport -> SportAddView(
                    bottomSheet = bottomSheet,
                    postAddVM = null,
                    profileVM = profileVM
                )
                SubCurrentBottomSheet.FindLocation -> FindLocationView(
                    bottomSheet = bottomSheet,
                    postAddVM = null,
                    profileVM = profileVM
                )
                SubCurrentBottomSheet.Gallery -> GalleryView(
                    bottomSheet = bottomSheet,
                    hiltViewModel()
                )
                SubCurrentBottomSheet.Empty -> null
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
//            ProfileImageAddButton()

            CustomPlainTextField1(
                modifier = Modifier
                    .padding(horizontal = 10.dp)
                    .padding(bottom = 10.dp),
                placeholder = "팀 사용자 이름",
                text = teamUsername,
                onTextChange = { teamUsername = it })
            CustomPlainTextField1(
                modifier = Modifier
                    .padding(horizontal = 10.dp)
                    .padding(bottom = 10.dp),
                placeholder = "팀 이름",
                text = teamName,
                onTextChange = { teamName = it })

            SearchViewButton(
                sport = listOf(),
                place = null,
                required = false,
            ) {
                bottomSheet.subOpenSheet(SubCurrentBottomSheet.SearchSport)
            }
            SearchViewButton(
                sport = null,
                place = "",
                required = false,
            ) {
                bottomSheet.subOpenSheet(SubCurrentBottomSheet.FindLocation)
            }

                CustomPlainTextField2(
                    modifier = Modifier
                        .padding(horizontal = 10.dp)
                        .padding(bottom = 10.dp),
                    placeholder = "팀 정보(소개글)",
                    required = false,
                    text = teamIntroduction,
                    onTextChange = { teamIntroduction = it })

            CompleteButton(text = "완료") {
            }
        }
    }
}

//@OptIn(ExperimentalMaterialApi::class)
//@Preview(showBackground = true)
//@Composable
//fun UpdateTeamProfileViewPreview() {
//    MoareTheme {
//        UpdateTeamProfileView(rememberBottomSheetScaffoldState())
//    }
//}