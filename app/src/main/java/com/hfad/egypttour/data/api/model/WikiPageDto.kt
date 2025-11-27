package com.hfad.egypttour.data.api.model

import com.google.gson.annotations.SerializedName

data class WikiPageDto (
    @SerializedName("pageid") val pageId: Int?,
    @SerializedName("title") val title: String?,
    @SerializedName("coordinates") val coordinates: List<WikiCoordinatesDto>?=null,
    @SerializedName("thumbnail") val thumbnail: WikiImageDto?=null,
    @SerializedName("extract") val extract: String?=null

)