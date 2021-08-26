package ru.netology.inmedia.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ru.netology.inmedia.entity.EventWorkEntity

@Dao
interface EntityWorkDao {
    @Query("SELECT * FROM EventWorkEntity WHERE id = :id")
    suspend fun getById(id: Long): EventWorkEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(work: EventWorkEntity): Long

    @Query("DELETE FROM EventWorkEntity WHERE id = :id")
    suspend fun removeById(id: Long)
}