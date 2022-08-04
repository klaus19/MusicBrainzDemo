package com.example.android.network

import com.example.android.models.responses.PlaceResponse
import retrofit2.http.*

interface MB_API {

    @GET("{entity}/")
    suspend fun searchEntity(@Path("entity") entity: String?,
                             @Query("query") searchTerm: String?,
                             @Query("limit") limit: Int,
                             @Query("offset") offset: Int): PlaceResponse
}