package kr.moare.android.components

import android.net.Uri
import androidx.compose.foundation.BorderStroke
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
fun ColumnScope.CompleteButton(
    text: String,
    enabled: Boolean = false,
    loading: Boolean = false,
    onClick: () -> Unit
) {
    val color = if (enabled) MaterialTheme.colors.primary else Color.Gray

    if (loading) {
        CircularProgressIndicator()
    } else {
        Button(onClick = onClick,
            colors = ButtonDefaults.buttonColors(
                backgroundColor = Color.Transparent,
                disabledBackgroundColor = Color.Transparent
            ),
            elevation = ButtonDefaults.elevation(
                defaultElevation = 0.dp
            ),
            border = BorderStroke(1.dp, color),
            enabled = enabled
        ) {
            Text(text = text, color = color)
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