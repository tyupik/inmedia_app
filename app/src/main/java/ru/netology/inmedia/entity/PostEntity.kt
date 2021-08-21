package ru.netology.inmedia.entity

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.netology.inmedia.dto.Attachment
import ru.netology.inmedia.dto.Coordinates
import ru.netology.inmedia.dto.Post
import ru.netology.inmedia.enumiration.AttachmentType
import java.time.Instant

@Entity
data class PostEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val authorId: Long,
    val author: String,
    val authorAvatar: String?,
    val content: String,
    val published: String,
    @Embedded
    val coords: CoordinatesEmbeddable?,
    val link: String? = null,
//    val mentionIds: Set<Long> = emptySet(),
//    val mentionedMe: Boolean = false,
//    val likeOwnerIds: Set<Long> = emptySet(),
    val likedByMe: Boolean = false,
    @Embedded
    val attachment: AttachmentEmbeddable?,
) {
    fun toDto() =
        Post(
            id = id,
            authorId = authorId,
            author = author,
            authorAvatar = authorAvatar,
            content = content,
            published = published,
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
            PostEntity(
                dto.id,
                dto.authorId,
                dto.author,
                dto.authorAvatar,
                dto.content,
                dto.published,
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


//fun List<PostEntity>.toDto(): List<Post> = map(PostEntity::toDto)
fun List<Post>.toEntity(): List<PostEntity> = map(PostEntity::fromDto)


data class AttachmentEmbeddable(
    var url: String,
    var type: AttachmentType
) {
    fun toDto() = Attachment(url, type)

    companion object {
        fun fromDto(dto: Attachment?) = dto?.let {
            AttachmentEmbeddable(it.url, it.type)
        }
    }
}

data class CoordinatesEmbeddable(
    val lat: Double,
    val longitude: Double,
) {
    fun toDto() = Coordinates(lat, longitude)

    companion object {
        fun fromDto(dto: Coordinates?) = dto?.let {
            CoordinatesEmbeddable(it.lat, it.long)
        }
    }
}

//data class PublishedEmbeddable(
//    var published: Long
//) {
//    fun toDto(): Instant =
//        Instant.ofEpochMilli(published)
//
//    companion object {
//        fun fromDto(dto: Instant) = PublishedEmbeddable(dto.toEpochMilli())
//    }
//
//
//}
