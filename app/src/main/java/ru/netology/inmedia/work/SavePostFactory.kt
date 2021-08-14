package ru.netology.inmedia.work

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import ru.netology.inmedia.repository.PostRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SavePostFactory @Inject constructor(
    private val repository: PostRepository
) : WorkerFactory() {
    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker? =
        if (workerClassName == SavePostWorker::class.java.name) {
            SavePostWorker(repository, appContext, workerParameters)
        } else {
            null
        }
}