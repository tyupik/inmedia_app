package ru.netology.inmedia.work

import androidx.work.DelegatingWorkerFactory
import ru.netology.inmedia.repository.EventRepository
import ru.netology.inmedia.repository.PostRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WorkerFactoryDelegate @Inject constructor(
    repository: PostRepository,
    eventRepository: EventRepository
): DelegatingWorkerFactory() {

    init {
        addFactory(RefreshPostFactory(repository))
        addFactory(SavePostFactory(repository))
        addFactory(RemovePostsFactory(repository))
        addFactory(RefreshEventFactory(eventRepository))
        addFactory(SaveEventFactory(eventRepository))
        addFactory(RemoveEventFactory(eventRepository))
    }
}