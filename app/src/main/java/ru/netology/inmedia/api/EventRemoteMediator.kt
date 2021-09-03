package ru.netology.inmedia.api

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import ru.netology.inmedia.dao.EventDao
import ru.netology.inmedia.dao.EventKeyDao
import ru.netology.inmedia.db.AppDb
import ru.netology.inmedia.entity.EventRemoteKeyEntity
import ru.netology.inmedia.entity.EventEntity
import ru.netology.inmedia.error.ApiError


@ExperimentalPagingApi
class EventRemoteMediator(
    private val eventApiService: EventApiService,
    private val eventDao: EventDao,
    private val db: AppDb,
    private val eventKeyDao: EventKeyDao
) : RemoteMediator<Int, EventEntity>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, EventEntity>
    ): MediatorResult {

        try {

            val pageSize = state.config.pageSize
            val response = when (loadType) {
                LoadType.REFRESH -> {
                    eventApiService.getLatest(pageSize)

                }
                LoadType.PREPEND -> {
                    //Отключение автоматического PREPEND
                    return MediatorResult.Success(true)
                }
                LoadType.APPEND -> {
                    val id = eventKeyDao.min() ?: return MediatorResult.Success(false)
                    eventApiService.getBefore(id, pageSize)
                }

            }
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val body = response.body() ?: throw ApiError(
                response.code(), response.message()
            )

            if (body.isEmpty()) {
                return MediatorResult.Success(body.isEmpty())
            }
            db.withTransaction {
                when (loadType) {
                    LoadType.REFRESH -> {
                        eventKeyDao.insert(
                            listOf(
                                EventRemoteKeyEntity(
                                    EventRemoteKeyEntity.Type.PREPEND,
                                    body.first().id
                                ),
                                EventRemoteKeyEntity(
                                    EventRemoteKeyEntity.Type.APPEND,
                                    body.last().id
                                )
                            )
                        )
                    }
                    LoadType.PREPEND -> {
                        eventKeyDao.insert(
                            EventRemoteKeyEntity(
                                EventRemoteKeyEntity.Type.PREPEND,
                                body.first().id
                            )
                        )
                    }
                    LoadType.APPEND -> {
                        eventKeyDao.insert(
                            EventRemoteKeyEntity(
                                EventRemoteKeyEntity.Type.APPEND,
                                body.last().id
                            )
                        )
                    }
                }

                eventDao.insert(body.map(EventEntity.Companion::fromDto))
            }

            return MediatorResult.Success(false)
        } catch (e: Exception) {
            return MediatorResult.Error(e)
        }
    }

}
