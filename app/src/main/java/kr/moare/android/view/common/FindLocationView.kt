package kr.moare.android.view.common

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import kr.moare.android.components.SearchBar
import kr.moare.android.entities.BottomSheet
import kr.moare.android.viewmodel.location.LocationViewModel
import kr.moare.android.viewmodel.post.PostAddViewModel
import kr.moare.android.viewmodel.profile.ProfileViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class, ExperimentalComposeUiApi::class,
    ExperimentalPermissionsApi::class
)
@Composable
fun FindLocationView(
    bottomSheet: BottomSheet,
    postAddVM: PostAddViewModel?,
    profileVM: ProfileViewModel?,
    locationVM: LocationViewModel = hiltViewModel()
) {
    var query by remember { mutableStateOf("") }

    val keyboardController = LocalSoftwareKeyboardController.current
    var checkPermission by remember { mutableStateOf(true) }

    val addressList by locationVM.addressList.collectAsState()
    val showAlert by locationVM.showAlert.collectAsState()

    val coroutineScope = rememberCoroutineScope()

    val locationPermissionsState = rememberMultiplePermissionsState(
        listOf(
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.ACCESS_FINE_LOCATION
        )
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "지역 설정") },
                backgroundColor = Color.White,
                elevation = 0.dp,
                actions = {
                    Button(
                        onClick = {
                            if (profileVM != null || postAddVM != null) {
                                bottomSheet.subCloseSheet()
                            } else {
                                bottomSheet.mainCloseSheet()
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
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                SearchBar(modifier = Modifier
                    .padding(horizontal = 12.dp)
                    .weight(1f),
                    placeholder = "지역",
                    text = query,
                    onTextChange = { query = it },
                    keyboardActions = KeyboardActions(onSearch = {
                        locationVM.searchAddress(query)
                        keyboardController?.hide()
                    })
                )
//                Button(
//                    onClick = {
//                            locationVM.searchAddress(query)
//                    },
//                    colors = ButtonDefaults.buttonColors(
//                        backgroundColor = Color.Transparent
//                    ),
//                    elevation = ButtonDefaults.elevation(
//                        defaultElevation = 0.dp
//                    )
//                ) {
//                    Text(text = "검색")
//                }
            }

            if (addressList.isEmpty()) {
                Button(
                    onClick = {
                        if (locationPermissionsState.allPermissionsGranted) {
                            locationVM.startLocationUpdates()
                        } else {
                            locationPermissionsState.launchMultiplePermissionRequest()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color.Transparent
                    ),
                    elevation = ButtonDefaults.elevation(
                        defaultElevation = 0.dp
                    )
                ) {
                    Text(text = "현재 위치로 설정")
                }
            }
            
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(0.dp)
            ) {
                items(addressList) {
                    TextButton(
                        onClick = {
                            if (postAddVM != null) {
//                                postAddVM.postPlace.value = it.address
                                postAddVM.post.place = it.address
                                postAddVM.post.x = it.x
                                postAddVM.post.y = it.y
                                bottomSheet.subCloseSheet()
                            } else if (profileVM != null) {
                                profileVM.newTeamProfile.place = it.address
                                bottomSheet.subCloseSheet()

                            } else {
                                locationVM.showAlert(true, addressItem = it)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp),
                        colors = ButtonDefaults.textButtonColors(contentColor = Color.Black),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
//                                .padding(horizontal = 10.dp)
                        ) {
                            Text(text = it.address)

                            if (it.roadAddress != "") {
                                Text(text = it.roadAddress,
                                    color = Color.LightGray,
                                    fontSize = 13.sp,
                                    modifier = Modifier.padding(top = 5.dp)
                                )
                            }
                        }
                    }
                }
            }

            if (locationPermissionsState.allPermissionsGranted && checkPermission) {
                locationVM.startLocationUpdates()
            }

            if (showAlert) {
                AlertDialog(
                    onDismissRequest = { locationVM.showAlert(false, null) },
                    confirmButton = {
                        TextButton(onClick = {
                            coroutineScope.launch {
                                locationVM.addLocation()
                                checkPermission = false
                                bottomSheet.mainCloseSheet()
                            }
                        }) {
                            Text(text = "확인")
                        }
                    },
                    dismissButton = { TextButton(onClick = {
                        locationVM.showAlert(false, null)
                        checkPermission = false
                    }) {
                        Text(text = "취소")
                    }},
                    title = { Text(text = locationVM.addressItem!!.address) },
                    text = { Text(text = "으로 지역을 설정하시겠습니까?") }
                )
            }

        }
    }
}

//@OptIn(ExperimentalMaterialApi::class)
//@Preview(showBackground = true)
//@Composable
//fun FindLocationViewPreview() {
//    MoareTheme {
//        FindLocationView(bottomSheetScaffoldState = rememberBottomSheetScaffoldState())
//    }
//}