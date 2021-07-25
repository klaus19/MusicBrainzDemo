package com.musicbrainz.android.ui.map

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.musicbrainz.android.data.repository.PlacesRepo
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import com.musicbrainz.android.getOrAwaitValue
import com.musicbrainz.android.models.responses.PlaceResponse
import com.musicbrainz.android.network.MBClient
import com.musicbrainz.android.network.MB_API
import com.musicbrainz.android.network.Resource
import junit.framework.TestCase.assertEquals
import org.junit.Before
import org.junit.Test

import org.junit.Rule
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PlacesViewModelTest {

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