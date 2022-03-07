package com.digitalinclined.edugate.ui.viewmodel.repository

import com.digitalinclined.edugate.restapi.APIInterface
import com.digitalinclined.edugate.ui.viewmodel.MainViewModel

class Repository(
    private val api: APIInterface
) {

    /** Direct API call's */

    // get banners from api
    suspend fun getBanners() = api.getBanner()

    // fetch notes from api
    suspend fun getNotes(course: String, semester: Int) = api.getNotes(course, semester)

    // add Notes to the api
    suspend fun addNotes(course: String, semester: String, filename: String, pdfFile: MainViewModel.PDFFileDataClass) =
        api.addNotes(course,semester,filename, pdfFile)

}