package com.yasinmoridi.temperaturecontrol.data.dataStore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map

class DataStoreRepositoryImpl(
    private val dataStore: DataStore<Preferences>
) : DataStoreRepository {

    override suspend fun putInt(key: String, value: Int) {
        dataStore.edit { it[intPreferencesKey(key)] = value }
    }

    override suspend fun putBoolean(key: String, value: Boolean) {
        dataStore.edit { it[booleanPreferencesKey(key)] = value }
    }

    override suspend fun putString(key: String, value: String) {
        dataStore.edit { it[stringPreferencesKey(key)] = value }
    }

    override suspend fun putLong(key: String, value: Long) {
        dataStore.edit { it[longPreferencesKey(key)] = value }
    }

    override suspend fun getBoolean(key: String): Boolean? {
        return dataStore.data.map { it[booleanPreferencesKey(key)] }.firstOrNull()
    }

    override suspend fun getInt(key: String): Int? {
        return dataStore.data.map { it[intPreferencesKey(key)] }.firstOrNull()
    }

    override suspend fun getString(key: String): String? {
        return dataStore.data.map { it[stringPreferencesKey(key)] }.firstOrNull()
    }

    override suspend fun getLong(key: String): Long? {
        return dataStore.data.map { it[longPreferencesKey(key)] }.firstOrNull()
    }

    override fun observeInt(key: String): Flow<Int?> = dataStore.data
        .map { it[intPreferencesKey(key)] }

    override fun observeBoolean(key: String): Flow<Boolean?> = dataStore.data
        .map { it[booleanPreferencesKey(key)] }

    override fun observeString(key: String): Flow<String?> = dataStore.data
        .map { it[stringPreferencesKey(key)] }

}