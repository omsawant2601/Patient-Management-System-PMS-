package com.cliniccare.app.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    // Android emulator connects to Windows localhost
    private const val BASE_URL =
        "http://10.0.2.2:8080/"

    val api: ClinicApi by lazy {

        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(
                GsonConverterFactory.create()
            )
            .build()
            .create(ClinicApi::class.java)
    }
}