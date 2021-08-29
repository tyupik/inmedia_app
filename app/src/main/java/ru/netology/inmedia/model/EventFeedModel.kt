package ru.netology.inmedia.model

import ru.netology.inmedia.dto.Event

sealed interface EventFeedModel {
    val id: Long
}

data class EventModel(
    val event: Event
) : EventFeedModel {
    override val id: Long = event.id
}