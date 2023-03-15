package com.digitalinclined.edugate.data.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.digitalinclined.edugate.data.PDFDatabase
import com.digitalinclined.edugate.data.model.PDFDataRoom
import com.digitalinclined.edugate.data.repository.PDFRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LocalViewModel(application: Application) : AndroidViewModel(application) {

    private val toDoDao = PDFDatabase.getDatabase(application).pdfDataDao()
    private val repository: PDFRepository = PDFRepository(toDoDao)

    // insert data
    fun insertData(pdfDataRoom: PDFDataRoom) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertData(pdfDataRoom)
        }
    }

    // get pdf
    fun getSelectedPdf(id: String): LiveData<PDFDataRoom> = repository.getSelectedPDF(id)

}