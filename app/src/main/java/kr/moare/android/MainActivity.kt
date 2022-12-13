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
import kr.moare.android.view.common.SportAddView
import kr.moare.android.view.start.UsernameView

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    lateinit var navController: NavController

    @OptIn(ExperimentalAnimationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val postNavController = rememberAnimatedNavController()
            MoareTheme {
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