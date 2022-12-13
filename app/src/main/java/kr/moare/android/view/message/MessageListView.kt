package kr.moare.android.view.message

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import kr.moare.android.ui.theme.MoareTheme
import io.getstream.chat.android.compose.ui.channels.ChannelsScreen
import io.getstream.chat.android.compose.ui.theme.ChatTheme

@Composable
fun MessageListView(navController: NavController) {
    val context = LocalContext.current
    ChatTheme {
        ChannelsScreen(
            title = "메세지",
            onItemClick = { channel ->
                context.startActivity(MessagesActivity.getIntent(context, channel.cid))
            },
            onBackPressed = { navController.popBackStack() }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun MessageListViewPreview() {
    MoareTheme {
        MessageListView(navController = rememberNavController())
    }
}