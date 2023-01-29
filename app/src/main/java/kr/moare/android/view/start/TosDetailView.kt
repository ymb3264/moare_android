package kr.moare.android.view.start

import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch
import kr.moare.android.R
import kr.moare.android.components.EmptyView
import kr.moare.android.components.StartViewButton
import kr.moare.android.entities.BottomSheet
import kr.moare.android.ui.theme.MoareTheme
import kr.moare.android.utils.StartNavItem
import kr.moare.android.utils.StringResources
import kr.moare.android.utils.TermsAgreeBottomSheet
import kr.moare.android.utils.TosDetailBottomSheet

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun TosDetailView(
    bottomSheetScaffoldState: BottomSheetScaffoldState
) {
    val coroutineScope = rememberCoroutineScope()
    val subBottomSheetScaffoldState = rememberBottomSheetScaffoldState()
    var currentBottomSheet: TosDetailBottomSheet by remember { mutableStateOf(TosDetailBottomSheet.Empty) }

    if (subBottomSheetScaffoldState.bottomSheetState.isCollapsed) { currentBottomSheet = TosDetailBottomSheet.Empty }

    val url = StringResources.tosUrl

    BackHandler {
        coroutineScope.launch {
            bottomSheetScaffoldState.bottomSheetState.collapse()
        }
    }

    BottomSheetScaffold(
        scaffoldState = subBottomSheetScaffoldState,
        sheetPeekHeight = 0.dp,
        sheetGesturesEnabled = false,
        topBar = {
            TopAppBar(
                title = { Text(text = StringResources.tos) },
                backgroundColor = Color.White,
                elevation = 0.dp,
                actions = {
                    Button(
                        onClick = {
                            coroutineScope.launch {
                                bottomSheetScaffoldState.bottomSheetState.collapse()
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
        sheetContent = {
            when (currentBottomSheet) {
                TosDetailBottomSheet.LocationTos -> LocationTosDetailView(subBottomSheetScaffoldState)
                TosDetailBottomSheet.Empty -> EmptyView()
            }
        }
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Bottom
        ) {
            AndroidView(
                modifier = Modifier.fillMaxHeight(0.95f),
                factory = {
                WebView(it).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    webViewClient = WebViewClient()
                    loadUrl(url)
                }
            }, update = {
                it.loadUrl(url)
            })
            
            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = StringResources.locationTos,
                modifier = Modifier
                    .clickable {
                        coroutineScope.launch {
                            currentBottomSheet = TosDetailBottomSheet.LocationTos
                            subBottomSheetScaffoldState.bottomSheetState.expand()
                        }
                    }
                    .padding(bottom = 4.dp),
                textDecoration = TextDecoration.Underline,
                color = Color.Gray
            )
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun LocationTosDetailView(
    bottomSheetScaffoldState: BottomSheetScaffoldState
) {
    val coroutineScope = rememberCoroutineScope()

    val url = StringResources.locationTosUrl

    BackHandler {
        coroutineScope.launch {
            bottomSheetScaffoldState.bottomSheetState.collapse()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = StringResources.locationTos) },
                backgroundColor = Color.White,
                elevation = 0.dp,
                actions = {
                    Button(
                        onClick = {
                            coroutineScope.launch {
                                bottomSheetScaffoldState.bottomSheetState.collapse()
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
    ) {
        AndroidView(factory = {
            WebView(it).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                webViewClient = WebViewClient()
                loadUrl(url)
            }
        }, update = {
            it.loadUrl(url)
        })
    }
}