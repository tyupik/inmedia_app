package ru.netology.inmedia.work

import android.content.Context
import androidx.work.Configuration
import androidx.work.WorkManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ru.netology.inmedia.repository.EventRepository
import ru.netology.inmedia.repository.PostRepository
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class WorkManagerModule {

    @Singleton
    @Provides
    fun provideWorkManager(
        @ApplicationContext
        context: Context,
//        workerFactory:WorkManagerModule,
        repository: PostRepository,
        eventRepository: EventRepository
    ): WorkManager {
        WorkManager.initialize(
            context, Configuration.Builder()
                .setWorkerFactory(WorkerFactoryDelegate(repository, eventRepository))
                .build())

        return WorkManager.getInstance(context)
    }
}