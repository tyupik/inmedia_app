package ru.netology.inmedia.api

import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*
import ru.netology.inmedia.dto.*


interface EventApiService {

    @GET("api/events/latest")
    suspend fun getLatest(@Query("count") count: Int): Response<List<Event>>

    @POST("api/events")
    suspend fun save(@Body event: Event): Response<Event>

    @POST("api/events/{id}/likes")
    suspend fun likeById(@Path("id") id: Long): Response<Event>

    @DELETE("api/events/{id}/likes")
    suspend fun dislikeById(@Path("id") id: Long): Response<Event>

    @DELETE("api/events/{id}")
    suspend fun removeById(@Path("id") id: Long): Response<Unit>

    @GET("api/events/{id}/before")
    suspend fun getBefore(
        @Path("id") id: Long,
        @Query("count") count: Int
    ): Response<List<Event>>

    @Multipart
    @POST("api/media")
    suspend fun upload(@Part media: MultipartBody.Part): Response<Media>

    @POST("api/events/{id}/participants")
    suspend fun participateById(@Path("id") id: Long): Response<Event>

    @DELETE("api/events/{id}/participants")
    suspend fun unparticipateById(@Path("id") id: Long): Response<Event>
}