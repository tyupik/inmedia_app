package ru.netology.inmedia.entity


import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class EventRemoteKeyEntity(
    @PrimaryKey
    val type: Type,
    val id: Long,
) {

    enum class Type{
        PREPEND,
        APPEND
    }
}