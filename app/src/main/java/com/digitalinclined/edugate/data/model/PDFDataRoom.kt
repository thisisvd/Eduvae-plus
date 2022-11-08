package com.digitalinclined.edugate.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.digitalinclined.edugate.constants.Constants.PDF_TABLE_NAME

@Entity(tableName = PDF_TABLE_NAME)
data class PDFDataRoom(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    var uniqueID: String,
    var title: String,
    var fileData: String
)
