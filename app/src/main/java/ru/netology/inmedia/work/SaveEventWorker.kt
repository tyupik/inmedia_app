package ru.netology.inmedia.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import ru.netology.inmedia.repository.EventRepository

class SaveEventWorker(
    private val repository: EventRepository,
    applicationContext: Context,
    params: WorkerParameters
) : CoroutineWorker(applicationContext, params) {

    companion object {
        const val eventKey = "event"
    }

    override suspend fun doWork(): Result {
        val id = inputData.getLong(eventKey, 0L)
//        if (id == 0L) {
//            return Result.failure()
//        }

        return try {
            repository.processWork(id)
            Result.success()
        }catch (e: Exception) {
            Result.retry()
        } catch (e: Exception) {
            Result.failure()
        }
    }
}