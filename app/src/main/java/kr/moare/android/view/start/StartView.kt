package kr.moare.android.view.start

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import kr.moare.android.ui.theme.MoareTheme
import kr.moare.android.utils.StartNavItem
import kr.moare.android.utils.StringResources

@Composable
fun StartView(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = { navController.navigate(StartNavItem.Login.name) },
            colors = ButtonDefaults.buttonColors(
                backgroundColor = Color.White
            ),
            elevation = ButtonDefaults.elevation(
                defaultElevation = 0.dp
            )
        ) {
            Text(text = StringResources.login, fontSize = 20.sp, color = Color.Gray)
        }

        MiddleDesign()

        Button(onClick = { navController.navigate(StartNavItem.Email.name) },
            colors = ButtonDefaults.buttonColors(
                backgroundColor = Color.White
            ),
            elevation = ButtonDefaults.elevation(
                defaultElevation = 0.dp
            )
        ) {
            Text(text = "회원가입", fontSize = 20.sp, color = Color.Gray)
        }
    }
}

@Composable
fun MiddleDesign() {
    Row(
        horizontalArrangement = Arrangement.spacedBy(5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier
            .clip(RectangleShape)
            .background(MaterialTheme.colors.primary)
            .height(2.dp)
            .weight(1f)
        )
        Box(modifier = Modifier
            .clip(CircleShape)
            .border(BorderStroke(2.dp, MaterialTheme.colors.primary), CircleShape)
            .background(Color.Transparent)
            .size(75.dp)
        )
        Box(modifier = Modifier
            .clip(RectangleShape)
            .background(MaterialTheme.colors.primary)
            .height(2.dp)
            .weight(1f)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun StartViewPreview() {
    MoareTheme {
        StartView(navController = rememberNavController())
    }
}