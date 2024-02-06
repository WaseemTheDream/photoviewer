package com.example.android.photoviewer.ui.photoslist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.android.photoviewer.data.model.Photo
import com.example.android.photoviewer.data.repository.AppSettingsRepository
import com.example.android.photoviewer.data.repository.PhotoRepository
import com.example.android.photoviewer.ui.model.DisplayStyle
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class PhotosListViewModel @Inject constructor(
    private val appSettingsRepository: AppSettingsRepository,
    private val photoRepository: PhotoRepository
) : ViewModel() {

    private val _photosState: MutableStateFlow<PagingData<Photo>> =
        MutableStateFlow(value = PagingData.empty())

    val photosState: MutableStateFlow<PagingData<Photo>> get() = _photosState

    val displayStyleState: Flow<DisplayStyle> = appSettingsRepository.displayStyle

    init {
        viewModelScope.launch {
            getPhotos()
        }
    }

    private suspend fun getPhotos() {
        photoRepository.getPhotos()
            .distinctUntilChanged()
            .cachedIn(viewModelScope)
            .collect {
                _photosState.value = it
            }
    }


    fun updateDisplayStyle(displayStyle: DisplayStyle) {
        viewModelScope.launch(Dispatchers.IO) {
            appSettingsRepository.setDisplayStyle(displayStyle)
        }
    }
}