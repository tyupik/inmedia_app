package ru.netology.inmedia.model

import ru.netology.inmedia.dto.Post

sealed interface FeedModel {
    val id: Long
}

data class PostModel(
    val post: Post
): FeedModel {
    override val id: Long = post.id
}
