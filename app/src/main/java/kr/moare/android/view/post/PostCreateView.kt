package kr.moare.android.view.post

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import kr.moare.android.components.*
import kr.moare.android.entities.BottomSheet
import kr.moare.android.ui.theme.MoareTheme
import kr.moare.android.utils.PostCreateNavItem
import kr.moare.android.utils.SubCurrentBottomSheet
import kr.moare.android.view.common.FindLocationView
import kr.moare.android.view.common.GalleryView
import kr.moare.android.view.common.SportAddView
import kr.moare.android.viewmodel.post.PostCreateViewModel
import kr.moare.android.viewmodel.post.PostViewModel
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kr.moare.android.R
import kr.moare.android.entities.Post
import kr.moare.android.entities.UserDefaultLocation
import kr.moare.android.viewmodel.common.GalleryViewModel
import java.util.jar.Manifest

@OptIn(ExperimentalMaterialApi::class, ExperimentalPermissionsApi::class)
@Composable
fun PostCreateView(
    bottomSheet: BottomSheet,
    mainNavController: NavController,
    postCreateNavController: NavController,
    postCreateVM: PostCreateViewModel,
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

    var checkPermission by remember { mutableStateOf(false) }
    val permissionState = rememberPermissionState(permission = android.Manifest.permission.READ_EXTERNAL_STORAGE)

    BackHandler() {
        if (postCreateVM.checkContent(selectedMediaList)) {
            coroutineScope.launch {
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
                title = { Text(text = "새 게시물") },
                backgroundColor = Color.White,
                elevation = 0.dp,
                actions = {
                    Button(
                        onClick = {
                            if (postCreateVM.checkContent(selectedMediaList)) {
                                coroutineScope.launch {
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
                        Text(text = "취소", color = MaterialTheme.colors.primary)
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
                    postCreateVM.post.sportHashtag = sport
                }
                SubCurrentBottomSheet.FindLocation -> FindLocationView(
                    bottomSheet = bottomSheet,
                    setLocation = {
                        postCreateVM.post.place = it.address
                        postCreateVM.post.x = it.x
                        postCreateVM.post.y = it.y
                    }
                )
                SubCurrentBottomSheet.Gallery -> GalleryView(bottomSheet, galleryVM)
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
                        description = "사진 및 영상 추가",
                        content = null,
                        username = null,
                        if (selectedMediaList.size > 0) selectedMediaList[0].uri else null
                    ) {
                        if (permissionState.status.isGranted) {
                            bottomSheet.subOpenSheet(SubCurrentBottomSheet.Gallery)
                        } else {
                            checkPermission = true
                            permissionState.launchPermissionRequest()
                        }
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
                        description = "미리보기",
                        content = content,
                        username = postCreateVM.username,
                        uri = if (selectedMediaList.size > 0) selectedMediaList[0].uri else null,
                        isPreview = true,
                        isVideo = if (selectedMediaList.size > 0) {
                            selectedMediaList[0].type == "video"
                        } else {
                            false
                        }
                    ) {
                        if (selectedMediaList.isNotEmpty()) {
                            postCreateNavController.currentBackStackEntry?.savedStateHandle?.set("selectedMediaList", selectedMediaList.toTypedArray())
                            postCreateNavController.navigate(PostCreateNavItem.POSTCCREATEDETAIL.name)
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
                    text = "스포츠 관련 사진/영상이 아닐경우 게시물이 삭제될 수 있습니다.",
                    color = Color.Gray,
                    style = MaterialTheme.typography.caption)
            }

            SportOrPlaceAddButton(
                placeholder = "운동종목",
                sport = sport,
                expanded = sport.isNotEmpty()
            ) {
                bottomSheet.subOpenSheet(SubCurrentBottomSheet.SearchSport)
            }

            SportOrPlaceAddButton(
                placeholder = "장소",
                place = place,
                placeText = place.split(" ")[place.split(" ").lastIndex - 1],
                expanded = place.isNotEmpty()
            ) {
                bottomSheet.subOpenSheet(SubCurrentBottomSheet.FindLocation)
            }

            ContentTextField(
                modifier = Modifier,
                placeholder = "내용입력(첫째줄은 메인에 표시됩니다.)",
                text = content,
                expanded = content.isNotEmpty(),
                onTextChange = {
                    postCreateVM.post.content = it
                    postCreateVM.content.value = it
                    postCreateVM.checkCompleteBtn(selectedMediaList)
                }
            )

            CompleteButton(
                text = "게시",
                enabled = completeBtn,
                loading = loading
            ) {
                postCreateVM.createPost(selectedMediaList) {
                    mainNavController.popBackStack()
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
                        coroutineScope.launch {
                            mainNavController.navigateUp()
                        }
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

@OptIn(ExperimentalMaterialApi::class)
@Preview(showBackground = true)
@Composable
fun AddPostViewPreview() {
    MoareTheme {
        val bottomSheetScaffoldState = rememberBottomSheetScaffoldState()
//        val coroutineScope = rememberCoroutineScope()
//        coroutineScope.launch {
//            bottomSheetScaffoldState.bottomSheetState.expand()
//        }
//        AddPostView(rememberNavController())
    }
}