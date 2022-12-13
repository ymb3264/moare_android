package kr.moare.android.network

import android.util.Log
import kr.moare.android.entities.MessageResponse
import kr.moare.android.utils.network.APIRoutes
import kr.moare.android.entities.Post
import kr.moare.android.utils.network.KtorClient
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.http.*
import io.ktor.util.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kr.moare.android.entities.LikeObj
import kr.moare.android.entities.UserDefaultLocation
import org.json.JSONObject
import java.io.File

class PostAPI {

    suspend fun createPost(token: String, post: Post, files: List<File>): MessageResponse {
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

    suspend fun getPosts(yearAndMonth: String, location: UserDefaultLocation, username: String, date: String): List<Post> {
        return KtorClient.httpClient.get(APIRoutes.post) {
            url {
                parameters.append("yearAndMonth", yearAndMonth)
                parameters.append("x", location.x)
                parameters.append("y", location.y)
                parameters.append("username", username)
                parameters.append("date", date)
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

    // ---------
    suspend fun getPost(number: Int): Post {
        return KtorClient.httpClient.get(APIRoutes.onePost) {
            url {
                parameters.append("number", number.toString())
            }
        }.body()
    }

    suspend fun updatePost(post: Post): String {
        return KtorClient.httpClient.put(APIRoutes.post) {
            url {
//                parameters.append("id", post.postNum.toString())
            }
            setBody(post)
        }.body()
    }

    suspend fun deletePost(id: String): String {
        return KtorClient.httpClient.delete(APIRoutes.post) {
            url {
                parameters.append("id", id)
            }
        }.body()
    }
}