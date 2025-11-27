package com.hfad.egypttour.data.api.model

import com.google.gson.annotations.SerializedName

data class WikiResponse (
    @SerializedName("query") val query: WikiQuery?
)