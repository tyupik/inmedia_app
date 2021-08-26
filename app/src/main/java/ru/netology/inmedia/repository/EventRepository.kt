package ru.netology.inmedia.repository

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import ru.netology.inmedia.dto.Event
import ru.netology.inmedia.dto.Media
import ru.netology.inmedia.dto.MediaUpload

interface EventRepository {
    val data: Flow<PagingData<Event>>

    suspend fun likeById(id: Long)
    suspend fun dislikeById(id: Long)
    suspend fun save(event: Event)
    suspend fun removeById(id: Long)
    suspend fun getLatestEvents()
    suspend fun participateById(id: Long)
    suspend fun unparticipateById(id: Long)
    suspend fun saveWithAttachment(event: Event, upload: MediaUpload)
    suspend fun upload(upload: MediaUpload): Media
    suspend fun saveWork(event: Event, upload: MediaUpload?): Long
    suspend fun processWork(id: Long)
}