package com.hfad.egypttour.data.api.model

import com.google.gson.annotations.SerializedName
data class WikiImageDto (
    @SerializedName("source") val source: String,
    @SerializedName("width") val width: Int,
    @SerializedName("height") val height: Int
)