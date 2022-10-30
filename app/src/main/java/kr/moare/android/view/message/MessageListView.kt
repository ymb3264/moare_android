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
            title = "mo_test1",
            //                    title = stringResource(id = R.string.app_name),
            onItemClick = { channel ->
                context.startActivity(MessagesActivity.getIntent(context, channel.cid))
            },
            onBackPressed = { navController.popBackStack() }
            //                    onBackPressed = { finish() }
        )
    }
//    Scaffold(
//        topBar = {
//            TopAppBar(
//                title = { Text("") },
//                navigationIcon = {
//                    IconButton(onClick = { /*TODO*/ }) {
//                        Icon(
//                            painter = painterResource(id = R.drawable.ic_arrow_back),
//                            contentDescription = "goBackIcon"
//                        )
//                    }
//                }
//            )
//        }
//    ) { padding ->
//        Column(
//            modifier = Modifier
//                .padding(padding)
//                .padding(horizontal = 12.dp),
//        ) {
//            Row(
//                modifier = Modifier
//                    .padding(top = 8.dp)
//                    .clickable { navController.navigate(MessageNavItem.MESSAGE.name) }
//                ,
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                Box(modifier = Modifier
//                    .padding(end = 8.dp)
//                    .clip(CircleShape)
//                    .size(60.dp)
//                    .background(Moare)
//                )
//                Column(
//
//                ) {
//                    Text("moare",
//                        style = MaterialTheme.typography.subtitle1,
//                    )
//                    Row(
//                        modifier = Modifier.padding(top = 4.dp),
//                        verticalAlignment = Alignment.CenterVertically
//                    ) {
//                        Text("hello...")
//                        Spacer(modifier = Modifier.weight(1f))
//                        Text(text = "오전 08:34", style = MaterialTheme.typography.caption)
//                    }
//                }
//            }
//            Row(
//                modifier = Modifier
//                    .padding(top = 8.dp),
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                Box(modifier = Modifier
//                    .padding(end = 8.dp)
//                    .clip(CircleShape)
//                    .size(60.dp)
//                    .background(Moare)
//                )
//                Column(
//
//                ) {
//                    Text("moare",
//                        style = MaterialTheme.typography.subtitle1,
//                    )
//                    Row(
//                        modifier = Modifier.padding(top = 4.dp),
//                        verticalAlignment = Alignment.CenterVertically
//                    ) {
//                        Text("hello...")
//                        Spacer(modifier = Modifier.weight(1f))
//                        Text(text = "오전 08:34", style = MaterialTheme.typography.caption)
//                    }
//                }
//            }
//        }
//    }
}

@Preview(showBackground = true)
@Composable
fun MessageListViewPreview() {
    MoareTheme {
        MessageListView(navController = rememberNavController())
    }
}