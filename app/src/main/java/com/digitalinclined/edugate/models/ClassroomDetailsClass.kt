package com.digitalinclined.edugate.models

import java.io.Serializable

data class ClassroomDetailsClass(
    val classDueDate: String? = null,
    val classroomName: String? = null,
    val imageInt: String? = null,
    val classroomID: String? = null,
    val classworkStudentList: ArrayList<String>? = null
) : Serializable
