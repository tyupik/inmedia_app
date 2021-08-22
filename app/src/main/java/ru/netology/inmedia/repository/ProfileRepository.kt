package ru.netology.inmedia.repository

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import ru.netology.inmedia.dto.Post
import ru.netology.inmedia.dto.User

interface ProfileRepository {
    val data: Flow<PagingData<Post>>

    suspend fun getUserById(id: Long): User
    suspend fun getLatestPosts()
}