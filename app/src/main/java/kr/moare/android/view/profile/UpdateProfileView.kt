package kr.moare.android.view.profile

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.flow.collect
import kr.moare.android.components.*
import kr.moare.android.entities.BottomSheet
import kr.moare.android.view.common.FindLocationView
import kr.moare.android.utils.SubCurrentBottomSheet
import kr.moare.android.view.common.GalleryView
import kr.moare.android.view.common.ProfileGalleryView
import kr.moare.android.view.common.SportAddView
import kr.moare.android.viewmodel.common.GalleryViewModel
import kr.moare.android.viewmodel.profile.ProfileViewModel
import kr.moare.android.viewmodel.start.JoinViewModel

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun UpdateProfileView(
    bottomSheet: BottomSheet,
    profileVM: ProfileViewModel,
    galleryVM: GalleryViewModel = hiltViewModel(),
    joinVM: JoinViewModel = hiltViewModel()
) {
    var username by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }

    val profile by profileVM.newUserProfile.collectAsState()
    val updateLoading by profileVM.updateLoading.collectAsState()

    val croppedImage by galleryVM.croppedImage.collectAsState()

    val showErrorText by joinVM.showErrorText.collectAsState()
    val showErrorText2 by joinVM.showErrorText2.collectAsState()
    val usernameBtn by joinVM.usernameBtn.collectAsState()

    val errorText1 = "이미 사용중인 이름입니다"
    val errorText2 = "사용자 이름에는 영어 대/소문자, 숫자,\n밑줄(_) 및 마침표(.)만 사용할 수 있습니다."

    username = profile.username
    name = profile.name
    content = profile.content

    BottomSheetScaffold(
        scaffoldState = bottomSheet.subSheetScaffoldState,
        topBar = {
            TopAppBar(
                title = { Text(text = "프로필 편집") },
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
                    bottomSheet = bottomSheet
                ) { sport ->
                    profileVM.newUserProfile.value.sportHashtag = sport
                }
                SubCurrentBottomSheet.FindLocation -> FindLocationView(
                    bottomSheet = bottomSheet,
                    postCreateVM = null,
                    profileVM = profileVM
                ) { place ->
                    profileVM.newUserProfile.value.place = place
                }
                SubCurrentBottomSheet.Gallery -> ProfileGalleryView(
                    bottomSheet = bottomSheet,
                    galleryVM = galleryVM
                )
                SubCurrentBottomSheet.Empty -> EmptyView()
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
                url = profile.profileImage,
                uri = null
            ) {
                bottomSheet.subOpenSheet(SubCurrentBottomSheet.Gallery)
            }

            SearchViewButton(
                sport = profile.sportHashtag,
                place = null,
                required = false
            ) {
                bottomSheet.subOpenSheet(SubCurrentBottomSheet.SearchSport)
            }
            SearchViewButton(
                sport = null,
                place = profile.place,
                required = false
//                text = "주 운동 지역"
            ) {
                bottomSheet.subOpenSheet(SubCurrentBottomSheet.FindLocation)
            }

            CustomPlainTextField2(
                modifier = Modifier,
                placeholder = "소개",
                text = content,
                onTextChange = {
                    content = it
                    profileVM.newUserProfile.value.content = it
                },
                required = false
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
                placeholder = "사용자 이름",
                text = username,
                onTextChange = {
                    username = it
                    // stateflow 변수의 참조형 속성은 값을 바꿔도 composable view에 적용되지 않는다. 선언형은 적용된다.
                    profileVM.newUserProfile.value.username = it
                    joinVM.checkUsername(username)
                    if (profileVM.username != username) {
                        joinVM.checkUsername2(username)
                    } else {
                        // 이름이 있는경우에서 바로 원래이름으로 바꿨을때 errortext2가 사라져야한다
                        joinVM.showErrorText2.value = false
                    }
                },
                expanded = usernameBtn
            )
            CustomPlainTextField1(
                modifier = Modifier
                    .padding(vertical = 6.dp),
                placeholder = "이름",
                text = name,
                onTextChange = {
                    name = it
                    profileVM.newUserProfile.value.name = it
                },
                required = false,
            )

            CompleteButton(
                text = "완료",
                enabled = usernameBtn,
                loading = updateLoading
            ) {
                profileVM.updateProfile(croppedImage) {
                    bottomSheet.mainCloseSheet()
                }
            }
        }
    }
}

//@OptIn(ExperimentalMaterialApi::class)
//@Preview(showBackground = true)
//@Composable
//fun UpdateMyProfileViewPreview() {
//    MoareTheme {
//        UpdateMyProfileView(rememberBottomSheetScaffoldState())
//    }
//}