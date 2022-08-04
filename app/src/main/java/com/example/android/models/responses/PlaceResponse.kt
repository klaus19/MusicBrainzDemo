package com.example.android.models.responses


import com.google.gson.annotations.SerializedName
import com.example.android.models.entities.place.Place

data class PlaceResponse(
    @SerializedName("count")
    val count: Int?,

    @SerializedName("places")
    val places: ArrayList<Place>?
)