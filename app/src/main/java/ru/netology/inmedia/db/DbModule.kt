package ru.netology.inmedia.db

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ru.netology.inmedia.dao.PostDao
import ru.netology.inmedia.dao.PostKeyDao
import ru.netology.inmedia.dao.PostWorkDao
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class DbModule {

    @Singleton
    @Provides
    fun provideDb(
        @ApplicationContext
        context: Context
    ): AppDb = Room.databaseBuilder(context, AppDb::class.java, "app.db")
        .fallbackToDestructiveMigration()
        .build()

    @Provides
    fun providePostDao(db: AppDb): PostDao = db.postDao()

    @Provides
    fun providePostKeyDao(db: AppDb): PostKeyDao = db.postRemoteKeyDao()

    @Provides
    fun providePostWorkDao(db: AppDb): PostWorkDao = db.postWorkDao()
}