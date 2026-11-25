package com.cliniccare.app.data.remote

import com.cliniccare.app.data.model.HealthResponse
import retrofit2.http.GET

interface ClinicApi {

    @GET("api/v1/health")
    suspend fun checkHealth(): HealthResponse
}