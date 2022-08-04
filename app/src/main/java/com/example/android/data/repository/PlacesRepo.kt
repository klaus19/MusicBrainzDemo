package com.example.android.data.repository


import com.example.android.network.MB_API
import com.example.android.network.SafeApiCall
import javax.inject.Inject

class PlacesRepo @Inject constructor(
    private val mbApi: MB_API
) : SafeApiCall {

    suspend fun searchEntity(
        entity: String?,
        searchTerm: String?,
        limit: Int,
        offset: Int
    ) = safeApiCall { mbApi.searchEntity(entity, searchTerm, limit, offset) }
}