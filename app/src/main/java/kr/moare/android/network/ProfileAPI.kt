package kr.moare.android.network

import kr.moare.android.entities.Post
import kr.moare.android.utils.network.APIRoutes
import kr.moare.android.utils.network.KtorClient
import kr.moare.android.entities.TeamProfile
import kr.moare.android.entities.UserProfile
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

class ProfileAPI {
    suspend fun getMyProfile(token: String, username: String): UserProfile {
        return KtorClient.httpClient.get(APIRoutes.profile) {
            headers {
                append(HttpHeaders.Authorization, "Bearer $token")
            }
            url {
                parameters.append("username", username)
            }
        }.body()
    }

    suspend fun getUserProfile(username: String): UserProfile {
        return KtorClient.httpClient.get(APIRoutes.userProfile) {
            url {
                parameters.append("username", username)
            }
        }.body()
    }

    // --------------
    suspend fun getTeamProfile(username: String): TeamProfile {
        return KtorClient.httpClient.get(APIRoutes.teamProfile) {
            url {
                parameters.append("username", username)
            }
        }.body()
    }

    suspend fun makeTeamProfile(teamProfile: TeamProfile): TeamProfile {
        return KtorClient.httpClient.post(APIRoutes.teamProfile) {
            setBody(teamProfile)
        }.body()
    }

    suspend fun getUserPosts(username: String): List<Post> {
        return KtorClient.httpClient.get(APIRoutes.post) {
            url {
                parameters.append("username", username)
            }
        }.body()
    }
}