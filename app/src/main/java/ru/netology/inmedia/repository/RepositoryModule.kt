package ru.netology.inmedia.repository

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
interface RepositoryModule {

    @Singleton
    @Binds
    fun bindRepo(impl: PostRepositoryImpl) : PostRepository

    @Singleton
    @Binds
    fun bindProfileRepo(impl: ProfileRepositoryImpl) : ProfileRepository

    @Singleton
    @Binds
    fun bindEventRepo(impl: EventRepositoryImpl) : EventRepository
}