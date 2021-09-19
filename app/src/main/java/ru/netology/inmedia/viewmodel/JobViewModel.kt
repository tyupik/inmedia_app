package ru.netology.inmedia.viewmodel

import androidx.lifecycle.*
import com.google.firebase.installations.FirebaseInstallations
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import ru.netology.inmedia.SingleLiveEvent
import ru.netology.inmedia.dto.Job
import ru.netology.inmedia.entity.TypeEmbeddable
import ru.netology.inmedia.model.JobFeedModel
import ru.netology.inmedia.model.JobFeedModelState
import ru.netology.inmedia.repository.JobRepository
import javax.inject.Inject

private val defaultJob = Job(
    id = 0L,
    name = "",
    position = "",
    start = 0L
)

@HiltViewModel
class JobViewModel @Inject constructor(
    private val jobRepository: JobRepository
) : ViewModel() {

    val data: LiveData<JobFeedModel> = jobRepository.data
//        .map(::JobFeedModel)
        .map { jobs ->
            JobFeedModel(
                jobs.map { it },
                jobs.isEmpty()
            )
        }
        .catch { e ->
            e.printStackTrace()
        }
        .asLiveData(Dispatchers.Default)

    private val _dataState = MutableLiveData<JobFeedModelState>()
    val dataState: LiveData<JobFeedModelState>
        get() = _dataState

    private val edited = MutableLiveData(defaultJob)

    private val _jobCreated = SingleLiveEvent<Unit>()
    val jobCreated: LiveData<Unit>
        get() = _jobCreated

    init {
        loadJobs()
        FirebaseInstallations.getInstance().getToken(true)
    }

    fun loadJobs() = viewModelScope.launch {
        try {
            _dataState.value = JobFeedModelState(loading = true)
            jobRepository.getAllMyJobs()
            _dataState.value = JobFeedModelState()
        } catch (e: Exception) {
            _dataState.value = JobFeedModelState(error = true)
        }
    }

    fun refreshJobs() = viewModelScope.launch {
        try {
            _dataState.value = JobFeedModelState(refreshing = true)
            jobRepository.getAllMyJobs()
            _dataState.value = JobFeedModelState()
        } catch (e: Exception) {
            _dataState.value = JobFeedModelState(error = true)
        }
    }

    fun removeJobById(id: Long) = viewModelScope.launch {
        try {
            jobRepository.removeJobById(id)
            _dataState.value = JobFeedModelState()
        } catch (e: Exception) {
            _dataState.value = JobFeedModelState(error = true)
        }
    }

    fun save() = viewModelScope.launch {
        edited.value?.let { job ->
            _jobCreated.value = Unit
            viewModelScope.launch {
                try {
                    _dataState.value = JobFeedModelState(loading = true)
                    jobRepository.save(job)
                    _dataState.value = JobFeedModelState()
                } catch (e: Exception) {
                    _dataState.value = JobFeedModelState(error = true)
                }
            }
        }
        edited.value = defaultJob
    }

    fun edit(job: Job) {
        edited.value = job
    }

    fun changeContent(startDateTime: Long, name: String, position: String, finishDateTime: Long?) {
        if (finishDateTime == 0L) {
            edited.value = edited.value?.copy(
                start = startDateTime,
                finish = null,
                name = name,
                position = position
            )
        } else {
            edited.value = edited.value?.copy(
                start = startDateTime,
                finish = finishDateTime,
                name = name,
                position = position
            )
        }
    }

}