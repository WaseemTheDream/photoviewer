package com.example.android.photoviewer.ui.main

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.photoviewer.data.repository.AppSettingsRepository
import com.example.android.photoviewer.ui.theme.AppTheme
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val appSettingsRepository: AppSettingsRepository
) : ViewModel() {

    private val _appTheme = MutableStateFlow(AppTheme.Light)
    val appTheme: StateFlow<AppTheme> = _appTheme

    init {
        readAppTheme()
    }

    fun updateAppTheme(theme: AppTheme) {
        viewModelScope.launch(Dispatchers.IO) {
            appSettingsRepository.setAppTheme(theme)
        }
    }

    private fun readAppTheme() {
        viewModelScope.launch(Dispatchers.IO) {
            appSettingsRepository.appTheme.collect {
                _appTheme.value = it
            }
        }
    }
}
