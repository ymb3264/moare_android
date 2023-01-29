package kr.moare.android.network

import io.ktor.client.call.*
import kr.moare.android.utils.network.APIRoutes
import kr.moare.android.utils.network.KtorClient
import io.ktor.client.request.*
import io.ktor.http.*
import kr.moare.android.entities.FollowObj
import kr.moare.android.entities.RequestFollowObj
import kr.moare.android.entities.ResponseFollowObj

class FollowAPI {
    suspend fun follow(token: String, followObj: RequestFollowObj): ResponseFollowObj {
        return KtorClient.httpClient.post(APIRoutes.follow) {
            headers {
                append(HttpHeaders.Authorization, "Bearer $token")
            }
            setBody(followObj)
        }.body()
    }

    suspend fun unfollow(token: String, followObj: RequestFollowObj): ResponseFollowObj {
        return KtorClient.httpClient.post(APIRoutes.unfollow) {
            headers {
                append(HttpHeaders.Authorization, "Bearer $token")
            }
            setBody(followObj)
        }.body()
    }
}