package ru.netology.inmedia.api


import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*
import ru.netology.inmedia.dto.Media
import ru.netology.inmedia.dto.Post
import ru.netology.inmedia.dto.PushToken
import ru.netology.inmedia.dto.Token

interface PostApiService {

    @POST("api/users/push-tokens")
    suspend fun save(@Body pushToken: PushToken): Response<Unit>

    @GET("api/posts/latest")
    suspend fun getLatest(@Query("count") count: Int): Response<List<Post>>

    @POST("api/posts")
    suspend fun save(@Body post: Post): Response<Post>

    @POST("api/posts/{id}/likes")
    suspend fun likeById(@Path("id") id: Long): Response<Post>

    @DELETE("api/posts/{id}/likes")
    suspend fun dislikeById(@Path("id") id: Long): Response<Post>

    @DELETE("api/posts/{id}")
    suspend fun removeById(@Path("id") id: Long): Response<Unit>

    @GET("api/posts/{id}/before")
    suspend fun getBefore(
        @Path("id") id: Long,
        @Query("count") count: Int
    ): Response<List<Post>>

    @POST("api/users/registration")
    suspend fun registration(@Query ("login") login: String, @Query ("pass") pass: String, @Query ("name") name: String): Response<Token>

    @POST("api/users/authentication")
    suspend fun sendAuth(@Query ("login") login: String, @Query ("pass") pass: String): Response<Token>

    @Multipart
    @POST("api/media")
    suspend fun upload(@Part media: MultipartBody.Part): Response<Media>
}