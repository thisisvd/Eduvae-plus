package com.digitalinclined.edugate.restapi.models.banner

import com.google.gson.annotations.SerializedName

data class Banner(
    @SerializedName("banner")
    val banner: String,
    @SerializedName("banner_id")
    val bannerId: String
)