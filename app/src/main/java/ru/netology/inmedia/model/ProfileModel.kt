package ru.netology.inmedia.model

import ru.netology.inmedia.dto.User

sealed interface ProfileModel {
    val id: Long
}

data class UserModel(
    val user: User
): FeedModel {
    override val id: Long = user.id
}