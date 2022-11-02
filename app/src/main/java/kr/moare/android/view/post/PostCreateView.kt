package kr.moare.android.view.post

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
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
import kr.moare.android.viewmodel.common.GalleryViewModel

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun PostCreateView(
    bottomSheet: BottomSheet,
    mainNavController: NavController,
    postCreateNavController: NavController,
    postCreateVM: PostCreateViewModel,
    postVM: PostViewModel = hiltViewModel(),
    galleryVM: GalleryViewModel = hiltViewModel()
) {
    val coroutineScope = rememberCoroutineScope()

    var content by remember { mutableStateOf("") }
    val selectedMediaList by galleryVM.selectedMediaList.collectAsState()

    val sport = postCreateVM.post.sportHashtag
    val place = postCreateVM.post.place
    val completeBtnEnabled = sport.size > 0 && place.isNotEmpty() && content.isNotEmpty()

    BottomSheetScaffold(
        scaffoldState = bottomSheet.subSheetScaffoldState,
        topBar = {
            TopAppBar(
                title = { Text(text = "새 게시물") },
                backgroundColor = Color.White,
                elevation = 0.dp,
                actions = {
                    Button(
                        onClick = {
                            coroutineScope.launch {
                                mainNavController.navigateUp()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = Color.Transparent
                        ),
                        elevation = ButtonDefaults.elevation(
                            defaultElevation = 0.dp
                        )
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
                    postAddVM = postCreateVM,
                    profileVM = null,
                )
                SubCurrentBottomSheet.FindLocation -> FindLocationView(
                    bottomSheet = bottomSheet,
                    postAddVM = postCreateVM,
                    profileVM = null
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
                        .aspectRatio(
//                                if (mediaUriList.size > 0) {
//                                    if (contentResolver
//                                            .getType(mediaUriList[0])
//                                            ?.contains("video") == true
//                                    ) 0.5f else 1f
//                                } else {
//                                    1f
//                                }

//                                if (selectedMediaList.size > 0) {
//                                    if (selectedMediaList[0].type == "video") 0.5625f else 1f
//                                } else {
//                                    1f
//                                }
                            0.5625f
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    PhotoPickerView(
                        description = "사진 및 영상 추가",
                        content = null,
                        username = null,
                        if (selectedMediaList.size > 0) selectedMediaList[0].uri else null
                    ) {
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
                        description = "미리보기",
                        content = content,
                        username = postVM.username,
                        if (selectedMediaList.size > 0) selectedMediaList[0].uri else null,
                        true,
                        if (selectedMediaList.size > 0) {
                            selectedMediaList[0].type == "video"
                        } else {
                            false
                        }
                    ) {
//                        addPostNavController.currentBackStackEntry?.savedStateHandle?.set(
//                            "mediaUriList",
//                            selectedMediaList.toTypedArray()
//                        )
                        postCreateNavController.navigate(PostCreateNavItem.POSTCCREATEDETAIL.name)
                    }
                }
            }

            SearchViewButton(
//                text = if (sport.size > 0) sport.joinToString(" ") else "운동종목",
                sport = sport,
                place = null,
                expanded = sport.isNotEmpty()
            ) {
                bottomSheet.subOpenSheet(SubCurrentBottomSheet.SearchSport)
            }
            SearchViewButton(
//                text = if (postPlace.isEmpty()) "장소" else postPlace,
//                itemExist = postPlace.isNotEmpty(),
//                expanded = postPlace.isNotEmpty()
                sport = null,
                place = place,
                expanded = place.isNotEmpty()
            ) {
                bottomSheet.subOpenSheet(SubCurrentBottomSheet.FindLocation)
            }

            CustomPlainTextField2(
                modifier = Modifier,
                placeholder = "내용입력(첫째줄은 메인에 표시됩니다.)",
                text = content,
                expanded = content.isNotEmpty(),
                onTextChange = {
                    content = it
                    postCreateVM.post.content = it
                }
            )

            CompleteButton(
                text = "게시",
                enabled = completeBtnEnabled
            ) {
                postCreateVM.createPost(content, selectedMediaList)
            }
        }
    }
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