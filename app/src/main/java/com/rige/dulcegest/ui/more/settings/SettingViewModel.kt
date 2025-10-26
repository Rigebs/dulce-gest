package com.rige.dulcegest.ui.more.settings

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rige.dulcegest.data.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val settingsRepo: SettingsRepository
) : ViewModel() {

    fun deleteAllData() {
        viewModelScope.launch {
            try {
                settingsRepo.deleteAllData()
                Log.d("SettingsViewModel", "Datos eliminados correctamente")
            } catch (e: Exception) {
                Log.e("SettingsViewModel", "Error al eliminar datos: ${e.message}")
            }
        }
    }
}