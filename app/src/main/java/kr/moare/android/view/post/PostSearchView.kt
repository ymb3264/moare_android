package kr.moare.android.view.post

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kr.moare.android.utils.PostNavItem

@Composable
fun PostSearchView(
    searchList: List<String>,
    postNavController: NavController
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
        ) {
            items(searchList) {
                if (it.startsWith("#")) {
                    TextButton(
                        onClick = { },
                        modifier = Modifier
                            .fillMaxWidth(),
                        colors = ButtonDefaults.textButtonColors(contentColor = Color.Black),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text(text = it, modifier = Modifier.padding(start = 10.dp))
                        Spacer(Modifier.weight(1f))
                    }
                } else {
                    TextButton(
                        onClick = {
                            postNavController.navigate("${PostNavItem.USERPROFILE.name}/$it")
                        },
                        modifier = Modifier
                            .fillMaxWidth(),
                        colors = ButtonDefaults.textButtonColors(contentColor = Color.Black),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text(
                            text = it,
                            modifier = Modifier.padding(start = 10.dp),
                            fontSize = 18.sp
                        )
                        Spacer(Modifier.weight(1f))
                    }
                }
            }
        }
    }
}