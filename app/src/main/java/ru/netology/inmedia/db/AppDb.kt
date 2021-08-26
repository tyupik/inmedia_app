package ru.netology.inmedia.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ru.netology.inmedia.dao.*
import ru.netology.inmedia.entity.PostEntity
import ru.netology.inmedia.entity.PostRemoteKeyEntity
import ru.netology.inmedia.entity.PostWorkEntity

@Database(entities = [PostEntity::class, PostRemoteKeyEntity::class, PostWorkEntity::class], version = 3, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDb :RoomDatabase() {
    abstract fun postDao(): PostDao
    abstract fun eventDao(): EventDao
    abstract fun postRemoteKeyDao(): PostKeyDao
    abstract fun postWorkDao(): PostWorkDao
    abstract fun eventWorkDao(): EventWorkDao
    abstract fun eventRemoteKeyDao(): EventKeyDao

}