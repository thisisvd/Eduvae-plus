package com.digitalinclined.edugate.models

import java.io.Serializable

data class BranchListDataClass(
    val branchName: String? = null,
    val branchCode: String? = null,
    val majorSubjects: ArrayList<String>? = null
): Serializable
