package ru.netology.inmedia.ui.profile

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import ru.netology.inmedia.auth.AppAuth
import ru.netology.inmedia.model.*
import ru.netology.inmedia.repository.ProfileRepository
import ru.netology.inmedia.repository.ProfileRepositoryImpl
import java.io.File
import java.lang.Exception
import javax.inject.Inject


private val noPhoto = PhotoModel()

@HiltViewModel
@ExperimentalCoroutinesApi
class ProfileViewModel @Inject constructor(
    private val profileRepository: ProfileRepositoryImpl,
    auth: AppAuth
) : ViewModel() {


    @ExperimentalPagingApi
    private val cached = profileRepository.data.cachedIn(viewModelScope)

    @ExperimentalPagingApi
    val data: Flow<PagingData<FeedModel>> = auth.authStateFlow
        .flatMapLatest { (myId, _) ->
            cached.map { pagingData ->
                pagingData.map { post ->
                    PostModel(post.copy(ownedByMe = post.authorId == myId))
                }
            }
        }



    private val _dataState = MutableLiveData<FeedModelState>()
    val dataState: LiveData<FeedModelState>
        get() = _dataState

    private val _photo = MutableLiveData(noPhoto)
    val photo: LiveData<PhotoModel>
        get() = _photo

    fun changePhoto(uri: Uri?) {
        _photo.value = PhotoModel(uri)
    }

    fun loadPosts() = viewModelScope.launch {
        try {
            _dataState.value = FeedModelState(loading = true)
            profileRepository.getLatestPosts()
            _dataState.value = FeedModelState()
        } catch (e: Exception) {
            _dataState.value = FeedModelState(error = true)
        }
    }

    fun loadUserProfile(id: Long) = viewModelScope.launch {
        try {
            _dataState.value = FeedModelState(loading = true)
            profileRepository.getUserById(id)
        } catch (e: Exception) {
            _dataState.value = FeedModelState(error = true)
        }
    }
}