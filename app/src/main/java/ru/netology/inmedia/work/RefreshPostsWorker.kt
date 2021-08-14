package ru.netology.inmedia.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.netology.inmedia.repository.PostRepository
import java.lang.Exception

class RefreshPostsWorker(
    private val repository: PostRepository,
    applicationContext: Context,
    params: WorkerParameters
) : CoroutineWorker(applicationContext, params) {

    companion object {
        const val name = "ru.netology.inmedia.work.RefreshPostsWorker"
    }

    override suspend fun doWork(): Result = withContext(Dispatchers.Default) {
        try {
            repository.getLatestPosts()
            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.retry()
        }
    }
}