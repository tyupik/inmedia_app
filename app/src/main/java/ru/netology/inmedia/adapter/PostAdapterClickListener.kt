package ru.netology.inmedia.adapter

import ru.netology.inmedia.dto.Post

interface PostAdapterClickListener {
    fun onEditClicked(post: Post)
    fun onRemoveClicked(post: Post)
    fun onLikeClicked(post: Post)
    fun onAttachmentClicked(post: Post)
}
