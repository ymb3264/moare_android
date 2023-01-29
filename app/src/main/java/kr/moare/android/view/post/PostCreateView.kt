package kr.moare.android.view.post

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.accompanist.permissions.*
import kr.moare.android.components.*
import kr.moare.android.entities.BottomSheet
import kr.moare.android.ui.theme.MoareTheme
import kr.moare.android.utils.SubCurrentBottomSheet
import kr.moare.android.view.common.FindLocationView
import kr.moare.android.view.common.GalleryView
import kr.moare.android.view.common.SportSelectView
import kr.moare.android.viewmodel.post.PostCreateViewModel
import kr.moare.android.viewmodel.post.PostViewModel
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kr.moare.android.R
import kr.moare.android.entities.Post
import kr.moare.android.entities.UserDefaultLocation
import kr.moare.android.utils.MainNavItem
import kr.moare.android.utils.StringResources
import kr.moare.android.utils.noRippleClickable
import kr.moare.android.viewmodel.common.GalleryViewModel
import kr.moare.android.viewmodel.profile.MyProfileViewModel
import java.util.jar.Manifest

@OptIn(ExperimentalMaterialApi::class, ExperimentalPermissionsApi::class,
    ExperimentalComposeUiApi::class
)
@Composable
fun PostCreateView(
    bottomSheet: BottomSheet,
    mainNavController: NavController,
//    postCreateNavController: NavController,
    postCreateVM: PostCreateViewModel,
    profileVM: MyProfileViewModel,
    galleryVM: GalleryViewModel = hiltViewModel()
) {
    val coroutineScope = rememberCoroutineScope()

    val selectedMediaList by galleryVM.selectedMediaList.collectAsState()
    val content by postCreateVM.content.collectAsState()
    val completeBtn by postCreateVM.completeBtn.collectAsState()
    val loading by postCreateVM.loading.collectAsState()

    val sport = postCreateVM.post.sportHashtag
    val place = postCreateVM.post.place

    var alert by remember { mutableStateOf(false) }
    var sportInfoAlert by remember { mutableStateOf(false) }

    var permissionRequested by remember { mutableStateOf(false) }
    val permissionState = rememberPermissionState(permission = android.Manifest.permission.READ_EXTERNAL_STORAGE)  {
        permissionRequested = true
    }

    val keyboardController = LocalSoftwareKeyboardController.current
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp

    BackHandler() {
        if (postCreateVM.checkContent(selectedMediaList)) {
            coroutineScope.launch {
                mainNavController.popBackStack()
                mainNavController.navigateUp()
            }
        } else {
            alert = true
        }
    }

    BottomSheetScaffold(
        scaffoldState = bottomSheet.subSheetScaffoldState,
        sheetGesturesEnabled = false,
        topBar = {
            TopAppBar(
                title = { Text(text = StringResources.postCreateNavigationTitle) },
                backgroundColor = Color.White,
                elevation = 0.dp,
                actions = {
                    Button(
                        onClick = {
                            if (postCreateVM.checkContent(selectedMediaList)) {
                                coroutineScope.launch {
                                    mainNavController.popBackStack()
                                    mainNavController.navigateUp()
                                }
                            } else {
                                alert = true
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = Color.Transparent
                        ),
                        elevation = ButtonDefaults.elevation(
                            defaultElevation = 0.dp
                        )
                    ) {
                        Text(text = StringResources.cancel, color = MaterialTheme.colors.primary)
                    }
                },
            )
        },
        sheetPeekHeight = bottomSheet.sheetHeight.dp,
        sheetContent = {
            when (bottomSheet.subSheet) {
                SubCurrentBottomSheet.SearchSport -> SportSelectView(
                    bottomSheet = bottomSheet
                ) { selectedSport, userHashtag ->
                    postCreateVM.post.sportHashtag = selectedSport
                    postCreateVM.post.userHashtag = userHashtag
                    postCreateVM.checkCompleteBtn(selectedMediaList)
                }
                SubCurrentBottomSheet.FindLocation -> FindLocationView(
                    bottomSheet = bottomSheet,
                    setLocation = {
                        postCreateVM.post.place = it.address
                        postCreateVM.post.x = it.x
                        postCreateVM.post.y = it.y
                        postCreateVM.checkCompleteBtn(selectedMediaList)
                    }
                )
                SubCurrentBottomSheet.Gallery -> GalleryView(
                    bottomSheet = bottomSheet,
                    galleryVM = galleryVM,
                    permissionRequested = permissionRequested,
                    permissionState = permissionState,
                ) {
                    postCreateVM.checkCompleteBtn(selectedMediaList)
                }
                SubCurrentBottomSheet.Empty -> EmptyView()
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
            Row(
                modifier = Modifier
                    .padding(horizontal = 12.dp)
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .clip(RectangleShape)
                        .background(Color.Transparent)
                        .weight(1f)
                        .aspectRatio(0.5625f),
                    contentAlignment = Alignment.Center
                ) {
                    PhotoPickerView(
                        description = StringResources.addMediaPlaceholder,
                        infoRequired = true,
                        infoText = StringResources.postCreateMediaInfo,
                        content = null,
                        place = null,
                        uri = if (selectedMediaList.size > 0) selectedMediaList[0].uri else null
                    ) {
                        keyboardController?.hide()
                        bottomSheet.subOpenSheet(SubCurrentBottomSheet.Gallery)
                    }
                }
                Box(
                    modifier = Modifier
                        .clip(RectangleShape)
                        .background(Color.Transparent)
                        .weight(1f)
                        .aspectRatio(0.5625f),
                    contentAlignment = Alignment.Center
                ) {
                    PhotoPickerView(
                        description = StringResources.postCreatePreviewPlaceholder,
                        content = content,
                        place = place,
                        uri = if (selectedMediaList.size > 0) selectedMediaList[0].uri else null,
                        isPreview = true,
                        isVideo = if (selectedMediaList.size > 0) {
                            selectedMediaList[0].type == "video"
                        } else {
                            false
                        }
                    ) {
                        if (selectedMediaList.isNotEmpty()) {
                            mainNavController.currentBackStackEntry?.savedStateHandle?.set("selectedMediaList", selectedMediaList.toTypedArray())
                            mainNavController.navigate(MainNavItem.POSTCREATEDETAIL.name)
                        }
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_info),
                    contentDescription = "info",
                    modifier = Modifier
                        .size(16.dp)
                        .padding(end = 4.dp),
                    tint = Color.Gray
                )

                Text(
                    text = StringResources.postCreateMediaDeleteInfo,
                    color = Color.Gray,
                    style = MaterialTheme.typography.caption)
            }

            SportOrPlaceAddButton(
                placeholder = StringResources.sportPlaceholder,
                sport = sport,
                infoRequired = true,
                infoText = StringResources.postCreateSportInfo,
                expanded = sport.isNotEmpty()
            ) {
                keyboardController?.hide()
                bottomSheet.sheetHeight = screenHeight
                bottomSheet.subOpenSheet(SubCurrentBottomSheet.SearchSport)
            }

            SportOrPlaceAddButton(
                placeholder = StringResources.locationPlaceholder,
                place = place,
                placeText = place.split(" ")[place.split(" ").lastIndex - 1],
                infoRequired = true,
                infoText = StringResources.postCreateLocationInfo,
                expanded = place.isNotEmpty()
            ) {
                keyboardController?.hide()
                bottomSheet.sheetHeight = screenHeight
                bottomSheet.subOpenSheet(SubCurrentBottomSheet.FindLocation)
            }

            ContentTextField(
                modifier = Modifier,
                placeholder = StringResources.postCreateContentPlaceholder,
                text = content,
                required = false,
                onTextChange = {
                    postCreateVM.post.content = it
                    postCreateVM.content.value = it
                }
            )

            CompleteButton(
                text = StringResources.upload,
                enabled = completeBtn,
                loading = loading
            ) {
                postCreateVM.createPost(selectedMediaList) {
                    mainNavController.popBackStack()
                    profileVM.getUserPosts(postCreateVM.username)
                }
            }
        }

//        if (permissionState.status.isGranted && permissionRequested) {
//            galleryVM.loadAttachments(false)
//            bottomSheet.subOpenSheet(SubCurrentBottomSheet.Gallery)
//            permissionRequested = false
//        }

        if (alert) {
            AlertDialog(
                onDismissRequest = { alert = false },
                confirmButton = {
                    TextButton(onClick = {
                        coroutineScope.launch {
                            mainNavController.popBackStack()
                            mainNavController.navigateUp()
                        }
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
//fun AddPostViewPreview() {
//    MoareTheme {
//        PostCreateView(
//            bottomSheet = ,
//            mainNavController = ,
//            postCreateNavController = ,
//            postCreateVM =
//        )
//    }
//}