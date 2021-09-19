package ru.netology.inmedia.model

import ru.netology.inmedia.dto.Job

data class JobFeedModel(
    val jobs: List<Job> = emptyList(),
    val empty: Boolean = false,
)

