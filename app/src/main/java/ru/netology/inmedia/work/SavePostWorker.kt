package ru.netology.inmedia.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import ru.netology.inmedia.repository.PostRepository
import java.lang.Exception

class SavePostWorker(
    private val repository: PostRepository,
    applicationContext: Context,
    params: WorkerParameters
) : CoroutineWorker(applicationContext, params) {

    companion object {
        const val postKey = "post"
    }

    override suspend fun doWork(): Result {
        val id = inputData.getLong(postKey, 0L)
        if (id == 0L) {
            return Result.failure()
        }

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