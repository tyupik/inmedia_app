package ru.netology.inmedia.repository

import android.content.Context
import android.net.Uri
import androidx.core.net.toFile
import androidx.core.net.toUri
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.map
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.map
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import ru.netology.inmedia.api.EventApiService
import ru.netology.inmedia.api.EventRemoteMediator
import ru.netology.inmedia.dao.EventDao
import ru.netology.inmedia.dao.EventKeyDao
import ru.netology.inmedia.dao.EventWorkDao
import ru.netology.inmedia.db.AppDb
import ru.netology.inmedia.dto.Attachment
import ru.netology.inmedia.dto.Event
import ru.netology.inmedia.dto.Media
import ru.netology.inmedia.dto.MediaUpload
import ru.netology.inmedia.entity.EventWorkEntity
import ru.netology.inmedia.entity.EventEntity
import ru.netology.inmedia.entity.TypeEmbeddable
import ru.netology.inmedia.entity.toEntity
import ru.netology.inmedia.enumiration.AttachmentType
import ru.netology.inmedia.error.ApiError
import ru.netology.inmedia.error.AppError
import ru.netology.inmedia.error.NetworkError
import ru.netology.inmedia.error.UnknownError
import java.io.IOException
import java.time.Instant
import javax.inject.Inject

class EventRepositoryImpl @Inject constructor(
    private val eventApiService: EventApiService,
    eventKeyDao: EventKeyDao,
    db: AppDb,
    private val eventDao: EventDao,
    private val  eventWorkDao: EventWorkDao,
    @ApplicationContext
    context: Context
) : EventRepository {

    private val userInfoPrefs = context.getSharedPreferences("user", Context.MODE_PRIVATE)

    @ExperimentalPagingApi
    override val data = Pager(
        remoteMediator = EventRemoteMediator(eventApiService, eventDao, db, eventKeyDao),
        config = PagingConfig(pageSize = 10),
        pagingSourceFactory = eventDao::getPagingSource
    ).flow.map {
        it.map(EventEntity::toDto)
    }

    override suspend fun likeById(id: Long) {
        try {
            val response = eventApiService.likeById(id)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val body = response.body() ?: throw ApiError(response.code(), response.message())
            eventDao.insert(EventEntity.fromDto(body))
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun dislikeById(id: Long) {
        try {
            val response = eventApiService.dislikeById(id)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val body = response.body() ?: throw ApiError(response.code(), response.message())
            eventDao.insert(EventEntity.fromDto(body))
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun save(event: Event) {
        try {
            val response = eventApiService.save(event)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val body = response.body() ?: throw ApiError(response.code(), response.message())
            val name = userInfoPrefs.getString("userName", "")
            val avatar = userInfoPrefs.getString("userAvatar", "")
            eventDao.insert(EventEntity.fromDto(body.copy(author = name.toString(), authorAvatar = avatar)))
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun removeById(id: Long) {
        try {
            val response = eventApiService.removeById(id)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            eventDao.removeById(id)
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun getLatestEvents() {
        try {
            val response = eventApiService.getLatest(10)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val body = response.body() ?: throw ApiError(response.code(), response.message())
            eventDao.insert(body.toEntity())
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun participateById(id: Long) {
        try {
            val response = eventApiService.participateById(id)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val body = response.body() ?: throw ApiError(response.code(), response.message())
            eventDao.insert(EventEntity.fromDto(body))
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun unparticipateById(id: Long) {
        try {
            val response = eventApiService.unparticipateById(id)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val body = response.body() ?: throw ApiError(response.code(), response.message())
            eventDao.insert(EventEntity.fromDto(body))
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun saveWithAttachment(event: Event, upload: MediaUpload) {
        try {
            val media = upload(upload)
            val eventWithAttachment =
                event.copy(attachment = Attachment(media.url, AttachmentType.IMAGE))
            save(eventWithAttachment)
        } catch (e: AppError) {
            throw e
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun upload(upload: MediaUpload): Media {
        try {
            println (upload.file.name)
            val media = MultipartBody.Part.createFormData(
                "file", upload.file.name, upload.file.asRequestBody()
            )
            println(media.body)

            val response = eventApiService.upload(media)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }

            return response.body() ?: throw ApiError(response.code(), response.message())
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun saveWork(event: Event, upload: MediaUpload?): Long {
        try {
            val entity = EventWorkEntity.fromDto(event).apply {
                if (upload != null) {
                    this.uri = upload.file.toUri().toString()
                }
            }
            return eventWorkDao.insert(entity)
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun processWork(id: Long) {
        try {
            val entity = eventWorkDao.getById(id)

            val event = Event(
                id = 0L,
                authorId = entity.authorId,
                author = entity.author,
                authorAvatar = entity.authorAvatar,
                content = entity.content,
                published = Instant.now().toString(),
                likedByMe = entity.likedByMe,
                datetime = entity.datetime,
                type = TypeEmbeddable.toDto(entity.type),
                participatedByMe = entity.participatedByMe

                )
            if (entity.uri != null) {
                val upload = MediaUpload(Uri.parse(entity.uri).toFile())
                saveWithAttachment(event, upload)
            } else {
                println (event)
                save(event)
            }
        } catch (e: Exception) {
            throw UnknownError
        }
    }
}