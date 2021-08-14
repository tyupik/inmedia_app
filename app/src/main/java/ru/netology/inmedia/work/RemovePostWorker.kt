package ru.netology.inmedia.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import ru.netology.inmedia.db.AppDb
import ru.netology.inmedia.repository.PostRepository
import javax.inject.Inject

class RemovePostWorker(
    private val repository: PostRepository,
    applicationContext: Context,
    params: WorkerParameters
) : CoroutineWorker(applicationContext, params) {
    companion object {
        const val postKey = "post"
    }

    @Inject
    lateinit var appDbPWD: AppDb

    override suspend fun doWork(): Result {
        val id = inputData.getLong(postKey, 0L)
        if (id == 0L) {
            return Result.failure()
        }
        return try {
            print(id)
//            appDbPWD.postWorkDao().removeById(id)
            repository.removeById(id)
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        } catch (e: UnknownError) {
            Result.failure()
        }
    }
}