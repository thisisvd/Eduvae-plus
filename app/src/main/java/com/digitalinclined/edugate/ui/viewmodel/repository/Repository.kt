package com.digitalinclined.edugate.ui.viewmodel.repository

import com.digitalinclined.edugate.restapi.APIInterface

class Repository(
    private val api: APIInterface
) {

    // get search result for query
    suspend fun getYoutubeSearchResults(
        query: String,
        regionCode: String,
    ) = api.getYoutubeSearchQuery(query, regionCode)

}