package com.example.recyclomate.instance

import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

// Define your data class for the API response
data class ApiResponse(
    val message: String // Adjust this based on your actual API response
)

interface ApiService {
    @Multipart
    @POST("/predict")
    suspend fun uploadImage(
        @Part file: MultipartBody.Part
    ): Response<ApiResponse> // Use ApiResponse instead of String
}
