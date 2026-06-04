package com.yasinmoridi.temperaturecontrol.data.dataStore

import kotlinx.coroutines.flow.Flow

interface DataStoreRepository {
    suspend fun putString(key: String, value: String)
    suspend fun putInt(key: String, value: Int)
    suspend fun putBoolean(key: String, value: Boolean)
    suspend fun putLong(key: String, value: Long)

    suspend fun getString(key: String): String?
    suspend fun getInt(key: String): Int?
    suspend fun getBoolean(key: String): Boolean?
    suspend fun getLong(key: String): Long?

    fun observeInt(key: String): Flow<Int?>
    fun observeBoolean(key: String): Flow<Boolean?>
    fun observeString(key: String): Flow<String?>
}