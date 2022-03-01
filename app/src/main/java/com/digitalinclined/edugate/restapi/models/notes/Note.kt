package com.digitalinclined.edugate.restapi.models.notes

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Note(
    @SerializedName("notesName")
    val notesName: String,
    @SerializedName("notesPDF")
    val notesPDF: String
): Serializable