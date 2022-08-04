package com.example.android.models.entities.place


import com.google.android.gms.maps.model.Marker
import com.google.gson.annotations.SerializedName

data class Place(

    @SerializedName("coordinates")
    val coordinates: Coordinates?,

    @SerializedName("life-span")
    val lifeSpan: LifeSpan?,
    @SerializedName("name")
    val name: String?,

    var lifeSpanInSeconds: Int?,

    var marker: Marker?,

    var drawTimeInMillis: Long?
)