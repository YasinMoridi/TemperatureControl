package com.yasinmoridi.temperaturecontrol.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import com.yasinmoridi.temperaturecontrol.data.dataStore.DataStoreRepository
import com.yasinmoridi.temperaturecontrol.data.dataStore.DataStoreRepositoryImpl
import com.yasinmoridi.temperaturecontrol.utils.Constants
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

/**
 * Koin module for DataStore and local persistence dependencies.
 */
val dataStoreModule = module {
    // Provides a singleton instance of DataStore<Preferences>
    single<DataStore<Preferences>> {
        PreferenceDataStoreFactory.create(produceFile = {
            androidContext().preferencesDataStoreFile(Constants.DATASTORE_NAME)
        })
    }
    
    // Binds the DataStoreRepository implementation to its interface
    singleOf(::DataStoreRepositoryImpl) bind DataStoreRepository::class
}
