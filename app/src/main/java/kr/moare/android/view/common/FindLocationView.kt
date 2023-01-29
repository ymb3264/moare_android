package kr.moare.android.view.common

import android.content.Intent
import android.net.Uri
import android.provider.Settings
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.rememberNestedScrollInteropConnection
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kr.moare.android.components.SearchBar
import kr.moare.android.entities.BottomSheet
import kr.moare.android.viewmodel.common.LocationViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import kotlinx.coroutines.launch
import kr.moare.android.entities.AddressItem
import kr.moare.android.utils.StringResources

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

    val addressList by locationVM.addressList.collectAsState()
    val showAlert by locationVM.showAlert.collectAsState()
    val addressListLoading by locationVM.addressListLoading.collectAsState()
    val locationLoading by locationVM.locationLoading.collectAsState()
    val noResult by locationVM.noResult.collectAsState()

    val coroutineScope = rememberCoroutineScope()

    var checkPermission by remember { mutableStateOf(false) }
    var permissionRequested by remember { mutableStateOf(false) }
    val locationPermissionsState = rememberMultiplePermissionsState(
        listOf(
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.ACCESS_FINE_LOCATION
        )
    ) { permissionRequested = true }
    var locationSettingsAlert by remember { mutableStateOf(false) }

    val context = LocalContext.current

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
                        Text(text = StringResources.cancel, color = MaterialTheme.colors.primary)
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
                    placeholder = StringResources.findLocationPlaceholder,
                    text = query,
                    textClear = {
                        query = ""
                        locationVM.addressList.value = mutableListOf()
                        locationVM.noResult.value = ""
                    },
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
            }

            if (addressList.isEmpty()) {
                Button(
                    onClick = {
                        if (locationPermissionsState.allPermissionsGranted) {
                            locationVM.startLocationUpdates()
                        } else {
                            checkPermission = true
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

            if (addressListLoading) {
                CircularProgressIndicator( modifier = Modifier.size(28.dp), color = Color.Gray)
            } else {
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
            }

            // 위치접근권한이 설정되어있지 않았을때 권한 요청을 묻고 권한 허용시 실행된다
            LaunchedEffect(locationPermissionsState.allPermissionsGranted) {
                if (checkPermission && locationPermissionsState.allPermissionsGranted) {
                    locationVM.startLocationUpdates()
                }
            }

            LaunchedEffect(permissionRequested) {
                if (permissionRequested && !locationPermissionsState.allPermissionsGranted) {
                    locationSettingsAlert = true
                }
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
                            Text(text = StringResources.confirm)
                        }
                    },
                    dismissButton = { TextButton(onClick = {
                        checkPermission = false
                        locationVM.showAlert(false, null)
                    }) {
                        Text(text = StringResources.cancel)
                    }},
                    title = { Text(text = locationVM.addressItem!!.address) },
                    text = { Text(text = StringResources.confirmToSetLocation) }
                )
            } // if showAlert

            if (locationSettingsAlert) {
                AlertDialog(
                    onDismissRequest = { locationSettingsAlert = false },
                    confirmButton = {
                        TextButton(onClick = {
                            checkPermission = false
                            permissionRequested = false
                            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                data = Uri.fromParts("package", context.packageName, null)
                            }
                            context.startActivity(intent)
                        }) {
                            Text(text = "이동")
                        }
                    },
                    dismissButton = { TextButton(onClick = {
                        checkPermission = false
                        permissionRequested = false
                        locationSettingsAlert = false
                    }) {
                        Text(text = StringResources.cancel)
                    }},
                    title = { Text(text = "위치접근권한 설정") },
                    text = { Text(text = "위치접근권한 설정에서 허용해주세요") }
                )
            } // if showAlert
        } // Column
        if (locationLoading) {
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