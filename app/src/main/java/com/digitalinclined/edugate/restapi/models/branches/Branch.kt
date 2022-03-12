package com.digitalinclined.edugate.restapi.models.branches

import com.google.gson.annotations.SerializedName

data class Branch(
    @SerializedName("branch")
    val branch: String,
    @SerializedName("yors")
    val yors: String
)