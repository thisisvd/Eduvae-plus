package com.digitalinclined.edugate.restapi

import com.digitalinclined.edugate.models.NotesMessage
import com.digitalinclined.edugate.restapi.models.banner.BannerResponse
import com.digitalinclined.edugate.restapi.models.notes.NotesResponse
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

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
    ) : Response<NotesResponse>

    // add notes to server
    @FormUrlEncoded
    @POST("addnotes")
    suspend fun addNotes(
        @Field("file") encodedPDF: ByteArray,
        @Field("course") course: String,
        @Field("sem") semester: String,
        @Field("filename") filename: String,
    ): Call<NotesMessage>

}