package ru.netology.inmedia.dto

import ru.netology.inmedia.enumiration.EventType
import java.time.Instant

data class Event(
    val id: Long,
    val authorId: Long,
    val author: String,
    val authorAvatar: String?,
    val content: String,
    /**
     * Дата и время проведения
     */
    val datetime: String,
    val published: String,
    /**
     * Координаты проведения
     */
    val coords: Coordinates? = null,
    /**
     * Типы события
     */
    val type: EventType,
    /**
     * Id'шники залайкавших
     */
    val likeOwnerIds: Set<Long> = emptySet(),
    /**
     * Залайкал ли я
     */
    val likedByMe: Boolean = false,
    /**
     * Id'шники спикеров
     */
    val speakerIds: Set<Long> = emptySet(),
    /**
     * Id'шники участников
     */
    val participantsIds: Set<Long> = emptySet(),
    /**
     * Участвовал ли я
     */
    val participatedByMe: Boolean = false,
    val attachment: Attachment? = null,
    val ownedByMe: Boolean = false
)
