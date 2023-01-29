package kr.moare.android.view.message

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import io.getstream.chat.android.compose.ui.channels.header.ChannelListHeader
import io.getstream.chat.android.compose.ui.channels.list.ChannelList
import io.getstream.chat.android.compose.ui.theme.ChatTheme
import kr.moare.android.utils.StringResources
import kr.moare.android.viewmodel.profile.MyProfileViewModel

@Composable
fun MessageListView(
    navController: NavController,
    myProfileVM: MyProfileViewModel
) {
    val context = LocalContext.current

    val profile by myProfileVM.myProfile.collectAsState()

    ChatTheme {
        Column() {
            ChannelListHeader(
                modifier = Modifier.fillMaxWidth(),
                title = StringResources.message,
                leadingContent = {},
                trailingContent = {}
            )
//            ChannelsScreen(
//                title = "메시지",
//                isShowingHeader = false,
//                onItemClick = { channel ->
//                    context.startActivity(MessagesActivity.getIntent(context, channel.cid))
//                },
//                onBackPressed = { navController.popBackStack() }
//            )
            ChannelList(
                modifier = Modifier.fillMaxSize(),
                emptyContent = {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = StringResources.noChannelMessage)
                    }
                },
                onChannelClick = { channel ->
                    context.startActivity(MessagesActivity.getIntent(context, channel.cid, profile))
                }
            )
        }
    }
}