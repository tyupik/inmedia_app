package ru.netology.inmedia.api

import retrofit2.Response
import retrofit2.http.*
import ru.netology.inmedia.dto.Job

interface JobApiService {

    @POST("api/my/jobs")
    suspend fun save(@Body job: Job): Response<Job>

    @GET("api/my/jobs")
    suspend fun getAllMyJobs(): Response<List<Job>>

    @DELETE("api/my/jobs/{id}")
    suspend fun removeById(@Path("id") id: Long): Response<Unit>
}