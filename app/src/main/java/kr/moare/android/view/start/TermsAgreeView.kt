package kr.moare.android.view.start

import androidx.compose.animation.ExperimentalAnimationApi
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
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import kotlinx.coroutines.launch
import kr.moare.android.R
import kr.moare.android.components.EmptyView
import kr.moare.android.components.PwdTextField
import kr.moare.android.components.StartViewButton
import kr.moare.android.ui.theme.MoareTheme
import kr.moare.android.utils.StartNavItem
import kr.moare.android.utils.StringResources
import kr.moare.android.utils.TermsAgreeBottomSheet
import kr.moare.android.viewmodel.start.JoinViewModel

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun TermsAgreeView(
    navController: NavController,
    joinVM: JoinViewModel
) {
    var checked by remember { mutableStateOf(false) }
    var tosAgreed by remember { mutableStateOf(false) }
    var privacyPolicyAgreed by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()
    val bottomSheetScaffoldState = rememberBottomSheetScaffoldState()
    var currentBottomSheet: TermsAgreeBottomSheet by remember { mutableStateOf(TermsAgreeBottomSheet.Empty) }

    if (bottomSheetScaffoldState.bottomSheetState.isCollapsed) { currentBottomSheet = TermsAgreeBottomSheet.Empty }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        val height = this.maxHeight
        BottomSheetScaffold(
            scaffoldState = bottomSheetScaffoldState,
            sheetPeekHeight = 0.dp,
            sheetGesturesEnabled = false,
            sheetContent = {
                when (currentBottomSheet) {
                    TermsAgreeBottomSheet.Tos -> TosDetailView(bottomSheetScaffoldState)
                    TermsAgreeBottomSheet.PrivacyPolicy -> PrivacyPolicyDetailView(bottomSheetScaffoldState)
                    TermsAgreeBottomSheet.Empty -> EmptyView()
                }
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
                    .offset(y = 37.dp),
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = StringResources.termsAgreeTitle,
                    style = MaterialTheme.typography.subtitle1,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                Text(
                    text = StringResources.termsAgreeMessage,
                    style = MaterialTheme.typography.caption
                )

                Column(
                    modifier = Modifier
                        .width(260.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .padding(top = 16.dp, bottom = 12.dp)
                            .padding(horizontal = 20.dp)
                    ) {
                        Column(Modifier.padding(end = 16.dp)) {
                            Text(
                                text = StringResources.allTermsAgreeButton,
                                fontSize = 14.sp
                            )
                        }

                        Spacer(Modifier.weight(1f))

                        Box(
                            Modifier
                                .clip(CircleShape)
                                .size(28.dp)
                                .border(
                                    if (checked) BorderStroke(
                                        1.dp,
                                        MaterialTheme.colors.primary
                                    ) else BorderStroke(1.dp, Color.Gray),
                                    CircleShape
                                )
                                .clickable {
                                    checked = !checked
                                    tosAgreed = checked
                                    privacyPolicyAgreed = checked
                                },
                        ) {
                            if (checked) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_check_circle),
                                    contentDescription = "checked",
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clickable {
                                            checked = !checked
                                            tosAgreed = checked
                                            privacyPolicyAgreed = checked
                                        },
                                    tint = MaterialTheme.colors.primary
                                )
                            }
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .padding(top = 16.dp, bottom = 12.dp)
                            .padding(horizontal = 20.dp)
                    ) {
                        Column(Modifier.padding(end = 16.dp)) {
                            Text(
                                text = StringResources.tosAgreeButton,
                                fontSize = 14.sp
                            )
                            Text(
                                text = StringResources.termsDetailButton,
                                modifier = Modifier
                                    .padding(top = 8.dp)
                                    .clickable {
                                        coroutineScope.launch {
                                            currentBottomSheet = TermsAgreeBottomSheet.Tos
                                            bottomSheetScaffoldState.bottomSheetState.expand()
                                        }
                                    },
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                        }

                        Spacer(Modifier.weight(1f))

                        Box(
                            Modifier
                                .clip(CircleShape)
                                .size(28.dp)
                                .border(
                                    if (tosAgreed) BorderStroke(
                                        1.dp,
                                        MaterialTheme.colors.primary
                                    ) else BorderStroke(1.dp, Color.Gray),
                                    CircleShape
                                )
                                .clickable {
                                    tosAgreed = !tosAgreed
                                    checked = tosAgreed && privacyPolicyAgreed
                                },
                        ) {
                            if (tosAgreed) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_check_circle),
                                    contentDescription = "checked",
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clickable {
                                            tosAgreed = !tosAgreed
                                            checked = tosAgreed && privacyPolicyAgreed
                                        },
                                    tint = MaterialTheme.colors.primary
                                )
                            }
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .padding(top = 16.dp, bottom = 12.dp)
                            .padding(horizontal = 20.dp)
                    ) {
                        Column(Modifier.padding(end = 16.dp)) {
                            Text(
                                text = StringResources.privacyPolicyAgreeButton,
                                fontSize = 14.sp
                            )
                            Text(
                                text = StringResources.termsDetailButton,
                                modifier = Modifier
                                    .padding(top = 8.dp)
                                    .clickable {
                                        coroutineScope.launch {
                                            currentBottomSheet = TermsAgreeBottomSheet.PrivacyPolicy
                                            bottomSheetScaffoldState.bottomSheetState.expand()
                                        }
                                    },
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                        }

                        Spacer(Modifier.weight(1f))

                        Box(
                            Modifier
                                .clip(CircleShape)
                                .size(28.dp)
                                .border(
                                    if (privacyPolicyAgreed) BorderStroke(
                                        1.dp,
                                        MaterialTheme.colors.primary
                                    ) else BorderStroke(1.dp, Color.Gray),
                                    CircleShape
                                )
                                .clickable {
                                    privacyPolicyAgreed = !privacyPolicyAgreed
                                    checked = tosAgreed && privacyPolicyAgreed
                                },
                        ) {
                            if (privacyPolicyAgreed) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_check_circle),
                                    contentDescription = "checked",
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clickable {
                                            privacyPolicyAgreed = !privacyPolicyAgreed
                                            checked = tosAgreed && privacyPolicyAgreed
                                        },
                                    tint = MaterialTheme.colors.primary
                                )
                            }
                        }
                    }
                }

                StartViewButton(enabled = checked) {
                    navController.navigate(StartNavItem.LOGININFOSAVE.name)
                }
                Spacer(modifier = Modifier.height(height / 2))
            }
        }
    }
}