package kr.moare.android.network

import kr.moare.android.utils.network.APIRoutes
import kr.moare.android.utils.network.KtorClient
import io.ktor.client.call.*
import io.ktor.client.request.*

class SearchAPI {
    suspend fun searchHashtag(query: String): List<String> {
        return KtorClient.httpClient.get(APIRoutes.hashtag) {
            url {
                parameters.append("hashtag", query)
            }
        }.body()
    }

    suspend fun searchUsername(query: String): List<String> {
        return KtorClient.httpClient.get(APIRoutes.username_search) {
            url {
                parameters.append("username", query)
            }
        }.body()
    }
}