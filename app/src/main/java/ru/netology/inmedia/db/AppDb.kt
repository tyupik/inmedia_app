package ru.netology.inmedia.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ru.netology.inmedia.dao.*
import ru.netology.inmedia.entity.*
import ru.netology.inmedia.entitydata.EventEntity

@Database(
    entities = [
        PostEntity::class,
        PostRemoteKeyEntity::class,
        PostWorkEntity::class,
        EventEntity::class,
        EventRemoteKeyEntity::class,
        EventWorkEntity::class], version = 4, exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDb : RoomDatabase() {
    abstract fun postDao(): PostDao
    abstract fun eventDao(): EventDao
    abstract fun postRemoteKeyDao(): PostKeyDao
    abstract fun postWorkDao(): PostWorkDao
    abstract fun eventWorkDao(): EventWorkDao
    abstract fun eventRemoteKeyDao(): EventKeyDao

}