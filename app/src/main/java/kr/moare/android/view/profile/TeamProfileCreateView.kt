package kr.moare.android.view.profile

import android.Manifest
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import kr.moare.android.components.*
import kr.moare.android.entities.BottomSheet
import kr.moare.android.utils.StringResources
import kr.moare.android.view.common.FindLocationView
import kr.moare.android.utils.SubCurrentBottomSheet
import kr.moare.android.utils.noRippleClickable
import kr.moare.android.view.common.ProfileGalleryView
import kr.moare.android.view.common.SportSelectView
import kr.moare.android.viewmodel.common.GalleryViewModel
import kr.moare.android.viewmodel.profile.MyProfileViewModel

@OptIn(ExperimentalMaterialApi::class, ExperimentalPermissionsApi::class,
    ExperimentalComposeUiApi::class
)
@Composable
fun TeamProfileCreateView(
    bottomSheet: BottomSheet,
    profileVM: MyProfileViewModel,
    galleryVM: GalleryViewModel = hiltViewModel()
) {
    var teamUsername by remember { mutableStateOf("") }
    var teamName by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var isDefaultImage by remember { mutableStateOf(false) }

    val croppedImage by galleryVM.croppedImage.collectAsState()

    val completeBtn by profileVM.teamCompleteBtnEnabled.collectAsState()
    val showErrorText1 by profileVM.showErrorText1.collectAsState()
    val showErrorText2 by profileVM.showErrorText2.collectAsState()
    val usernameLoading by profileVM.usernameLoading.collectAsState()
    val createLoading by profileVM.loading.collectAsState()

    val errorText1 = StringResources.existingUsernameError
    val errorText2 = StringResources.usernameValidationError

    val sport = profileVM.newTeamProfile.sportHashtag
    val place = profileVM.newTeamProfile.place

    var alert by remember { mutableStateOf(false) }

    var permissionRequested by remember { mutableStateOf(false) }
    val permissionState = rememberPermissionState(permission = Manifest.permission.READ_EXTERNAL_STORAGE) {
        permissionRequested = true
    }

    BackHandler() {
        if (profileVM.checkTeamContent(croppedImage)) {
            bottomSheet.mainCloseSheet()
            profileVM.resetTeamProfile()
        } else {
            alert = true
        }
    }

    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp
    val keyboardController = LocalSoftwareKeyboardController.current

    BottomSheetScaffold(
        scaffoldState = bottomSheet.subSheetScaffoldState,
        sheetGesturesEnabled = false,
        topBar = {
            TopAppBar(
                title = { Text(text = StringResources.teamProfileCreateNavigationTitle) },
                backgroundColor = Color.White,
                elevation = 0.dp,
                actions = {
                    TextButton(
                        onClick = {
                            if (profileVM.checkTeamContent(croppedImage)) {
                                bottomSheet.mainCloseSheet()
                                profileVM.resetTeamProfile()
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
        sheetPeekHeight = 0.dp,
        sheetContent = {
            bottomSheet.subSheet?.let {
                when (it) {
                    SubCurrentBottomSheet.SearchSport -> SportSelectView(
                        bottomSheet = bottomSheet
                    ) { selectedSport, userHashtag ->
                        profileVM.newTeamProfile.sportHashtag = selectedSport
                        profileVM.newTeamProfile.userHashtag = userHashtag
                    }
                    SubCurrentBottomSheet.FindLocation -> FindLocationView(
                        bottomSheet = bottomSheet,
                        setLocation = {
                            profileVM.newTeamProfile.place = it.address
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
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .noRippleClickable { keyboardController?.hide() },
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ProfileImageAddButton(
                url = "",
                uri = croppedImage,
                isDefaultImage = isDefaultImage,
                onClick1 = {
                    keyboardController?.hide()
                    bottomSheet.subOpenSheet(SubCurrentBottomSheet.Gallery)
                },
                onClick2 = {
                    galleryVM.croppedImage.value = null
                    isDefaultImage = true
                }
            )

            ProfileTextField(
                modifier = Modifier
                    .padding(vertical = 6.dp),
                placeholder = "생성자(관리자)",
                text = profileVM.myProfile.value.username,
                onTextChange = {},
                expanded = profileVM.myProfile.value.username.isNotEmpty(),
                readOnly = true
            )

            ProfileTextField(
                modifier = Modifier
                    .padding(vertical = 6.dp),
                placeholder = StringResources.teamUsernamePlaceholder,
                text = teamUsername,
                onTextChange = {
                    teamUsername = it
                    profileVM.newTeamProfile.username = it
                    profileVM.checkTeamUsername(teamUsername)
                },
                expanded = if (teamUsername.isNotEmpty()) (!showErrorText1 && !showErrorText2) else false,
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
                placeholder = StringResources.teamNamePlaceholder,
                text = teamName,
                onTextChange = {
                    teamName = it
                    profileVM.newTeamProfile.name = it
                    profileVM.checkCompleteBtn(true)
                },
                expanded = teamName.isNotEmpty(),
                infoRequired = true,
                infoText = StringResources.teamNameInfo
            )

            SportOrPlaceAddButton(
                placeholder = StringResources.sportPlaceholder,
                sport = sport ?: listOf(),
                infoRequired = true,
                infoText = StringResources.sportInfo,
                required = false
            ) {
                keyboardController?.hide()
                bottomSheet.subOpenSheet(SubCurrentBottomSheet.SearchSport)
            }
            SportOrPlaceAddButton(
                placeholder = "장소",
                place = place,
                placeText = if (place.isNotEmpty()) {
                    place.split(" ")[place.split(" ").lastIndex - 1]
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
                placeholder = "팀 정보(소개글)",
                text = content,
                onTextChange = {
                    content = it
                    profileVM.newTeamProfile.content = it
                },
                required = false
            )

            CompleteButton(
                text = StringResources.createProfileButton,
                enabled = completeBtn,
                loading = createLoading
            ) {
                profileVM.createTeamProfile(croppedImage)
                bottomSheet.mainCloseSheet()
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
                        profileVM.resetTeamProfile()
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
}

//@OptIn(ExperimentalMaterialApi::class)
//@Preview(showBackground = true)
//@Composable
//fun MakeTeamProfileViewPreview() {
//    MoareTheme {
//        MakeTeamProfileView(bottomSheetScaffoldState = rememberBottomSheetScaffoldState())
//    }
//}