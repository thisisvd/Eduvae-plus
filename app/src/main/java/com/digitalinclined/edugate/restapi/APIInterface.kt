package com.digitalinclined.edugate.restapi

import com.digitalinclined.edugate.restapi.models.banner.BannerResponse
import retrofit2.Response
import retrofit2.http.GET

interface APIInterface {

    // banner
    @GET("getbanner")
    suspend fun getBanner() :
            Response<BannerResponse>

}