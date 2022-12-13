package kr.moare.android.view.profile

import android.Manifest
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.rememberNestedScrollInteropConnection
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
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

@OptIn(ExperimentalMaterialApi::class, ExperimentalPermissionsApi::class)
@Composable
fun ProfileUpdateView(
    bottomSheet: BottomSheet,
    profileVM: ProfileViewModel,
    galleryVM: GalleryViewModel = hiltViewModel()
) {
    var username by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }

    val profile by profileVM.updatedUserProfile.collectAsState()
    val updateLoading by profileVM.loading.collectAsState()

    val croppedImage by galleryVM.croppedImage.collectAsState()

    val completeBtn by profileVM.updateCompleteBtnEnabled.collectAsState()
    val showErrorText1 by profileVM.showErrorText1.collectAsState()
    val showErrorText2 by profileVM.showErrorText2.collectAsState()
    val usernameLoading by profileVM.usernameLoading.collectAsState()

    var alert by remember { mutableStateOf(false) }

    val errorText1 = "이미 사용중인 이름입니다"
    val errorText2 = "사용자 이름에는 영어 대/소문자, 숫자,\n밑줄(_) 및 마침표(.)만 사용할 수 있습니다."

    var checkPermission by remember { mutableStateOf(false) }
    val permissionState = rememberPermissionState(permission = Manifest.permission.READ_EXTERNAL_STORAGE)

    username = profile.username
    name = profile.name
    content = profile.content

    BackHandler() {
        if (profileVM.checkUpdateContent(croppedImage)) {
            bottomSheet.mainCloseSheet()
            profileVM.resetUpdateProfile()
        } else {
            alert = true
        }
    }

    BottomSheetScaffold(
        scaffoldState = bottomSheet.subSheetScaffoldState,
        sheetGesturesEnabled = false,
        topBar = {
            TopAppBar(
                title = { Text(text = "프로필 편집") },
                backgroundColor = Color.White,
                elevation = 0.dp,
                actions = {
                    TextButton(
                        onClick = {
                            if (profileVM.checkUpdateContent(croppedImage)) {
                                bottomSheet.mainCloseSheet()
                                profileVM.resetUpdateProfile()
                            } else {
                                alert = true
                            }
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
                    profileVM.updatedUserProfile.value.sportHashtag = sport
                }
                SubCurrentBottomSheet.FindLocation -> FindLocationView(
                    bottomSheet = bottomSheet,
                    setLocation = {
                        profileVM.updatedUserProfile.value.place = it.address
                    }
                )
                SubCurrentBottomSheet.Gallery -> ProfileGalleryView(
                    bottomSheet = bottomSheet,
                    galleryVM = galleryVM
                )
                SubCurrentBottomSheet.Empty -> EmptyView()
            }
        },
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
                if (permissionState.status.isGranted) {
                    bottomSheet.subOpenSheet(SubCurrentBottomSheet.Gallery)
                } else {
                    checkPermission = true
                    permissionState.launchPermissionRequest()
                }
            }

            ProfileTextField(
                modifier = Modifier
                    .padding(vertical = 6.dp),
                placeholder = "사용자 이름",
                text = username,
                onTextChange = {
                    username = it
                    // stateflow 변수의 참조형 속성은 값을 바꿔도 composable view에 적용되지 않는다. 선언형은 적용된다.
                    profileVM.updatedUserProfile.value.username = it
                    profileVM.checkUsername(username)
//                    if (profileVM.myProfile.value.username != username) {
//                        profileVM.checkUsername2(username)
//                    } else {
//                        // 이름이 있는경우에서 바로 원래이름으로 바꿨을때 errortext2가 사라져야한다
//                        profileVM.showErrorText2.value = false
//                    }
                },
                expanded = !showErrorText1 && !showErrorText2,
                loading = usernameLoading
            )

            if (showErrorText1 || showErrorText2) {
                Text(text = if (showErrorText1) errorText2 else errorText1,
                    color = Color.Red,
                    style = MaterialTheme.typography.caption,
                    modifier = Modifier
                        .padding(bottom = 10.dp, start = 12.dp, end = 12.dp)
                        .fillMaxWidth(),
                    textAlign = TextAlign.Start)
            }

            ProfileTextField(
                modifier = Modifier
                    .padding(vertical = 6.dp),
                placeholder = "이름",
                text = name,
                onTextChange = {
                    name = it
                    profileVM.updatedUserProfile.value.name = it
                },
                required = false,
            )

            SportOrPlaceAddButton(
                placeholder = "운동종목",
                sport = profile.sportHashtag ?: listOf(),
                required = false
            ) {
                bottomSheet.subOpenSheet(SubCurrentBottomSheet.SearchSport)
            }
            SportOrPlaceAddButton(
                placeholder = "장소",
                place = profile.place,
                placeText = if (profile.place.isNotEmpty()) {
                    profile.place.split(" ")[profile.place.split(" ").lastIndex - 1]
                } else {
                    ""
                },
                required = false
            ) {
                bottomSheet.subOpenSheet(SubCurrentBottomSheet.FindLocation)
            }

            ContentTextField(
                modifier = Modifier,
                placeholder = "소개",
                text = content,
                onTextChange = {
                    content = it
                    profileVM.updatedUserProfile.value.content = it
                },
                required = false
            )

            CompleteButton(
                text = "완료",
                enabled = completeBtn,
                loading = updateLoading
            ) {
                profileVM.updateProfile(croppedImage) {
                    bottomSheet.mainCloseSheet()
                }
            }
        }

        if (permissionState.status.isGranted && checkPermission) {
            galleryVM.loadAttachments()
            bottomSheet.subOpenSheet(SubCurrentBottomSheet.Gallery)
            checkPermission = false
        }

        if (alert) {
            AlertDialog(
                onDismissRequest = { alert = false },
                confirmButton = {
                    TextButton(onClick = {
                        bottomSheet.mainCloseSheet()
                        profileVM.resetUpdateProfile()
                    }) {
                        Text(text = "확인")
                    }
                },
                dismissButton = { TextButton(onClick = { alert = false }) {
                    Text(text = "취소")
                }},
                title = { Text(text = "작성내용 삭제") },
                text = { Text(text = "작성중인 내용이 삭제됩니다.") }
            )
        }
    } // scaffold
}

//@OptIn(ExperimentalMaterialApi::class)
//@Preview(showBackground = true)
//@Composable
//fun UpdateMyProfileViewPreview() {
//    MoareTheme {
//        UpdateMyProfileView(rememberBottomSheetScaffoldState())
//    }
//}