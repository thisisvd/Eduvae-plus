package com.digitalinclined.edugate.restapi

import com.digitalinclined.edugate.restapi.models.banner.BannerResponse
import com.digitalinclined.edugate.restapi.models.notes.NotesResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface APIInterface {

    // banner
    @GET("getbanner")
    suspend fun getBanner() :
            Response<BannerResponse>

    // notes
    @GET("getnotes")
    suspend fun getNotes(
        @Query("course")
        course: String,
        @Query("sem")
        semister: Int,
    ) :
            Response<NotesResponse>

}