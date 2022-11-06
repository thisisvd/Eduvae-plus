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
import com.digitalinclined.edugate.models.NotesMessage
import com.digitalinclined.edugate.restapi.APIClient
import com.digitalinclined.edugate.restapi.models.banner.BannerResponse
import com.digitalinclined.edugate.restapi.models.branches.BranchesModel
import com.digitalinclined.edugate.restapi.models.notes.NotesResponse
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
    var getBranchesFromAPI: MutableLiveData<Resource<BranchesModel>> = MutableLiveData()

    // notes details
    var getNotesDetail: MutableLiveData<Resource<NotesResponse>> = MutableLiveData()

    // add notes
    var addNotesDetail: MutableLiveData<Resource<NotesMessage>> = MutableLiveData()

    init {
        repository = Repository(APIClient.api)
    }

    /** MAIN API CALLS */
    // get branches from api
    fun getBranches() = viewModelScope.launch {
        getBranchesFromAPI.postValue(Resource.Loading())

        if(hasInternetConnection()) {
            // Actually processing Data
            try {
                val response = repository.getBranches()
                getBranchesFromAPI.value = handleGetBranches(response)
            } catch (e: Exception) {
                getBranchesFromAPI.value = Resource.Error("404 Not Found!")
            }
        } else {
            getBranchesFromAPI.value = Resource.Error("No Internet Connection!")
        }
    }

    // handling error responses
    private fun handleGetBranches(response: Response<BranchesModel>): Resource<BranchesModel>? {
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

    // get banners from api
    fun getNotes(course: String, semester: Int) = viewModelScope.launch {
        getNotesDetail.value = Resource.Loading()

        if(hasInternetConnection()) {
            // Actually processing Data
            try {
                val response = repository.getNotes(course,semester)
                getNotesDetail.value = handleNotesResponse(response)
            } catch (e: Exception) {
                getNotesDetail.value = Resource.Error("404 Not Found!")
            }
        } else {
            getNotesDetail.value = Resource.Error("No Internet Connection!")
        }
    }

    // handling error responses
    private fun handleNotesResponse(response: Response<NotesResponse>): Resource<NotesResponse>? {
        return when {
            response.message().toString().contains("timeout") -> {
                Resource.Error("Timeout")
            }
            response.isSuccessful -> {
                val notes = response.body()
                Resource.Success(notes!!)
            }
            else -> {
                Resource.Error(response.message())
            }
        }
    }

    // add notes to api (server)
    fun addNotes(course: String, semester: String, filename: String, pdfFile: String) = viewModelScope.launch {
        addNotesSafeCall(course,semester, filename, PDFFileDataClass(pdfFile))
    }

    // add notes from api safe call
    private suspend fun addNotesSafeCall(course: String, semester: String, filename: String, pdfFile: MainViewModel.PDFFileDataClass) = viewModelScope.launch {
        addNotesDetail.value = Resource.Loading()

        if(hasInternetConnection()) {
            // Actually processing Data
            try {
                val response = repository.addNotes(course,semester,filename, pdfFile)
                addNotesDetail.value = handleAddNotesResponse(response)
            } catch (e: Exception) {
                addNotesDetail.value = Resource.Error("404 Not Found!")
            }
        } else {
            addNotesDetail.value = Resource.Error("No Internet Connection!")
        }
    }

    // handling error responses
    private fun handleAddNotesResponse(response: Response<NotesMessage>): Resource<NotesMessage>? {
        return when {
            response.message().toString().contains("timeout") -> {
                Resource.Error("Timeout")
            }
            response.isSuccessful -> {
                val notes = response.body()
                Resource.Success(notes!!)
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

    /** INNER CLASS FOR APIS */
    inner class PDFFileDataClass(var file: String)

}