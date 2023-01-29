package kr.moare.android.view.profile

import android.Manifest
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.rememberNestedScrollInteropConnection
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import kr.moare.android.components.*
import kr.moare.android.entities.BottomSheet
import kr.moare.android.ui.theme.MoareTheme
import kr.moare.android.utils.LoadingNavItem
import kr.moare.android.utils.StringResources
import kr.moare.android.view.common.FindLocationView
import kr.moare.android.utils.SubCurrentBottomSheet
import kr.moare.android.utils.noRippleClickable
import kr.moare.android.view.common.ProfileGalleryView
import kr.moare.android.view.common.SportSelectView
import kr.moare.android.viewmodel.common.GalleryViewModel
import kr.moare.android.viewmodel.common.SportSelectViewModel
import kr.moare.android.viewmodel.profile.MyProfileViewModel

@OptIn(ExperimentalMaterialApi::class, ExperimentalPermissionsApi::class,
    ExperimentalComposeUiApi::class
)
@Composable
fun MyProfileUpdateView(
    bottomSheet: BottomSheet,
    profileVM: MyProfileViewModel,
    galleryVM: GalleryViewModel = hiltViewModel()
) {
    val sportSelectVM: SportSelectViewModel = hiltViewModel()

    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp
    val keyboardController = LocalSoftwareKeyboardController.current

    var username by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var isDefaultImage by remember { mutableStateOf(false) }

    val profile by profileVM.updatedUserProfile.collectAsState()

    val updateLoading by profileVM.loading.collectAsState()

    val croppedImage by galleryVM.croppedImage.collectAsState()

    val completeBtn by profileVM.updateCompleteBtnEnabled.collectAsState()
    val showErrorText1 by profileVM.showErrorText1.collectAsState()
    val showErrorText2 by profileVM.showErrorText2.collectAsState()
    val usernameLoading by profileVM.usernameLoading.collectAsState()

    var alert by remember { mutableStateOf(false) }

    val errorText1 = StringResources.existingUsernameError
    val errorText2 = StringResources.usernameValidationError

    var permissionRequested by remember { mutableStateOf(false) }
    val permissionState = rememberPermissionState(permission = Manifest.permission.READ_EXTERNAL_STORAGE) {
        permissionRequested = true
    }

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
                title = { Text(text = StringResources.profileUpdateNavigationTitle) },
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
                        Text(text = StringResources.cancel)
                    }
                },
            )
        },
        sheetPeekHeight = bottomSheet.sheetHeight.dp,
        sheetContent = {
            when (bottomSheet.subSheet) {
                SubCurrentBottomSheet.SearchSport -> SportSelectView(
                    bottomSheet = bottomSheet,
                    sportSelectVM = sportSelectVM
                ) { selectedSport, userHashtag ->
                    profileVM.updatedUserProfile.value.sportHashtag = selectedSport
                    profileVM.updatedUserProfile.value.userHashtag = userHashtag
                }
                SubCurrentBottomSheet.FindLocation -> FindLocationView(
                    bottomSheet = bottomSheet,
                    setLocation = {
                        profileVM.updatedUserProfile.value.place = it.address
                    }
                )
                SubCurrentBottomSheet.Gallery -> ProfileGalleryView(
                    bottomSheet = bottomSheet,
                    galleryVM = galleryVM,
                    permissionRequested = permissionRequested,
                    permissionState = permissionState
                ) { isDefaultImage = false }
                SubCurrentBottomSheet.Empty -> EmptyView()
            }
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .noRippleClickable { keyboardController?.hide() },
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ProfileImageAddButton(
                url = profile.profileImage,
                uri = croppedImage,
                isDefaultImage = isDefaultImage,
                onClick1 = {
                    keyboardController?.hide()
                    bottomSheet.subOpenSheet(SubCurrentBottomSheet.Gallery)
                },
                onClick2 = {
                    galleryVM.croppedImage.value = null
                    isDefaultImage = true
                    if (profile.profileImage.isNotEmpty()) {
                        profileVM.updatedUserProfile.value.shouldUpdateDefaultImage = true
                    }
                }
            )

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
                placeholder = StringResources.namePlaceholder,
                text = name,
                onTextChange = {
                    name = it
                    profileVM.updatedUserProfile.value.name = it
                },
                required = false,
            )

            SportOrPlaceAddButton(
                placeholder = StringResources.sportPlaceholder,
                sport = profile.sportHashtag ?: listOf(),
                infoRequired = true,
                infoText = StringResources.sportInfo,
                required = false
            ) {
                keyboardController?.hide()
                bottomSheet.sheetHeight = screenHeight
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
                infoRequired = true,
                infoText = StringResources.locationInfo,
                required = false
            ) {
                keyboardController?.hide()
                bottomSheet.subOpenSheet(SubCurrentBottomSheet.FindLocation)
            }

            ContentTextField(
                modifier = Modifier,
                placeholder = StringResources.updateProfileContentPlaceholder,
                text = content,
                onTextChange = {
                    content = it
                    profileVM.updatedUserProfile.value.content = it
                },
                required = false
            )

            CompleteButton(
                text = StringResources.complete,
                enabled = completeBtn,
                loading = updateLoading
            ) {
                profileVM.updateProfile(croppedImage) {
                    bottomSheet.mainCloseSheet()
                }
            }
        }

//        if (permissionState.status.isGranted && checkPermission) {
//            galleryVM.loadAttachments(true)
//            bottomSheet.subOpenSheet(SubCurrentBottomSheet.Gallery)
//            checkPermission = false
//        }

        if (alert) {
            AlertDialog(
                onDismissRequest = { alert = false },
                confirmButton = {
                    TextButton(onClick = {
                        bottomSheet.mainCloseSheet()
                        profileVM.resetUpdateProfile()
                    }) {
                        Text(text = StringResources.confirm)
                    }
                },
                dismissButton = { TextButton(onClick = { alert = false }) {
                    Text(text = StringResources.cancel)
                }},
                title = { Text(text = StringResources.deleteFormTitle) },
                text = { Text(text = StringResources.deleteFormMessage) }
            )
        }
    } // scaffold

    LaunchedEffect(null) {
        profile.sportHashtag?.let { sportSelectVM.getSportList(it) }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Preview(showBackground = true)
@Composable
fun UpdateMyProfileViewPreview() {
    MoareTheme {
//        MyProfileUpdateView(bottomSheet = BottomSheet())
    }
}