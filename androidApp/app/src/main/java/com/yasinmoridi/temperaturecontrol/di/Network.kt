package com.yasinmoridi.temperaturecontrol.di

import com.yasinmoridi.temperaturecontrol.data.remote.api.ApiService
import com.yasinmoridi.temperaturecontrol.utils.Constants
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Koin module for network-related dependencies using Retrofit.
 */
val networkModule = module {

    // Provides a singleton instance of Retrofit configured with Gson converter
    single {
        Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // Provides the ApiService implementation created by Retrofit
    single {
        get<Retrofit>().create(ApiService::class.java)
    }
}
