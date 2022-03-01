package com.digitalinclined.edugate.restapi.models.notes

import com.google.gson.annotations.SerializedName

data class NotesResponse(
    @SerializedName("notes")
    val notes: List<Note>,
    @SerializedName("status")
    val status: Int
)