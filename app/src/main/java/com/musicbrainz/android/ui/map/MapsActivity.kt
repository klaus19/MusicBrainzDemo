package com.musicbrainz.android.ui.map

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.musicbrainz.android.Constants.PLACE_OPEN_FROM
import com.musicbrainz.android.R
import com.musicbrainz.android.databinding.ActivityMapsBinding
import com.musicbrainz.android.extensions.*
import com.musicbrainz.android.models.entities.place.Place
import com.musicbrainz.android.network.APIConstant.LIMIT
import com.musicbrainz.android.network.APIConstant.OFFSET
import com.musicbrainz.android.network.Resource
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import kotlin.collections.ArrayList
import kotlin.concurrent.fixedRateTimer

@AndroidEntryPoint
class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var query: String
    private val totalPlaces = ArrayList<Place>()
    private var timer: Timer? = null

    private val placesViewModel: PlacesViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        binding.apply {
            placesProgressbar.visible(true)

            searchFAB.setOnClickListener {
                query = searchTextInputEditText.text.toString()
                if (query.isEmpty()) {
                    showToast(getString(R.string.search_bar_validation_error_message))
                } else {
                    if (totalPlaces.size > 0) {
                        mMap.clear()
                        totalPlaces.clear()
                    }
                    timer?.cancel()
                    OFFSET = 1
                    placesViewModel.search(query)
                    hideKeyBoard(it)
                }

            }
        }
        placesViewModel
            .placeResponse
            .observe({ lifecycle }) {
                binding.placesProgressbar.visible(it is Resource.Loading)
                when (it) {
                    is Resource.Success -> {
                        it.value.count?.let { it1 ->
                            it.value.places?.let { it3 -> totalPlaces.addAll(it3) }
                            //Make the next api call
                            if ((OFFSET + LIMIT) < it1) {
                                OFFSET += LIMIT
                                placesViewModel.search(query)
                            } else {

                                drawPinsOnMap(totalPlaces, mMap)
                                showToast("Total Drawn Pin(s): ${totalPlaces.size}")
                            }
                        }
                    }
                    is Resource.Failure -> {
                        handleApiError(binding.root, it) {
                            placesViewModel.search(query)
                        }
                    }
                    else -> {
                    }
                }
            }
    }

    /**
     * Before to make api call make sure map is loaded properly
     * else no benefit to make request.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        binding.apply {
            placesProgressbar.visible(false)
            searchGroup.visible(true)

        }
        mMap = googleMap
    }

    /**
     * We need to calculate the lifespan in seconds.
     * Save the marker object in the places list.
     * Save the marker draw time in the places list.
     */
    private fun drawPinsOnMap(
        places: ArrayList<Place>?,
        googleMap: GoogleMap
    ) {
        places?.forEach {
            val openYear = it.lifeSpan?.begin?.removeMonthAndDate()?.toInt()
            it.lifeSpanInSeconds = openYear?.minus(PLACE_OPEN_FROM)

            Log.d(
                MapsActivity::class.java.name,
                "Pin Found From API: ${it.name} Open Year: $openYear LifeSpan: ${it.lifeSpanInSeconds}"
            )

            it.coordinates?.apply {
                val latLng =
                    LatLng(latitude.toDouble(), longitude.toDouble())
                it.marker = googleMap.addMarker(
                    MarkerOptions().position(latLng).title(it.name)
                )?.apply { showInfoWindow() }

                it.drawTimeInMillis = System.currentTimeMillis()

                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng))

            }
            timer = startTimer(totalPlaces, googleMap)
        }
    }

    /**
     *Set the timer to check the lifespan of the pin
     *and removed if it expire.
     */
    private fun startTimer(places: MutableList<Place>, map: GoogleMap): Timer {
        return fixedRateTimer("timer", false, 0L, 1000) {
            runOnUiThread {
                if (places.isEmpty()) {
                    map.clear()
                    cancel()
                }
                val placesIterator = places.iterator()

                for (it in placesIterator) {
                    val now = System.currentTimeMillis()
                    it.drawTimeInMillis?.let { it1 ->

                        val elapsedTime = (now - it1) / 1000
                        Log.d(
                            MapsActivity::class.java.name,
                            "Pin Name: ${it.name} Elapsed Time $elapsedTime LifeSpan ${it.lifeSpanInSeconds}"
                        )
                        if (elapsedTime >= it.lifeSpanInSeconds!!) {
                            showToast("${it.name} pin is removed after ${it.lifeSpanInSeconds} secs.")
                            it.marker?.remove()
                            placesIterator.remove()
                        }

                    }

                }
            }
        }
    }

    override fun onStop() {
        timer?.cancel()
        super.onStop()
    }

    override fun onDestroy() {
        timer?.cancel()
        super.onDestroy()
    }
}