package ru.netology.inmedia.repository

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
interface PostRepositoryModule {

    @Singleton
    @Binds
    fun bindRepo(impl: PostRepositoryImpl) : PostRepository
}