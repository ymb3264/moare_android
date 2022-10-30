package kr.moare.android.view.splash

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kr.moare.android.R
import kr.moare.android.utils.SplashNavItem
import kr.moare.android.viewmodel.start.JoinViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun JoinSplashView(
    splashNavController: NavController,
    joinVM: JoinViewModel
) {
    val offset1 = remember { Animatable(Offset.Zero, Offset.VectorConverter) }
    val offset2 = remember { Animatable(Offset.Zero, Offset.VectorConverter) }
    val offset3 = remember { Animatable(Offset.Zero, Offset.VectorConverter) }
    val offset4 = remember { Animatable(Offset.Zero, Offset.VectorConverter) }
    val offset5 = remember { Animatable(Offset.Zero, Offset.VectorConverter) }

    var visible by remember { mutableStateOf(true) }
    var iconVisible by remember { mutableStateOf(false) }

    val joinSuccess by joinVM.joinSuccess.collectAsState()

    val coroutineScope = rememberCoroutineScope()

    coroutineScope.launch {
        delay(800)
        offset1.animateTo(
            Offset(42f, -10.4f),
            tween(1000, easing = FastOutSlowInEasing)
        )
    }
    coroutineScope.launch {
        delay(800)
        offset2.animateTo(
            Offset(-42f, -10.4f),
            tween(1000, easing = FastOutSlowInEasing)
        )
//            offset2.animateTo(Offset(-34.2f, -10.8f))
    }
    coroutineScope.launch {
        delay(800)
        offset3.animateTo(
            Offset(0f, -41f),
            tween(1000, easing = FastOutSlowInEasing)
        )
    }
    coroutineScope.launch {
        delay(800)
        offset4.animateTo(
            Offset(26.4f, 39f),
            tween(1000, easing = FastOutSlowInEasing)
        )
    }
    coroutineScope.launch {
//            offset5.animateTo(Offset(-20.8f, 28.8f))
        delay(800)
        offset5.animateTo(
            Offset(-26.4f, 39f),
            tween(1000, easing = FastOutSlowInEasing)
        )
        iconVisible = true
        delay(500)
        visible = false

        delay(1100)
        if (joinSuccess) {
            splashNavController.popBackStack()
            splashNavController.navigate(SplashNavItem.Main.name)
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        AnimatedVisibility(
            visible = iconVisible,
            enter = fadeIn()
        ) {
            Icon(
                painter = painterResource(id = R.drawable.moare),
                contentDescription = "",
                modifier = Modifier
                    .size(230.dp)
                    .align(Alignment.Center)
                    .padding(bottom = 3.dp),
                tint = Color.Unspecified,
            )
        }

        AnimatedVisibility(
            visible = visible,
            exit = fadeOut(tween(1000)),
        ) {
            Box(
                Modifier.fillMaxSize()
            ) {
                Box(
                    Modifier
                        .offset(x = offset1.value.x.dp, y = offset1.value.y.dp)
                        .size(72.dp)
                        .clip(CircleShape)
                        .border(6.dp, MaterialTheme.colors.primary, CircleShape)
                        .background(Color.Transparent)
                        .align(Alignment.Center)
                )
                Box(
                    Modifier
                        .offset(x = offset2.value.x.dp, y = offset2.value.y.dp)
                        .size(72.dp)
                        .clip(CircleShape)
                        .border(6.dp, MaterialTheme.colors.primary, CircleShape)
                        .background(Color.Transparent)
                        .align(Alignment.Center)
                )
                Box(
                    Modifier
                        .offset(x = offset3.value.x.dp, y = offset3.value.y.dp)
                        .size(72.dp)
                        .clip(CircleShape)
                        .border(6.dp, MaterialTheme.colors.primary, CircleShape)
                        .background(Color.Transparent)
                        .align(Alignment.Center)
                )
                Box(
                    Modifier
                        .offset(x = offset4.value.x.dp, y = offset4.value.y.dp)
                        .size(72.dp)
                        .clip(CircleShape)
                        .border(6.dp, MaterialTheme.colors.primary, CircleShape)
                        .background(Color.Transparent)
                        .align(Alignment.Center)
                )
                Box(
                    Modifier
                        .offset(x = offset5.value.x.dp, y = offset5.value.y.dp)
                        .size(72.dp)
                        .clip(CircleShape)
                        .border(6.dp, MaterialTheme.colors.primary, CircleShape)
                        .background(Color.Transparent)
                        .align(Alignment.Center)
                )
            }
        }
    }
}

//@Preview(showBackground = true)
//@Composable
//fun JoinSplashViewPreview() {
//    MoareTheme {
//        JoinSplashView(rememberNavController())
//    }
//}