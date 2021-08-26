package ru.netology.inmedia.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.netology.inmedia.repository.EventRepository
import ru.netology.inmedia.repository.PostRepository
import java.lang.Exception

class RefreshEventsWorker(
    private val repository: EventRepository,
    applicationContext: Context,
    params: WorkerParameters
) : CoroutineWorker(applicationContext, params) {

    companion object {
        const val name = "ru.netology.inmedia.work.RefreshEventWorker"
    }

    override suspend fun doWork(): Result = withContext(Dispatchers.Default) {
        try {
            repository.getLatestEvents()
            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.retry()
        }
    }
}