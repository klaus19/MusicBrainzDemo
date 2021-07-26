package com.musicbrainz.android.network

import com.musicbrainz.android.models.entities.MBEntityTypes.Companion.PLACE
import com.musicbrainz.android.network.APIConstant.LIMIT
import com.musicbrainz.android.network.APIConstant.OFFSET
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test


class MBClientTest {

    private val mbApi: MB_API = MBClient().buildApi(MB_API::class.java)

    @Test
    fun `GET Places`() {
        runBlocking {
            val placesResponse = mbApi.searchEntity(PLACE, "Kaunas", LIMIT, OFFSET)
            Assert.assertNotNull(placesResponse).apply {

            }
        }
    }
}