package ru.netology.inmedia.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.netology.inmedia.dto.Event
import ru.netology.inmedia.entitydata.EventEntity
import ru.netology.inmedia.enumiration.EventType

@Entity
class EventWorkEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val authorId: Long,
    val author: String,
    val authorAvatar: String?,
    val content: String,
    val datetime: String,
    val published: String,
    @Embedded
    val coords: CoordinatesEmbeddable?,
    val type: EventType,
    val likedByMe: Boolean = false,
    val participatedByMe: Boolean = false,
    @Embedded
    val attachment: AttachmentEmbeddable?,
    var uri: String? = null
) {
    fun toDto() =
        Event(
            id = id,
            authorId = authorId,
            author = author,
            authorAvatar = authorAvatar,
            content = content,
            datetime = datetime,
            published = published,
            coords = coords?.toDto(),
            type = type,
            likedByMe = likedByMe,
            attachment = attachment?.toDto()
        )

    companion object {
        fun fromDto(dto: Event) =
            EventWorkEntity(
                dto.id,
                dto.authorId,
                dto.author,
                dto.authorAvatar,
                dto.content,
                dto.datetime,
                dto.published,
                CoordinatesEmbeddable.fromDto(dto.coords),
                dto.type,
                dto.likedByMe,
                dto.participatedByMe,
                AttachmentEmbeddable.fromDto(dto.attachment)
            )
    }
}