package ru.netology.inmedia.work

import androidx.work.DelegatingWorkerFactory
import ru.netology.inmedia.repository.PostRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WorkerFactoryDelegate @Inject constructor(
    repository: PostRepository
): DelegatingWorkerFactory() {

    init {
        addFactory(RefreshPostFactory(repository))
        addFactory(SavePostFactory(repository))
        addFactory(RemovePostsFactory(repository))
    }
}