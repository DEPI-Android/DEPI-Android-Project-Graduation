package com.hfad.egypttour.data.api.model
import com.google.gson.annotations.SerializedName
data class WikiQuery (
    @SerializedName("pages") val pages: Map<String, WikiPageDto>?
)