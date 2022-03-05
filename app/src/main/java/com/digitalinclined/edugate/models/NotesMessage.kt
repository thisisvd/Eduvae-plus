package com.digitalinclined.edugate.models

import com.google.gson.annotations.SerializedName

data class NotesMessage(
    @SerializedName("message")
    var message: String? = null
)
