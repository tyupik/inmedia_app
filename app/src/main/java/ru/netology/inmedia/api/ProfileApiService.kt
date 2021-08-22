package ru.netology.inmedia.api

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import ru.netology.inmedia.dto.Post
import ru.netology.inmedia.dto.User

interface ProfileApiService {

    @GET("api/my/wall/latest")
    suspend fun getLatest(@Query("count") count: Int): Response<List<Post>>

    @GET("api/users/{id}")
    suspend fun getUserById(@Path("id") id: Long): Response<User>

}