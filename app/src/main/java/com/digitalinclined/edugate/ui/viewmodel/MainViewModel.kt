package com.digitalinclined.edugate.ui.viewmodel

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.digitalinclined.edugate.models.youtubemodel.Item
import com.digitalinclined.edugate.restapi.APIClient
import com.digitalinclined.edugate.ui.viewmodel.repository.Repository
import com.digitalinclined.edugate.utils.Resource
import kotlinx.coroutines.launch

class MainViewModel(
    application: Application
) : AndroidViewModel(application) {

    // main repository
    private val repository: Repository = Repository(APIClient.api)

    // banner details
    var getYoutubeSearchResult: MutableLiveData<Resource<List<Item>>> = MutableLiveData()

    /** MAIN API CALLS */
    // add notes from api safe call
    fun getYoutubeResult(query: String, regionCode: String) = viewModelScope.launch {
        getYoutubeSearchResult.postValue(Resource.Loading())

        if (hasInternetConnection()) {
            try {
                // sending request
                val response = repository.getYoutubeSearchResults(query, regionCode)

                if (response.isSuccessful) {
                    response.body().let { response ->
                        if (response != null && response.items.isNotEmpty()) {
                            getYoutubeSearchResult.postValue(Resource.Success(response.items))
                        }
                    }

                } else {
                    getYoutubeSearchResult.postValue(Resource.Error(response.message().toString()))
                }

            } catch (e: Exception) {
                getYoutubeSearchResult.postValue(Resource.Error(e.message.toString()))
                e.printStackTrace()
            }
        } else {
            getYoutubeSearchResult.postValue(Resource.Error("No Internet Connection!"))
        }
    }

    // check for active internet connection
    @RequiresApi(Build.VERSION_CODES.M)
    private fun hasInternetConnection(): Boolean {
        val connectivityManager = getApplication<Application>().getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager

        val activeNetwork = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
        return when {
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
    }

}