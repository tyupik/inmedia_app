package ru.netology.inmedia.work

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import ru.netology.inmedia.repository.PostRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RefreshPostFactory @Inject constructor(
    private val repository: PostRepository
): WorkerFactory() {
    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker? =
        if (workerClassName == RefreshPostsWorker::class.java.name) {
            RefreshPostsWorker(repository, appContext, workerParameters)
        } else {
            null
        }
}