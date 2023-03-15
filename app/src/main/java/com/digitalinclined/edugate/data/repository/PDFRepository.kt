package com.digitalinclined.edugate.data.repository

import androidx.lifecycle.LiveData
import com.digitalinclined.edugate.data.PDFDataDao
import com.digitalinclined.edugate.data.model.PDFDataRoom

class PDFRepository(
    private val pdfDataDao: PDFDataDao
) {

    // get data
    fun getSelectedPDF(id: String): LiveData<PDFDataRoom> = pdfDataDao.getSelectedPDF(id)

    // insert data
    suspend fun insertData(pdfData: PDFDataRoom) {
        pdfDataDao.insertData(pdfData)
    }
}