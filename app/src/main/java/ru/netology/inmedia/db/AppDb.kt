package ru.netology.inmedia.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ru.netology.inmedia.dao.Converters
import ru.netology.inmedia.dao.PostDao
import ru.netology.inmedia.dao.PostKeyDao
import ru.netology.inmedia.entity.PostEntity
import ru.netology.inmedia.entity.PostRemoteKeyEntity

@Database(entities = [PostEntity::class, PostRemoteKeyEntity::class], version = 1)
@TypeConverters(Converters::class)
abstract class AppDb :RoomDatabase() {
    abstract fun postDao(): PostDao
    abstract fun postRemoteKeyDao(): PostKeyDao

}