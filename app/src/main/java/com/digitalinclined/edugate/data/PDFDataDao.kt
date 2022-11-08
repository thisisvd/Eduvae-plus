package com.digitalinclined.edugate.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.digitalinclined.edugate.data.model.PDFDataRoom

@Dao
interface PDFDataDao {

    @Query("SELECT * FROM PDF_TABLE_NAME WHERE uniqueID=:keyId")
    fun getSelectedPDF(keyId: String) : LiveData<PDFDataRoom>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertData(pdfDataRoom: PDFDataRoom)

}