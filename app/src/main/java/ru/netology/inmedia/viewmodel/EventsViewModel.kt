package ru.netology.inmedia.viewmodel

import android.net.Uri
import androidx.core.net.toFile
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import androidx.work.*
import com.google.firebase.installations.FirebaseInstallations
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import ru.netology.inmedia.SingleLiveEvent
import ru.netology.inmedia.auth.AppAuth
import ru.netology.inmedia.dto.Event
import ru.netology.inmedia.dto.MediaUpload
import ru.netology.inmedia.entity.TypeEmbeddable
import ru.netology.inmedia.enumiration.EventType
import ru.netology.inmedia.model.EventFeedModel
import ru.netology.inmedia.model.EventFeedModelState
import ru.netology.inmedia.model.EventModel
import ru.netology.inmedia.model.PhotoModel
import ru.netology.inmedia.repository.EventRepository
import ru.netology.inmedia.work.RemoveEventWorker
import ru.netology.inmedia.work.SaveEventWorker
import javax.inject.Inject

private val defaultEvent = Event(
    id = 0L,
    authorId = 0L,
    author = "",
    authorAvatar = null,
    content = "",
    datetime = "",
    published = "",
    type = EventType.ONLINE
)

private val noPhoto = PhotoModel()

@HiltViewModel
@ExperimentalCoroutinesApi
class EventsViewModel @Inject constructor(
    private val eventRepository: EventRepository,
    private val workManager: WorkManager,
    auth: AppAuth
) : ViewModel() {

    private val cached = eventRepository.data.cachedIn(viewModelScope)

    val data: Flow<PagingData<EventFeedModel>> = auth.authStateFlow
        .flatMapLatest { (myId, _) ->
            cached.map { pagingData ->
                pagingData.map { event ->
                    print(event)
                    EventModel(
                        event.copy(
                            ownedByMe = event.authorId == myId,
                            participatedByMe = event.participatedByMe
                        )
                    )
                }
            }
        }

    private val _dataState = MutableLiveData<EventFeedModelState>()
    val dataState: LiveData<EventFeedModelState>
        get() = _dataState

    private val edited = MutableLiveData(defaultEvent)

    private val _eventCreated = SingleLiveEvent<Unit>()
    val eventCreated: LiveData<Unit>
        get() = _eventCreated

    private val _photo = MutableLiveData(noPhoto)
    val photo: LiveData<PhotoModel>
        get() = _photo


    init {
        loadEvents()
        FirebaseInstallations.getInstance().getToken(true)
    }

    fun loadEvents() = viewModelScope.launch {
        try {
            _dataState.value = EventFeedModelState(loading = true)
            eventRepository.getLatestEvents()
            _dataState.value = EventFeedModelState()
        } catch (e: Exception) {
            _dataState.value = EventFeedModelState(error = true)
        }
    }

    fun refreshEvents() = viewModelScope.launch {
        try {
            _dataState.value = EventFeedModelState(refreshing = true)
            eventRepository.getLatestEvents()
            _dataState.value = EventFeedModelState()
        } catch (e: Exception) {
            _dataState.value = EventFeedModelState(error = true)
        }
    }

    fun likeById(id: Long) = viewModelScope.launch {
        try {
            eventRepository.likeById(id)
            _dataState.value = EventFeedModelState()
        } catch (e: Exception) {
            _dataState.value = EventFeedModelState(error = true)
        }
    }

    fun dislikeById(id: Long) = viewModelScope.launch {
        try {
            eventRepository.dislikeById(id)
            _dataState.value = EventFeedModelState()
        } catch (e: Exception) {
            _dataState.value = EventFeedModelState(error = true)
        }
    }

    fun participateById(id: Long) = viewModelScope.launch {
        try {
            eventRepository.participateById(id)
            _dataState.value = EventFeedModelState()
        } catch (e: Exception) {
            _dataState.value = EventFeedModelState(error = true)
        }
    }

    fun unparticipateById(id: Long) = viewModelScope.launch {
        try {
            eventRepository.unparticipateById(id)
            _dataState.value = EventFeedModelState()
        } catch (e: Exception) {
            _dataState.value = EventFeedModelState(error = true)
        }
    }

    fun removeById(id: Long) = viewModelScope.launch {
        viewModelScope.launch {
            val data = workDataOf(RemoveEventWorker.eventKey to id)
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()
            val request = OneTimeWorkRequestBuilder<RemoveEventWorker>()
                .setInputData(data)
                .setConstraints(constraints)
                .build()
            workManager.enqueue(request)
        }
    }

    fun changeContent(content: String, datetime: String, type: String) {
        val text = content.trim()
        val datetime = datetime.trim()
        val type = TypeEmbeddable(type).toDto()
        print(edited.value?.content)
        if (text == edited.value?.content && datetime == edited.value?.datetime && type == edited.value?.type) {
            return
        }
        edited.value = edited.value?.copy(content = text, datetime = datetime, type = type)
    }


    fun save(photoValue: PhotoModel) {
        edited.value?.let { event ->
            _eventCreated.value = Unit
            viewModelScope.launch {
                try {
                    val id = eventRepository.saveWork(
                        event,
                        photoValue.uri?.let { MediaUpload(it.toFile()) }
                    )
                    val data = workDataOf(SaveEventWorker.eventKey to id)
                    val constraints = Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build()
                    val request = OneTimeWorkRequestBuilder<SaveEventWorker>()
                        .setInputData(data)
                        .setConstraints(constraints)
                        .build()
                    workManager.enqueue(request)

                    _dataState.value = EventFeedModelState()
                } catch (e: Exception) {
                    e.printStackTrace()
                    _dataState.value = EventFeedModelState(error = true)
                }
            }
        }
        edited.value = defaultEvent
        _photo.value = noPhoto
    }

    fun edit(event: Event) {
        edited.value = event
    }

    fun changePhoto(uri: Uri?) {
        _photo.value = PhotoModel(uri)
    }

}