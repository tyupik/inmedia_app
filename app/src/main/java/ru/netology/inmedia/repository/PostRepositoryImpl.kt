package ru.netology.inmedia.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.map
import kotlinx.coroutines.flow.map
import ru.netology.inmedia.api.PostApiService
import ru.netology.inmedia.api.PostRemoteMediator
import ru.netology.inmedia.dao.PostDao
import ru.netology.inmedia.dao.PostKeyDao
import ru.netology.inmedia.db.AppDb
import ru.netology.inmedia.dto.Post
import ru.netology.inmedia.entity.PostEntity
import ru.netology.inmedia.entity.toEntity
import ru.netology.inmedia.error.ApiError
import ru.netology.inmedia.error.NetworkError
import ru.netology.inmedia.error.UnknownError
import java.io.IOException
import javax.inject.Inject

class PostRepositoryImpl @Inject constructor(
    private val postDao: PostDao,
    db: AppDb,
    private val postApiService: PostApiService,
    postKeyDao: PostKeyDao
) : PostRepository {

    @ExperimentalPagingApi
    override val data = Pager(
        remoteMediator = PostRemoteMediator(postApiService, postDao, db, postKeyDao),
        config = PagingConfig(pageSize = 10),
        pagingSourceFactory = postDao::getPagingSource
    ).flow.map {
        it.map(PostEntity::toDto)
    }

    override suspend fun getLatestPosts() {
        try {
            val response = postApiService.getLatest(10)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val body = response.body() ?: throw ApiError(response.code(), response.message())
            postDao.insert(body.toEntity())
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun save(post: Post) {
        try {
            val response = postApiService.save(post)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val body = response.body() ?: throw ApiError(response.code(), response.message())
            postDao.insert(PostEntity.fromDto(body))
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun removeById(id: Long) {
        try {
            val response = postApiService.removeById(id)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            postDao.removeById(id)
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun likeById(id: Long) {
        try {
            val response = postApiService.likeById(id)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val body = response.body() ?: throw ApiError(response.code(), response.message())
            postDao.insert(PostEntity.fromDto(body))
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun dislikeById(id: Long) {
        try {
            val response = postApiService.dislikeById(id)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val body = response.body() ?: throw ApiError(response.code(), response.message())
            postDao.insert(PostEntity.fromDto(body))
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }


}