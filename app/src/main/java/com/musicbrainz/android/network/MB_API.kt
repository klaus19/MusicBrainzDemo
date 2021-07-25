package com.musicbrainz.android.network

import com.musicbrainz.android.models.responses.PlaceResponse
import okhttp3.ResponseBody
import retrofit2.http.*

interface MB_API {

    @GET("{entity}/")
    suspend fun searchEntity(@Path("entity") entity: String?,
                             @Query("query") searchTerm: String?,
                             @Query("limit") limit: Int,
                             @Query("offset") offset: Int): PlaceResponse
}