package kr.moare.android.network

import kr.moare.android.entities.Post
import kr.moare.android.utils.network.APIRoutes
import kr.moare.android.utils.network.KtorClient
import kr.moare.android.entities.UserProfile
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.http.*
import io.ktor.util.*
import kr.moare.android.entities.UpdateProfile
import java.io.File

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

    suspend fun getUserPosts(username: String): List<Post> {
        return KtorClient.httpClient.get(APIRoutes.post) {
            url {
                parameters.append("username", username)
            }
        }.body()
    }

    suspend fun createTeamProfile(token: String, teamProfile: UserProfile, profileImage: File?): UserProfile {
        return KtorClient.httpClient.submitFormWithBinaryData(
            url = APIRoutes.teamProfile,
            formData = formData {
                if (profileImage != null) {
                    append("profileImage", profileImage.readBytes(), Headers.build {
                        append(HttpHeaders.ContentType, "application/json")
                        append(HttpHeaders.ContentDisposition, "filename=\"${profileImage.name}\"")
                    })
                }
            }
        ) {
            headers {
                append(HttpHeaders.Authorization, "Bearer $token")
            }
            setBody(teamProfile)
        }.body()
    }

    suspend fun updateProfile(token: String, profile: UpdateProfile, profileImage: File?): UserProfile {
        return KtorClient.httpClient.submitFormWithBinaryData(
           url = APIRoutes.profile,
            formData = formData {
                if (profileImage != null) {
                    append("profileImage", profileImage.readBytes(), Headers.build {
                        append(HttpHeaders.ContentType, "application/json")
                        append(HttpHeaders.ContentDisposition, "filename=\"${profileImage.name}\"")
                    })
                }
            }
        ) {
            headers {
                append(HttpHeaders.Authorization, "Bearer $token")
            }
            setBody(profile)
        }.body()
    }

    suspend fun getMyAccounts(token: String): List<UserProfile> {
        return KtorClient.httpClient.get(APIRoutes.myAccounts) {
            headers {
                append(HttpHeaders.Authorization, "Bearer $token")
            }
        }.body()
    }
}