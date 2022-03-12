package com.digitalinclined.edugate.restapi

import com.digitalinclined.edugate.models.NotesMessage
import com.digitalinclined.edugate.restapi.models.banner.BannerResponse
import com.digitalinclined.edugate.restapi.models.branches.BranchesModel
import com.digitalinclined.edugate.restapi.models.notes.NotesResponse
import com.digitalinclined.edugate.ui.viewmodel.MainViewModel
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
    @POST("addnotesmbl")
    suspend fun addNotes(
        @Query("course") course: String,
        @Query("sem") semester: String,
        @Query("filename") filename: String,
        @Body pdfFile: MainViewModel.PDFFileDataClass
    ): Response<NotesMessage>

    // get branches
    @GET("getbranch")
    suspend fun getBranches() : Response<BranchesModel>

}