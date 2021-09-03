package ru.netology.inmedia.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.netology.inmedia.dto.Event
import ru.netology.inmedia.enumiration.EventType

@Entity
class EventEntity(
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
    @Embedded
    val type: TypeEmbeddable,
    val likedByMe: Boolean,
    val participatedByMe: Boolean = false,
    @Embedded
    val attachment: AttachmentEmbeddable?,
    val participateCount: Int = 0
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
            type = type.toDto(),
            likedByMe = likedByMe,
            participatedByMe = participatedByMe,
            attachment = attachment?.toDto(),
            participateCount = participateCount
        )

    companion object {
        fun fromDto(dto: Event) =
            EventEntity(
                dto.id,
                dto.authorId,
                dto.author,
                dto.authorAvatar,
                dto.content,
                dto.datetime,
                dto.published,
                CoordinatesEmbeddable.fromDto(dto.coords),
                TypeEmbeddable.fromDto(dto.type),
                dto.likedByMe,
                dto.participatedByMe,
                AttachmentEmbeddable.fromDto(dto.attachment),
                dto.participantsIds.size
            )
    }
}

fun List<Event>.toEntity(): List<EventEntity> = map(EventEntity.Companion::fromDto)

data class TypeEmbeddable(
    val eventType: String
) {
    fun toDto() = if (eventType == "OFFLINE") EventType.OFFLINE else EventType.ONLINE

    companion object {
        fun fromDto(dto: EventType) =
            dto.let {
                if (dto == EventType.ONLINE) TypeEmbeddable("ONLINE") else TypeEmbeddable("OFFLINE")
            }
         fun toDto(evType: TypeEmbeddable) =
             evType.let {
                 if (evType.eventType == "OFFLINE") EventType.OFFLINE else EventType.ONLINE
             }
    }

}
