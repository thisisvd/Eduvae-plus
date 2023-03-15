package com.digitalinclined.edugate.restapi

import com.digitalinclined.edugate.constants.Constants.BASE_YOUTUBE_API_KEY
import com.digitalinclined.edugate.models.youtubemodel.YoutubeResponse
import retrofit2.Response
import retrofit2.http.*

interface APIInterface {

    // notes
    @GET("search")
    suspend fun getYoutubeSearchQuery(
        @Query("q")
        q: String,
        @Query("regionCode")
        regionCode: String,
        @Query("type")
        type: String = "video",
        @Query("key")
        key: String = BASE_YOUTUBE_API_KEY,
        @Query("maxResults")
        maxResults: Int = 30,
        @Query("part")
        part: String = "snippet"
    ) : Response<YoutubeResponse>

}