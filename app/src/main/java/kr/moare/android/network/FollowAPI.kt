package kr.moare.android.network

import kr.moare.android.utils.network.APIRoutes
import kr.moare.android.utils.network.KtorClient
import io.ktor.client.request.*

class FollowAPI {
    suspend fun follow(username1: String, username2: String) {
        val response =  KtorClient.httpClient.get(APIRoutes.follow) {
            url {
                parameters.append("username1", username1)
                parameters.append("username2", username2)
            }
        }
    }
}