package com.digitalinclined.edugate.ui.viewmodel.repository

import com.digitalinclined.edugate.restapi.APIInterface

class Repository(
    private val api: APIInterface
) {

    /** Direct API call's */
    suspend fun getBanners() = api.getBanner()

}