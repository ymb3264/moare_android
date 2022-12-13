package kr.moare.android.components

import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kr.moare.android.R
import kr.moare.android.ui.theme.MoareTheme

@Composable
fun StartViewTextField(
    placeholder: String,
    text: String,
    loading: Boolean = false,
    textClear: () -> Unit = {},
    onTextChange: (String) -> Unit
) {
        TextField(
            value = text,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp)
                .padding(bottom = 10.dp),
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = Color.White,
                focusedIndicatorColor = Color.Gray,
            ),
            placeholder = {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = placeholder,
                )
            },
            onValueChange = onTextChange,
            trailingIcon = {
                if (text.isNotEmpty()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        if (loading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(28.dp),
                                color = Color.Gray
                            )
                        } else {
                            Box(modifier = Modifier.size(28.dp))
                        }

                        ClearButton(
                            boxModifier = Modifier
                                .padding(end = 8.dp, start = 8.dp)
                                .size(20.dp),
                            iconModifier = Modifier.size(14.dp)
                        ) {
                            textClear()
                        }
                    }
                }
            }
        )
}

@Composable
fun PwdTextField(
    placeholder: String,
    text: String,
    textClear: () -> Unit = {},
    onTextChange: (String) -> Unit
) {
    var visibility by remember { mutableStateOf(false) }

    TextField(
        value = text,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp)
            .padding(bottom = 10.dp),
        colors = TextFieldDefaults.textFieldColors(
            backgroundColor = Color.White,
            focusedIndicatorColor = Color.Gray,
        ),
        placeholder = {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = placeholder,
            )
        },
        onValueChange = onTextChange,
        trailingIcon = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (text.isNotEmpty()) {
                    ClearButton(
                        boxModifier = Modifier
                            .padding(end = 8.dp)
                            .size(20.dp),
                        iconModifier = Modifier.size(14.dp)
                    ) {
                        textClear()
                    }
                }

                IconButton(onClick = { visibility = !visibility }) {
                    if (visibility) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_visibility),
                            contentDescription = "visibility"
                        )
                    } else {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_visibility_off),
                            contentDescription = "visibility_off"
                        )
                    }
                }
            }
        },
        visualTransformation = if (visibility) VisualTransformation.None else PasswordVisualTransformation()
    )
}

@Composable
fun StartViewButton(
    modifier: Modifier = Modifier,
    text: String,
    onClick: () -> Unit = {},
    enabled: Boolean,
    width: Int = 0,
    count: Int = 5
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            backgroundColor = Color.Transparent,
            disabledBackgroundColor = Color.Transparent,
            disabledContentColor = Color.Gray,
            contentColor = MaterialTheme.colors.primary
        ),
        elevation = ButtonDefaults.elevation(
            defaultElevation = 0.dp
        ),
        border = BorderStroke(1.dp, if (enabled) MaterialTheme.colors.primary else Color.Gray),
        shape = RoundedCornerShape(50),
        modifier = modifier
            .fillMaxWidth()
            .height(75.dp)
            .padding(horizontal = 10.dp),
        enabled = enabled,
        contentPadding = PaddingValues(0.dp)
    ) {
        Box(Modifier.fillMaxSize()) {
//            Text(
//                text = text, style = MaterialTheme.typography.subtitle1,
//                modifier = Modifier.align(Alignment.Center)
//            )
//            for (i in 1..count) {
//                ButtonCircle(enabled, width, 100*(i-1))
//            }
            ButtonCircle(enabled, width)
        }
    }
}

@Composable
fun ButtonCircle(enabled: Boolean, width: Int = 0, delayMillis: Int = 100) {
    val xOffset =  animateIntAsState(
        targetValue = if (enabled) width-95 else 0,
        animationSpec = tween(600, easing = FastOutLinearInEasing)
    )

    Box(Modifier.fillMaxSize()) {
        Box(
            Modifier
                .offset(x = xOffset.value.dp)
                .align(Alignment.CenterStart)
        ) {
            Box(
                Modifier
                    .size(75.dp)
                    .clip(CircleShape)
//                    .border(1.dp, MaterialTheme.colors.primary, CircleShape)
                    .border(
                        BorderStroke(
                            1.dp,
                            if (enabled) MaterialTheme.colors.primary else Color.Gray
                        ), CircleShape
                    )
                    .background(Color.Transparent)
            )
        }
    }
}

@Composable
fun StartViewButton(
    enabled: Boolean = false,
    loading: Boolean = false,
    modifier: Modifier = Modifier,
    checkEmail: () -> Unit = {},
    onClick: () -> Unit
) {
    Box(modifier = modifier
        .clip(CircleShape)
        .border(
            BorderStroke(
                2.dp,
                if (enabled) MaterialTheme.colors.primary else Color.Gray
            ),
            CircleShape
        )
        .background(Color.Transparent)
        .size(75.dp)
        .clickable {
            checkEmail()
            if (enabled) onClick()
        },
        contentAlignment = Alignment.Center
    ) {
        if (loading) {
            CircularProgressIndicator()
        }
    }
}

@Composable
fun SelectedSportHashtag(sport: String) {
    Text("$sport",
        modifier = Modifier
            .border(
                BorderStroke(1.dp, MaterialTheme.colors.primary),
                RoundedCornerShape(15.dp)
            )
            .padding(vertical = 5.dp, horizontal = 10.dp),
        color = MaterialTheme.colors.primary,
        style = MaterialTheme.typography.caption
    )
}

@Preview(showBackground = true)
@Composable
private fun FeaturedPostDarkPreview() {
    var text by remember { mutableStateOf("") }
    MoareTheme() {
        StartViewTextField(placeholder = "test", text = text, onTextChange = { text = it })
//        StartViewButton(enabled = true, loading = true) { text = "" }
//        PwdTextField(placeholder = "password", text = text, onTextChange = { text = it })
    }
}