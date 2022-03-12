package com.digitalinclined.edugate.restapi.models.branches

import com.google.gson.annotations.SerializedName

data class BranchesModel(
    @SerializedName("branch")
    val branch: List<Branch>,
    @SerializedName("status")
    val status: Int
)