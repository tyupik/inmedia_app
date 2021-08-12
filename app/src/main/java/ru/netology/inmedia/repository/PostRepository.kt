package ru.netology.inmedia.repository

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import ru.netology.inmedia.dto.Media
import ru.netology.inmedia.dto.MediaUpload
import ru.netology.inmedia.dto.Post

interface PostRepository {
    val data: Flow<PagingData<Post>>

    suspend fun likeById(id: Long)
    suspend fun dislikeById(id: Long)
    suspend fun save(post: Post)
    suspend fun removeById(id: Long)
    suspend fun getLatestPosts()
//    suspend fun saveWithAttachment(post: Post, upload: MediaUpload)
//    suspend fun upload(upload: MediaUpload): Media
}