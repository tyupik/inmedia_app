package ru.netology.inmedia.adapter

import ru.netology.inmedia.dto.Event

interface EventAdapterClickListener {
    fun onEditClicked(event: Event)
    fun onRemoveClicked(event: Event)
    fun onLikeClicked(event: Event)
    fun onAttachmentClicked(event: Event)
    fun onParticipateClicked(event: Event)
}