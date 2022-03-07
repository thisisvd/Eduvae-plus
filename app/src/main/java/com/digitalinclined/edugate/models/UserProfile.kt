package com.digitalinclined.edugate.models

data class UserProfile(
    val name: String? = null,
    val email: String? = null,
    val phone: String? = null,
    val course: String? = null,
    val year: String? = null,
    val city: String? = null,
    val semester: String? = null,
    val profilephotolink: String? = null,
    val following: ArrayList<String>? = null
)
