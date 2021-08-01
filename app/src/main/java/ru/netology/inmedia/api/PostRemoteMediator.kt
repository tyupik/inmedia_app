package ru.netology.inmedia.api

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import ru.netology.inmedia.dao.PostDao
import ru.netology.inmedia.dao.PostKeyDao
import ru.netology.inmedia.db.AppDb
import ru.netology.inmedia.entity.PostEntity
import ru.netology.inmedia.entity.PostRemoteKeyEntity
import ru.netology.inmedia.error.ApiError

@ExperimentalPagingApi
class PostRemoteMediator(
    private val postApiService: PostApiService,
    private val postDao: PostDao,
    private val db: AppDb,
    private val postKeyDao: PostKeyDao
) : RemoteMediator<Int, PostEntity>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, PostEntity>
    ): MediatorResult {

//        val lastId = postKeyDao.max() ?: 0
        try {

            val pageSize = state.config.pageSize
            val response = when (loadType) {
                LoadType.REFRESH -> {
                    postApiService.getLatest(pageSize)

                }
                LoadType.PREPEND -> {
                    //Отключение автоматического PREPEND
                    return MediatorResult.Success(true)
                }
                LoadType.APPEND -> {
                    val id = postKeyDao.min() ?: return MediatorResult.Success(false)
                    postApiService.getBefore(id, pageSize)
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
                        postKeyDao.insert(
                            listOf(
                                PostRemoteKeyEntity(
                                    PostRemoteKeyEntity.Type.PREPEND,
                                    body.first().id
                                ),
                                PostRemoteKeyEntity(
                                    PostRemoteKeyEntity.Type.APPEND,
                                    body.last().id
                                )
                            )
                        )
//                        postDao.removeAll()
                    }
                    LoadType.PREPEND -> {
                        postKeyDao.insert(
                            PostRemoteKeyEntity(
                                PostRemoteKeyEntity.Type.PREPEND,
                                body.first().id
                            )
                        )
                    }
                    LoadType.APPEND -> {
                        postKeyDao.insert(
                            PostRemoteKeyEntity(
                                PostRemoteKeyEntity.Type.APPEND,
                                body.last().id
                            )
                        )
                    }
                }

                postDao.insert(body.map(PostEntity.Companion::fromDto))
            }
            val lastId = body.first().id

            return MediatorResult.Success(false)
        } catch (e: Exception) {
            return MediatorResult.Error(e)
        }
    }

}
