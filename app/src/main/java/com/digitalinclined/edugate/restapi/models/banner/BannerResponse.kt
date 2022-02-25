package com.digitalinclined.edugate.restapi.models.banner

import com.google.gson.annotations.SerializedName

data class BannerResponse(
    @SerializedName("banners")
    val banners: List<Banner>,
    @SerializedName("status")
    val status: Int
)