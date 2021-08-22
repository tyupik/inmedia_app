package ru.netology.inmedia.repository

import androidx.paging.*
import kotlinx.coroutines.flow.map
import ru.netology.inmedia.api.PostApiService
import ru.netology.inmedia.api.PostRemoteMediator
import ru.netology.inmedia.api.ProfileApiService
import ru.netology.inmedia.dao.PostDao
import ru.netology.inmedia.dao.PostKeyDao
import ru.netology.inmedia.db.AppDb
import ru.netology.inmedia.dto.User
import ru.netology.inmedia.entity.PostEntity
import ru.netology.inmedia.entity.toEntity
import ru.netology.inmedia.error.ApiError
import ru.netology.inmedia.error.NetworkError
import ru.netology.inmedia.error.UnknownError
import java.io.IOException
import javax.inject.Inject

class ProfileRepositoryImpl @Inject constructor(
    private val profileApiService: ProfileApiService,
    val postApiService: PostApiService,
    val postDao: PostDao,
    db: AppDb,
    postKeyDao: PostKeyDao
        ): ProfileRepository {



    @ExperimentalPagingApi
    override val data = Pager(
        remoteMediator = PostRemoteMediator(postApiService, postDao, db, postKeyDao),
        config = PagingConfig(pageSize = 10),
        pagingSourceFactory = postDao::getPagingSource
    ).flow.map {
        it.map(PostEntity::toDto)
    }

    override suspend fun getUserById(id: Long): User {
        try {
            val response = profileApiService.getUserById(id)
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


    override suspend fun getLatestPosts() {
        try {
            val response = profileApiService.getLatest(10)
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
}