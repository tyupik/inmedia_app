package ru.netology.inmedia.adapter

import ru.netology.inmedia.dto.Job

interface JobAdapterClickListener {
    fun onEditClicked(job: Job)
    fun onRemoveClicked(job: Job)
}