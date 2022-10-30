package kr.moare.android.network

import kr.moare.android.utils.network.APIRoutes
import kr.moare.android.utils.network.KtorClient
import kr.moare.android.entities.LoginAccount
import kr.moare.android.entities.TokenResponse
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

class LoginAPI {
    suspend fun login(account: LoginAccount): TokenResponse {
        return KtorClient.httpClient.post(APIRoutes.login) {
            setBody(account)
        }.body()
    }

    suspend fun me(token: String): TokenResponse {
        return KtorClient.httpClient.get(APIRoutes.login) {
            headers {
                append(HttpHeaders.Authorization, "Bearer $token")
            }
        }.body()
    }
}