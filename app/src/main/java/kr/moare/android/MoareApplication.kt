package kr.moare.android

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MoareApplication : Application() {
//     val offlinePluginFactory = StreamOfflinePluginFactory(
//         config = io.getstream.chat.android.offline.plugin.configuration.Config(),
//         appContext = applicationContext,
//     )
//
//    val client = ChatClient.Builder("YOUR API KEY", applicationContext)
//        .withPlugin(offlinePluginFactory)
//        .logLevel(ChatLogLevel.ALL)
//        .build()
//
//    val user = User(
//        id = "marvel",
//        name = "Iron Man",
//        image = "https://bit.ly/2TIt8NR"
//    )
//    val token = client.devToken(user.id)
}