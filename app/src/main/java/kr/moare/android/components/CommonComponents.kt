package kr.moare.android.components

import android.net.Uri
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
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
import androidx.compose.ui.unit.Dp
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
fun SearchViewButton(
    sport: List<String>?,
    place: String?,
    required: Boolean = true,
    expanded: Boolean = false,
    onClick: () -> Unit,
) {
    Button(onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
            .height(50.dp),
        shape = RectangleShape,
        colors = ButtonDefaults.buttonColors(
            backgroundColor = Color.Transparent
        ),
        elevation = ButtonDefaults.elevation(
            defaultElevation = 0.dp
        ),
        contentPadding = PaddingValues(0.dp)
    ) {
        if (required) {
            Box(
                Modifier
//                    .padding(vertical = 4.dp)
                    .padding(end = 8.dp)
                    .background(MaterialTheme.colors.primary)
                    .width(2.dp)
                    .animateContentSize(tween(500))
                    .height(if (expanded) 50.dp else 5.dp)
                    .align(Alignment.CenterVertically)
            )
        } else {
            Box(
                Modifier
                    .padding(start = 12.dp)
                    .background(Color.Transparent)
                    .size(2.dp))
        }

        sport?.let {
            if (sport.isEmpty()) {
                Text(text = "운동종목", color = Color.Gray)
            } else {
                for (i in 0..sport.lastIndex) {
                    Text(text = sport[i],
                        color = Color.Black)
                    if (i != sport.lastIndex) {
                        TextDivideLine()
                    }
                }
//                sport.forEachIndexed { index, sport ->
//                    Text(text = sport,
//                        color = Color.Black)
//                    if (index != sport.lastIndex) {
//                        TextDivideLine()
//                    }
//                }
            }
        }

        place?.let {
            if (place.isEmpty()) {
                Text(text = "장소", color = Color.Gray)
            } else {
                Text(text = place, color = Color.Black)
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Icon(
            painter = painterResource(R.drawable.ic_arrow_right),
            contentDescription = "arrowRight",
            tint = Color.Gray,
            modifier = Modifier
//                .padding(end = 6.dp)
                .size(20.dp),
        )
    }
}

@Composable
fun TextDivideLine() {
    Box(
        Modifier
            .padding(vertical = 16.dp, horizontal = 8.dp)
            .clip(RectangleShape)
            .background(Color.Gray)
            .width(1.dp)
            .fillMaxHeight()
    )
}

@Composable
fun CustomPlainTextField1(
    modifier: Modifier = Modifier,
    placeholder: String,
    text: String,
    expandedHeight: Dp = 36.dp,
    required: Boolean = true,
    expanded: Boolean = false,
    readOnly: Boolean = false,
    onTextChange: (String) -> Unit
) {
    val focusRequester by remember { mutableStateOf(FocusRequester()) }
    var isFocused by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier.fillMaxWidth().height(36.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (required) {
            Box(
                Modifier
                    .padding(start = 12.dp)
//                    .padding(top = if (expanded) 0.dp else 18.dp)
                    .background(MaterialTheme.colors.primary)
                    .width(2.dp)
                    .animateContentSize(tween(500))
                    .height(if (expanded) expandedHeight else 5.dp)
                    .align(Alignment.CenterVertically)
            )
        } else {
            Box(
                Modifier
                    .padding(start = 12.dp)
                    .background(Color.Transparent)
                    .size(2.dp))
        }

        BasicTextField(
            value = text,
            onValueChange = onTextChange,
            modifier = modifier
                .padding(start = 8.dp, end = 12.dp)
                .fillMaxWidth()
                .weight(1f)
                .focusRequester(focusRequester = focusRequester)
                .onFocusChanged { isFocused = it.isFocused }
                .align(Alignment.CenterVertically),
            decorationBox = { innerTextField ->
                if (text.isEmpty() && !isFocused) {
                    Text(
                        text = placeholder,
                        style = MaterialTheme.typography.button,
                        color = Color.Gray,
                        modifier = Modifier.align(Alignment.CenterVertically)
                    )
                } else {
                    innerTextField()
                }
            },
            readOnly = readOnly,
            singleLine = true,
        )
    }
}

@Composable
fun CustomPlainTextField2(
    modifier: Modifier = Modifier,
    placeholder: String,
    text: String,
    expandedHeight: Dp = 86.dp,
    required: Boolean = true,
    expanded: Boolean = false,
    onTextChange: (String) -> Unit
) {
    val focusRequester by remember { mutableStateOf(FocusRequester()) }
    var isFocused by remember { mutableStateOf(false) }

    Row() {
        if (required) {
            Box(
                Modifier
                    .padding(start = 12.dp)
                    .padding(bottom = 8.dp)
                    .background(MaterialTheme.colors.primary)
                    .width(2.dp)
                    .animateContentSize(tween(500))
                    .height(if (expanded) expandedHeight else 5.dp)
                    .align(Alignment.CenterVertically)
            )
        } else {
            Box(
                Modifier
                    .padding(start = 12.dp)
                    .background(Color.Transparent)
                    .size(2.dp))
        }

        Column(
            Modifier
                .height(94.dp)
                .fillMaxWidth()
        ) {
            ContentTextFieldLine(Modifier.padding(vertical = 8.dp))

            BasicTextField(
                value = text,
                onValueChange = onTextChange,
                modifier = modifier
                    .padding(start = 10.dp, end = 12.dp)
                    .height(60.dp)
                    .fillMaxWidth()
                    .weight(1f)
                    .focusRequester(focusRequester = focusRequester)
                    .onFocusChanged { isFocused = it.isFocused },
                decorationBox = { innerTextField ->
                    if (text.isEmpty() && !isFocused) {
                        Text(
                            text = placeholder,
                            style = MaterialTheme.typography.button,
                            color = Color.Gray
                        )
                    } else {
                        innerTextField()
                    }
                }
            )

            ContentTextFieldLine(Modifier.padding(vertical = 8.dp))
        }
    }
}

@Composable
fun ContentTextFieldLine(modifier: Modifier = Modifier) {
    Box(
        modifier
            .padding(end = 12.dp)
            .background(Color.Gray)
            .fillMaxWidth()
            .height(1.dp)
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
//            .fillMaxSize()
            .fillMaxWidth()
            .height(1.dp)
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