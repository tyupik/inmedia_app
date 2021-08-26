package ru.netology.inmedia.work

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import ru.netology.inmedia.repository.EventRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RefreshEventFactory @Inject constructor(
    private val repository: EventRepository
): WorkerFactory() {
    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker? =
        if (workerClassName == RefreshEventsWorker::class.java.name) {
            RefreshEventsWorker(repository, appContext, workerParameters)
        } else {
            null
        }
}