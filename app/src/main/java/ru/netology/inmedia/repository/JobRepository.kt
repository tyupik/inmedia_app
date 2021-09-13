package ru.netology.inmedia.repository

import kotlinx.coroutines.flow.Flow
import ru.netology.inmedia.dto.Job

interface JobRepository {
    val data: Flow<List<Job>>


    suspend fun save(job: Job)
    suspend fun getAllMyJobs()
    suspend fun removeJobById(id: Long)
}