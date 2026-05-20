package com.example.ass2.data.remote

import retrofit2.http.GET
import retrofit2.http.Query

interface UsdaApiService {

    @GET("fdc/v1/foods/search")
    suspend fun searchFoods(
        @Query("api_key") apiKey: String,
        @Query("query") query: String,
        @Query("pageSize") pageSize: Int = 15
    ): UsdaSearchResponse
}
