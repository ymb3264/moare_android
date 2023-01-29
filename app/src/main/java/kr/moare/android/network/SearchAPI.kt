package kr.moare.android.network

import kr.moare.android.utils.network.APIRoutes
import kr.moare.android.utils.network.KtorClient
import io.ktor.client.call.*
import io.ktor.client.request.*
import kr.moare.android.entities.SearchUserObj

class SearchAPI {
    suspend fun searchUsername(query: String): List<SearchUserObj> {
        return KtorClient.httpClient.get(APIRoutes.searchUser) {
            url {
                parameters.append("username", query)
            }
        }.body()
    }

    suspend fun searchHashtag(query: String): List<String> {
        return KtorClient.httpClient.get(APIRoutes.hashtag) {
            url {
                parameters.append("hashtag", query)
            }
        }.body()
    }
}