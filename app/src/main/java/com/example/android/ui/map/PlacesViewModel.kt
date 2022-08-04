package com.example.android.ui.map

import androidx.lifecycle.*
import com.example.android.Constants.PLACE_OPEN_FROM
import com.example.android.data.repository.PlacesRepo
import com.example.android.extensions.removeMonthAndDate
import com.example.android.models.entities.MBEntityTypes.Companion.PLACE
import com.example.android.models.responses.PlaceResponse
import com.example.android.network.APIConstant.LIMIT
import com.example.android.network.APIConstant.OFFSET
import com.example.android.network.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class PlacesViewModel @Inject constructor(
    private val searchPlaceRepo: PlacesRepo,
) : ViewModel() {

    private val _placeResponse = MutableLiveData<Resource<PlaceResponse>>()
    val placeResponse: LiveData<Resource<PlaceResponse>> = _placeResponse

    /**
     * We need only those places who can satisfied these conditions.
     * 1-LifeSpan & Begin date is not null
     * 2-Begin date is after PLACE_OPEN_FROM i.e 1990
     * 3-Coordinates (Latitude & Longitude) is not Null
     */
    fun search(query: String) {
        viewModelScope.launch {
            _placeResponse.value = Resource.Loading
            val placeResponse: Resource<PlaceResponse> =
                searchPlaceRepo.searchEntity(PLACE, query, LIMIT, OFFSET)

            if (placeResponse is Resource.Success) {
                val places = placeResponse.value.places?.toMutableList()
                places?.filter { place ->
                    place.lifeSpan?.begin != null
                            &&
                            place.lifeSpan.begin.isNotEmpty()
                            &&
                            place.lifeSpan.begin.removeMonthAndDate()
                                .toInt() > PLACE_OPEN_FROM
                            &&
                            !place.coordinates?.latitude.isNullOrBlank()
                            &&
                            !place.coordinates?.longitude.isNullOrBlank()
                }?.let {
                    placeResponse.value.places.clear()
                    placeResponse.value.places.addAll(it)
                } ?: placeResponse.value.places?.clear()

            }
            _placeResponse.value = placeResponse
        }


    }


}