package ru.netology.inmedia.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.netology.inmedia.dto.Post
import java.time.Instant

@Entity
data class PostWorkEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val authorId: Long,
    val author: String,
    val authorAvatar: String?,
    val content: String,
    val published: Long,
    @Embedded
    val coords: CoordinatesEmbeddable?,
    val link: String? = null,
//    val mentionIds: Set<Long> = emptySet(),
//    val mentionedMe: Boolean = false,
//    val likeOwnerIds: Set<Long> = emptySet(),
    val likedByMe: Boolean = false,
    @Embedded
    val attachment: AttachmentEmbeddable?,
    var uri: String? = null
) {
    fun toDto() =
        Post(
            id = id,
            authorId = authorId,
            author = author,
            authorAvatar = authorAvatar,
            content = content,
            published = Instant.ofEpochMilli(published),
            coords = coords?.toDto(),
            link = link,
//            mentionIds = mentionIds,
//            mentionedMe = mentionedMe,
//            likeOwnerIds = likeOwnerIds,
            likedByMe = likedByMe,
            attachment = attachment?.toDto(),
        )

    companion object {
        fun fromDto(dto: Post) =
            PostWorkEntity(
                dto.id,
                dto.authorId,
                dto.author,
                dto.authorAvatar,
                dto.content,
                dto.published.toEpochMilli(),
                CoordinatesEmbeddable.fromDto(dto.coords),
                dto.link,
//                dto.mentionIds,
//                dto.mentionedMe,
//                dto.likeOwnerIds,
                dto.likedByMe,
                AttachmentEmbeddable.fromDto(dto.attachment)
            )
    }
}
