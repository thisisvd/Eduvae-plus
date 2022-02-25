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
import com.digitalinclined.edugate.restapi.APIClient
import com.digitalinclined.edugate.restapi.APIInterface
import com.digitalinclined.edugate.restapi.models.banner.BannerResponse
import com.digitalinclined.edugate.ui.viewmodel.repository.Repository
import com.digitalinclined.edugate.utils.Resource
import kotlinx.coroutines.launch
import retrofit2.Response
import java.lang.Exception

class MainViewModel(
    application: Application
): AndroidViewModel(application) {

    // TAG
    private val TAG = "EmployeeViewModel"

    // main repository
    private val repository: Repository
    
    // banner details 
    var getBannerDetail: MutableLiveData<Resource<BannerResponse>> = MutableLiveData()

    init {
        repository = Repository(APIClient.api)
    }

    /** MAIN API CALLS */

    // get banners from api
    fun getBanner() = viewModelScope.launch {
        getBannerDetail.value = Resource.Loading()

        if(hasInternetConnection()) {
            // Actually processing Data
            try {
                val response = repository.getBanners()
                getBannerDetail.value = handleBannerResponse(response)
            } catch (e: Exception) {
                getBannerDetail.value = Resource.Error("404 Not Found!")
            }
        } else {
            getBannerDetail.value = Resource.Error("No Internet Connection!")
        }
    }

    // handling error responses
    private fun handleBannerResponse(response: Response<BannerResponse>): Resource<BannerResponse>? {
        return when {
            response.message().toString().contains("timeout") -> {
                Resource.Error("Timeout")
            }
            response.isSuccessful -> {
                val banner = response.body()
                Resource.Success(banner!!)
            }
            else -> {
                Resource.Error(response.message())
            }
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