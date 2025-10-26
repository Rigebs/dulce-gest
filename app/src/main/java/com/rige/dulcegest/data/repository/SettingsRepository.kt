package com.rige.dulcegest.data.repository

import com.rige.dulcegest.data.local.dao.SettingsDao
import javax.inject.Inject

class SettingsRepository @Inject constructor(
    private val settingsDao: SettingsDao
) {
    suspend fun deleteAllData() {
        settingsDao.deleteAllData()
    }
}