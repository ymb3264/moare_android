package kr.moare.android.components

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import kr.moare.android.R
import kr.moare.android.ui.theme.Gray200
import kr.moare.android.ui.theme.MoareTheme

@Composable
fun SearchBar(
    modifier: Modifier,
    placeholder: String,
    text: String, onTextChange: (String) -> Unit,
    keyboardActions: KeyboardActions = KeyboardActions()
) {
    Row(
        modifier = modifier
//            .padding(horizontal = 10.dp)
            .clip(RoundedCornerShape(10.dp))
            .fillMaxWidth()
            .height(30.dp)
            .background(Gray200),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_search),
            contentDescription = "searchIcon",
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .size(25.dp)
        )
        SearchTextField(modifier = Modifier,
            placeholder = placeholder,
            text = text,
            onTextChange = onTextChange,
            keyboardActions = keyboardActions
        )
    }
}

@Composable
fun SearchTextField(modifier: Modifier, placeholder: String, text: String, onTextChange: (String) -> Unit, keyboardActions: KeyboardActions) {
    val focusRequester by remember { mutableStateOf(FocusRequester()) }
    var isFocused by remember { mutableStateOf(false) }

    BasicTextField(
        value = text,
        onValueChange = onTextChange,
        modifier = modifier
            .fillMaxWidth()
            .focusRequester(focusRequester = focusRequester)
            .onFocusChanged { isFocused = it.isFocused },
        decorationBox = { innerTextField ->
            if (text.isEmpty() && !isFocused) {
                Text(text = placeholder, color = Color.Gray)
            } else {
                innerTextField()
            }
        },
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = keyboardActions
    )
}

@Composable
fun RowScope.ProfileButton(
    modifier: Modifier,
    text: String,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            contentColor = MaterialTheme.colors.primary,
            backgroundColor = Color.Transparent,
            disabledBackgroundColor = Color.Transparent,
            disabledContentColor = Color.Gray
        ),
        elevation = ButtonDefaults.elevation(
            defaultElevation = 0.dp
        ),
        modifier = modifier
            .weight(1f),
        enabled = enabled,
        contentPadding = PaddingValues(0.dp)
    ) {
        ProfileButtonLine(Modifier)
        Spacer(Modifier.weight(1f))
        Text(text = text)
        Spacer(Modifier.weight(1f))
        ProfileButtonLine(Modifier)
    }
}

@Composable
fun ProfileButtonLine(
    modifier: Modifier,
    enabled: Boolean = true
) {
    Box(
        modifier = modifier
            .clip(RectangleShape)
            .size(width = 2.dp, height = 26.dp)
            .background(
                if (enabled) MaterialTheme.colors.primary
                else Color.Gray
            )
    )
}

@Composable
fun ProfileImageAddButton(
    uri: Uri?,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            backgroundColor = Color.Transparent
        ),
        elevation = ButtonDefaults.elevation(
            defaultElevation = 0.dp
        ),
        modifier = Modifier
            .padding(bottom = 10.dp)
    ) {
        Box(modifier = Modifier
            .clip(CircleShape)
            .size(180.dp)
            .border(
                width = 1.dp,
                color = if (uri != null) Color.Transparent else Color.Gray,
                shape = CircleShape
            ),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                .clip(CircleShape)
                .size(70.dp)
                .border(
                    width = 1.dp,
                    color = Color.Gray,
                    shape = CircleShape
                )
            )
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = Modifier
                    .clip(RectangleShape)
                    .height(2.dp)
                    .width(54.dp)
                    .background(Color.Transparent))
                Box(modifier = Modifier
                    .clip(RectangleShape)
                    .height(70.dp)
                    .width(36.dp)
                    .background(Color.White))
                Box(modifier = Modifier
                    .clip(RectangleShape)
                    .height(70.dp)
                    .width(35.dp)
                    .background(Color.Transparent))
                Box(modifier = Modifier
                    .clip(RectangleShape)
                    .height(2.dp)
                    .width(55.dp)
                    .background(Color.Gray))
            }

            Text(text = "사진 추가", color = Color.Gray, fontSize = 13.sp)

            if (uri != null) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White),
                    contentAlignment = Alignment.BottomStart
                ) {
                    AsyncImage(
                        model = uri,
                        contentDescription = "image",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit
                    )
                }
            }
        }
    }
}

@Composable
fun EmptyView() {
    Box(
        Modifier
            .fillMaxSize()
            .background(Color.Transparent))
}

@Preview(showBackground = true)
@Composable
private fun CommonComponentsPreview() {
    var text by remember { mutableStateOf("") }
    MoareTheme() {
        SearchBar(modifier = Modifier, placeholder = "test", text = text, onTextChange = { text = it })
    }
}