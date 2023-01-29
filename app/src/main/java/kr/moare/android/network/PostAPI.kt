package kr.moare.android.network

import android.util.Log
import com.google.protobuf.Api
import kr.moare.android.utils.network.APIRoutes
import kr.moare.android.utils.network.KtorClient
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.http.*
import io.ktor.util.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kr.moare.android.entities.*
import org.json.JSONObject
import java.io.File

class PostAPI {

    suspend fun createPost(token: String, post: CreatePost, files: List<File>): MessageResponse {
        val jsonPost = Json.encodeToString(post)
        return KtorClient.httpClient.submitFormWithBinaryData(
            url = APIRoutes.post,
            formData = formData {
                append("post", jsonPost)

                files.forEach {
                    append("media", it.readBytes(), Headers.build {
                        append(HttpHeaders.ContentType, "application/json")
                        append(HttpHeaders.ContentDisposition, "filename=\"${it.name}\"")
                    })
                }
            }
        ) {
            headers {
                append(HttpHeaders.Authorization, "Bearer $token")
            }
        }.body()
    }

    suspend fun updatePost(token: String, post: UpdatePost): Post {
        return KtorClient.httpClient.post(APIRoutes.postUpdate) {
            headers {
                append(HttpHeaders.Authorization, "Bearer $token")
            }
            setBody(post)
        }.body()
    }

    suspend fun deletePost(token: String, post: Post): MessageResponse {
        return KtorClient.httpClient.post(APIRoutes.postDelete) {
            headers {
                append(HttpHeaders.Authorization, "Bearer $token")
            }
            setBody(post)
        }.body()
    }

    suspend fun getPosts(token: String, yearAndMonth: String, location: UserDefaultLocation, username: String, date: String): List<Post> {
        return KtorClient.httpClient.get(APIRoutes.post) {
            headers {
                append(HttpHeaders.Authorization, "Bearer $token")
            }

            url {
                parameters.append("yearAndMonth", yearAndMonth)
                parameters.append("x", location.x)
                parameters.append("y", location.y)
                parameters.append("username", username)
                parameters.append("date", date)
            }
        }.body()
    }

    suspend fun getMorePosts(yearAndMonth: String, location: UserDefaultLocation, postCreatedAt: String): List<Post> {
        return KtorClient.httpClient.get(APIRoutes.morePost) {
            url {
                parameters.append("yearAndMonth", yearAndMonth)
                parameters.append("x", location.x)
                parameters.append("y", location.y)
                parameters.append("postCreatedAt", postCreatedAt)
            }
        }.body()
    }

    suspend fun getPost(yearAndMonth: String, postCreatedAt: String): Post {
        return KtorClient.httpClient.get(APIRoutes.onePost) {
            url {
                parameters.append("yearAndMonth", yearAndMonth)
                parameters.append("postCreatedAt", postCreatedAt)
            }
        }.body()
    }

    suspend fun like(likeObj: LikeObj): List<String> {
        return KtorClient.httpClient.post(APIRoutes.like) {
            setBody(likeObj)
        }.body()
    }

    suspend fun unlike(likeObj: LikeObj): List<String> {
        return KtorClient.httpClient.post(APIRoutes.unlike) {
            setBody(likeObj)
        }.body()
    }

    suspend fun reportPost(token: String, obj: Map<String, String>): MessageResponse {
        return KtorClient.httpClient.post(APIRoutes.postReport) {
            headers {
                append(HttpHeaders.Authorization, "Bearer $token")
            }
            setBody(obj)
        }.body()
    }
}