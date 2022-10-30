package kr.moare.android

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.navigation.NavController
import kr.moare.android.ui.theme.MoareTheme
import kr.moare.android.view.navgraph.SplashNavGraph
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import dagger.hilt.android.AndroidEntryPoint
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.client.logger.ChatLogLevel
import io.getstream.chat.android.client.models.User
import io.getstream.chat.android.offline.plugin.configuration.Config
import io.getstream.chat.android.offline.plugin.factory.StreamOfflinePluginFactory

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    lateinit var navController: NavController

    @OptIn(ExperimentalMaterialApi::class, ExperimentalAnimationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val offlinePluginFactory = StreamOfflinePluginFactory(
            config = Config(),
            appContext = applicationContext,
        )

        val client = ChatClient.Builder("7rctkyk524sx", applicationContext)
            .withPlugin(offlinePluginFactory)
            .logLevel(ChatLogLevel.ALL)
            .build()

        val user = User(
//            id = "marvel",
//            name = "Iron Man",
            id = "moare1",
            name = "moare1",
            image = "https://upload.wikimedia.org/wikipedia/en/thumb/0/01/Golden_State_Warriors_logo.svg/1200px-Golden_State_Warriors_logo.svg.png"
        )
        val token = client.devToken(user.id)
        client.connectUser(user, token)
            .enqueue {
                if(it.isSuccess) {
                    client.createChannel(
                        channelType = "messaging",
                        channelId = "new_channel_02",
                        memberIds = listOf(user.id),
                        extraData = mapOf("name" to "My New Channel2")
                    ).enqueue()
                }
            }

        setContent {
            val postNavController = rememberAnimatedNavController()
//            navController = postNavController
            MoareTheme {
//                StartNavGraph()
                SplashNavGraph(postNavController)
            }
        }
    }

//    override fun onNewIntent(intent: Intent?) {
//        super.onNewIntent(intent)
//        navController.handleDeepLink(intent)
//    }
}

//@Preview(showBackground = true)
//@Composable
//fun DefaultPreview() {
//    MoareTheme {
//        MainView()
////        SportSelectView()
//    }
//}