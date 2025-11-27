package com.hfad.egypttour.data.api.model

import kotlinx.serialization.SerialName
import com.google.gson.annotations.SerializedName

/*
* examole response from wikki api
*
* {
    "batchcomplete": "",
    "query": {
        "pages": {
            "18618509": {
                "pageid": 18618509,
                "ns": 0,
                "title": "Wikimedia Foundation",
                "coordinates": [
                    {
                        "lat": 37.7891838,
                        "lon": -122.4033522,
                        "primary": "",
                        "globe": "earth"
                    }
                ]
            }
        }
    }
}
*
* */


data class WikiCoordinatesDto (
    @SerializedName("lat") val lat: Double,
    @SerializedName("lon") val lon: Double,
    @SerializedName("primary") val primary: String?=null,
    @SerializedName("globe") val globe: String?=null
)