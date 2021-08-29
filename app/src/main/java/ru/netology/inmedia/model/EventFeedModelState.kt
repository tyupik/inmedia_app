package ru.netology.inmedia.model

data class EventFeedModelState(
    val loading: Boolean = false,
    val error: Boolean = false,
    val refreshing: Boolean = false,
    val empty: Boolean = true
)
