package ru.netology.inmedia.application

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.*
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.netology.inmedia.auth.AppAuth
import ru.netology.inmedia.work.RefreshPostsWorker
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltAndroidApp
class InMediaApplication : Application(), Configuration.Provider {

    private val appScope = CoroutineScope(Dispatchers.Default)

    @Inject
    lateinit var auth: AppAuth

    @Inject
    lateinit var workManager: WorkManager

    override fun onCreate() {
        super.onCreate()
        setupWork()
    }

    private fun setupWork() {
        appScope.launch {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()
            val request = PeriodicWorkRequestBuilder<RefreshPostsWorker>(1, TimeUnit.MINUTES)
                .setConstraints(constraints)
                .build()
            workManager.enqueueUniquePeriodicWork(
                RefreshPostsWorker.name,
                ExistingPeriodicWorkPolicy.KEEP,
                request
            )
        }
    }

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override fun getWorkManagerConfiguration() =
        Configuration.Builder()
            .setMinimumLoggingLevel(android.util.Log.INFO)
            .setWorkerFactory(workerFactory)
            .build()

}