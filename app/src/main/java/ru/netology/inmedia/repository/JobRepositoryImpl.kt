package ru.netology.inmedia.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import ru.netology.inmedia.dao.JobDao
import ru.netology.inmedia.dto.Job
import ru.netology.inmedia.entity.JobEntity
import ru.netology.inmedia.entity.toDto
import javax.inject.Inject

class JobRepositoryImpl @Inject constructor(
    dao: JobDao

        ) : JobRepository {
    override val data: Flow<List<Job>> = dao.getAllUserJobs()
        .map(List<JobEntity>::toDto)
        .flowOn(Dispatchers.Default)

    override suspend fun save(job: Job) {
        TODO("Not yet implemented")
    }

    override suspend fun getAllMyJobs() {
        TODO("Not yet implemented")
    }

    override suspend fun removeJobById(id: Long) {
        TODO("Not yet implemented")
    }
}