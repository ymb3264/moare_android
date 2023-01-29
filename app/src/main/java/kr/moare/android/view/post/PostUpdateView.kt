package kr.moare.android.view.post

import android.Manifest
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import kotlinx.coroutines.launch
import kr.moare.android.R
import kr.moare.android.components.*
import kr.moare.android.entities.BottomSheet
import kr.moare.android.utils.StringResources
import kr.moare.android.utils.SubCurrentBottomSheet
import kr.moare.android.utils.noRippleClickable
import kr.moare.android.view.common.FindLocationView
import kr.moare.android.view.common.GalleryView
import kr.moare.android.view.common.ProfileGalleryView
import kr.moare.android.view.common.SportSelectView
import kr.moare.android.viewmodel.common.GalleryViewModel
import kr.moare.android.viewmodel.post.PostCreateViewModel
import kr.moare.android.viewmodel.post.PostViewModel
import kr.moare.android.viewmodel.profile.MyProfileViewModel

@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterialApi::class)
@Composable
fun PostUpdateView(
    bottomSheet: BottomSheet,
    mainNavController: NavController,
    myProfileNavController: NavController,
    profileVM: MyProfileViewModel
) {
    val coroutineScope = rememberCoroutineScope()

    val updatedPost by profileVM.updatedPost.collectAsState()
    val loading by profileVM.loading.collectAsState()

    val content by profileVM.content.collectAsState()

    var alert by remember { mutableStateOf(false) }

    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp
    val keyboardController = LocalSoftwareKeyboardController.current

    BackHandler() {
        mainNavController.popBackStack()
        mainNavController.navigateUp()
    }

    BottomSheetScaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = StringResources.postUpdateNavigationTitle) },
                backgroundColor = Color.White,
                elevation = 0.dp,
                actions = {
                    Button(
                        onClick = {
                            if (profileVM.checkUpdatedPostContent()) {
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
                }
                SubCurrentBottomSheet.FindLocation -> FindLocationView(
                    bottomSheet = bottomSheet,
                    setLocation = {
                    }
                )
                SubCurrentBottomSheet.Gallery -> EmptyView()
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
                Box(
                    modifier = Modifier
                        .clip(RectangleShape)
                        .background(Color.Transparent)
                        .width(screenWidth.dp / 2)
                        .aspectRatio(0.5625f)
                        .padding(bottom = 12.dp)
                        .clickable {

                        }
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.White),
                        contentAlignment = Alignment.BottomStart
                    ) {
                        AsyncImage(
                            model = profileVM.postToUpdate.value.mediaObj.first().url,
                            placeholder = painterResource(R.drawable.ic_search),
                            contentDescription = "image",
                            contentScale = ContentScale.Fit,
                            modifier = Modifier
                                .clip(RectangleShape)
                                .align(Alignment.Center)

                        )
                        PostListItemShadowView(profileVM.postToUpdate.value)
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
                    text = StringResources.postUpdateMediaInfo,
                    color = Color.Gray,
                    style = MaterialTheme.typography.caption)
            }

            SportOrPlaceAddButton(
                placeholder = StringResources.sportPlaceholder,
                sport = updatedPost.sportHashtag,
                infoRequired = true,
                infoText = StringResources.postCreateSportInfo,
                expanded = updatedPost.sportHashtag.isNotEmpty()
            ) {
                keyboardController?.hide()
                bottomSheet.subOpenSheet(SubCurrentBottomSheet.SearchSport)
            }

            SportOrPlaceAddButton(
                placeholder = StringResources.locationPlaceholder,
                place = updatedPost.place,
                placeText = updatedPost.place.split(" ")[updatedPost.place.split(" ").lastIndex - 1],
                infoRequired = true,
                infoText = StringResources.postCreateLocationInfo,
                expanded = updatedPost.place.isNotEmpty()
            ) {
                keyboardController?.hide()
                bottomSheet.subOpenSheet(SubCurrentBottomSheet.FindLocation)
            }

            ContentTextField(
                modifier = Modifier,
                placeholder = StringResources.postCreateContentPlaceholder,
                text = content,
                required = false,
                onTextChange = {
                    profileVM.updatedPost.value.content = it
                    profileVM.content.value = it
                }
            )

            CompleteButton(
                text = StringResources.complete,
                enabled = updatedPost.sportHashtag.isNotEmpty() && updatedPost.place.isNotEmpty(),
                loading = loading
            ) {
                profileVM.updatePost {
                    myProfileNavController.popBackStack()
                    myProfileNavController.navigateUp()
                }
            }
        }

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
                }
                },
                title = { Text(text = StringResources.deleteFormTitle) },
                text = { Text(text = StringResources.deleteFormMessage) }
            )
        }
    } // scaffold
}
