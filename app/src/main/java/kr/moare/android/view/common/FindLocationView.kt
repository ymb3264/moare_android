package kr.moare.android.view.common

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
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
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.rememberNestedScrollInteropConnection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import kr.moare.android.components.SearchBar
import kr.moare.android.entities.BottomSheet
import kr.moare.android.viewmodel.common.LocationViewModel
import kr.moare.android.viewmodel.post.PostCreateViewModel
import kr.moare.android.viewmodel.profile.ProfileViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import kotlinx.coroutines.launch
import kr.moare.android.entities.AddressItem

@OptIn(ExperimentalMaterialApi::class, ExperimentalComposeUiApi::class,
    ExperimentalPermissionsApi::class
)
@Composable
fun FindLocationView(
    bottomSheet: BottomSheet,
    isDefaultLocation: Boolean = false,
    setLocation: (AddressItem) -> Unit = {},
    completion: () -> Unit = {},
    locationVM: LocationViewModel = hiltViewModel(),
) {
    var query by remember { mutableStateOf("") }

    val keyboardController = LocalSoftwareKeyboardController.current
    var checkPermission by remember { mutableStateOf(false) }

    val addressList by locationVM.addressList.collectAsState()
    val showAlert by locationVM.showAlert.collectAsState()
    val loading by locationVM.addressListLoading.collectAsState()
    val noResult by locationVM.noResult.collectAsState()

    val coroutineScope = rememberCoroutineScope()

    val locationPermissionsState = rememberMultiplePermissionsState(
        listOf(
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.ACCESS_FINE_LOCATION
        )
    )
    
    BackHandler() {
        if (isDefaultLocation) {
            bottomSheet.mainCloseSheet()
        } else {
            bottomSheet.subCloseSheet()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "지역 설정") },
                backgroundColor = Color.White,
                elevation = 0.dp,
                actions = {
                    Button(
                        onClick = {
//                            if (profileVM != null || postCreateVM != null) {
                            if (isDefaultLocation) {
                                bottomSheet.mainCloseSheet()
                            } else {
                                bottomSheet.subCloseSheet()
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
                    placeholder = "동명(읍, 면)으로 검색 (ex. 서초동)",
                    text = query,
                    textClear = { query = "" },
                    onTextChange = {
                        query = it
                        if (query.isEmpty()) {
                            locationVM.addressList.value = mutableListOf()
                            locationVM.noResult.value = ""
                        }
                    },
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
                        checkPermission = true
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
                    Text(text = "현재 위치로 설정", color = MaterialTheme.colors.primary)
                }

                if (noResult.isNotEmpty()) {
                    Text(text = noResult)
                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(0.dp)
            ) {
                items(addressList) {
                    TextButton(
                        onClick = {
                            if (isDefaultLocation) {
                                locationVM.showAlert(true, addressItem = it)
                            } else {
                                setLocation(it)
                                locationVM.addressList.value = mutableListOf()
                                bottomSheet.subCloseSheet()
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
                        ) {
                            Text(text = it.address)
                        } // Column
                    } // TextButton
                } // items
            } // LazyColumn

            // 위치접근권한이 설정되어있지 않았을때 권한 요청을 묻고 권한 허용시 실행된다
            if (locationPermissionsState.allPermissionsGranted && checkPermission) {
                locationVM.startLocationUpdates()
            }

            if (showAlert) {
                AlertDialog(
                    onDismissRequest = { locationVM.showAlert(false, null) },
                    confirmButton = {
                        TextButton(onClick = {
                            checkPermission = false
                            locationVM.addressList.value = mutableListOf()

                            if (isDefaultLocation) {
                                coroutineScope.launch {
                                    locationVM.addLocation()
                                    bottomSheet.mainCloseSheet()
                                }
                            } else {
                                setLocation(locationVM.addressItem!!)

                                bottomSheet.subCloseSheet()
                            }
                        }) {
                            Text(text = "확인")
                        }
                    },
                    dismissButton = { TextButton(onClick = {
                        checkPermission = false
                        locationVM.addressList.value = mutableListOf()
                        locationVM.showAlert(false, null)
                    }) {
                        Text(text = "취소")
                    }},
                    title = { Text(text = locationVM.addressItem!!.address) },
                    text = { Text(text = "으로 지역을 설정하시겠습니까?") }
                )
            } // if showAlert
        } // Column
        if (loading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Transparent),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
    } // Scaffold
}

//@OptIn(ExperimentalMaterialApi::class)
//@Preview(showBackground = true)
//@Composable
//fun FindLocationViewPreview() {
//    MoareTheme {
//        FindLocationView(bottomSheetScaffoldState = rememberBottomSheetScaffoldState())
//    }
//}