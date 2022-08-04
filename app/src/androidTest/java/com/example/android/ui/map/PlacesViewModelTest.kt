package com.example.android.ui.map

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import com.example.android.data.repository.PlacesRepo
import com.example.android.getOrAwaitValue
import com.example.android.models.responses.PlaceResponse
import com.example.android.network.MBClient
import com.example.android.network.MB_API
import com.example.android.network.Resource
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PlacesViewModelTest {
    //TODO Will inject all these properties via hilt
    private lateinit var viewModel: PlacesViewModel
    private lateinit var placesRepo: PlacesRepo
    private lateinit var mbApi: MB_API

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {

        mbApi = MBClient().buildApi(MB_API::class.java)
        placesRepo = PlacesRepo(mbApi)
        viewModel = PlacesViewModel(placesRepo)
    }

    @Test
    fun testPlacesViewModel() {
        /**
         * To test the viewmodel via mock data source
         */
//        val context = ApplicationProvider.getApplicationContext<Context>()
//        val jsonString = loadJSONFromAsset(context, "place_response.json")
//        val placeResponse: PlaceResponse = Gson().fromJson(jsonString, PlaceResponse::class.java)
//        viewModel.placeResponse.value = Resource.Success(placeResponse)


        viewModel.search("Eiffel Tower")
        val placeResource: Resource<PlaceResponse> = viewModel.placeResponse.getOrAwaitValue()

        if (placeResource is Resource.Success) {
            val result = placeResource.value.places != null

            placeResource.value.places?.forEach {
                assertThat(it.lifeSpan != null).isTrue()
            }

            assertThat(result).isTrue()

        }

    }

}