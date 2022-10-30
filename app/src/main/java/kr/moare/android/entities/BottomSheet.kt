package kr.moare.android.entities

import androidx.compose.material.BottomSheetScaffoldState
import androidx.compose.material.ExperimentalMaterialApi
import kr.moare.android.utils.MainCurrentBottomSheet
import kr.moare.android.utils.SubCurrentBottomSheet
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

data class BottomSheet @OptIn(ExperimentalMaterialApi::class) constructor(
    val mainSheetScaffoldState: BottomSheetScaffoldState,
    val subSheetScaffoldState: BottomSheetScaffoldState,
    var mainSheet: MainCurrentBottomSheet,
    var subSheet: SubCurrentBottomSheet,
    val coroutineScope: CoroutineScope,
//    val mainOpenSheet: (MainCurrentBottomSheet) -> Unit,
//    val subOpenSheet: (SubCurrentBottomSheet) -> Unit,
//    val mainCloseSheet: () -> Unit,
//    val subCloseSheet: () -> Unit
) {
    // 로직을 안타게 변수 각각 선언
    @OptIn(ExperimentalMaterialApi::class)
    val mainOpenSheet: (MainCurrentBottomSheet) -> Unit = {
        mainSheet = it
        coroutineScope.launch { mainSheetScaffoldState.bottomSheetState.expand() }
    }
    @OptIn(ExperimentalMaterialApi::class)
    val subOpenSheet: (SubCurrentBottomSheet) -> Unit = {
        subSheet = it
        coroutineScope.launch { subSheetScaffoldState.bottomSheetState.expand() }
    }
    @OptIn(ExperimentalMaterialApi::class)
    val mainCloseSheet: () -> Unit = {
        coroutineScope.launch {
            mainSheetScaffoldState.bottomSheetState.collapse()
            mainSheet = MainCurrentBottomSheet.Empty
        }
    }
    @OptIn(ExperimentalMaterialApi::class)
    val subCloseSheet: () -> Unit = {
        coroutineScope.launch {
            subSheetScaffoldState.bottomSheetState.collapse()
            subSheet = SubCurrentBottomSheet.Empty
        }
    }
}