package com.digitalinclined.edugate.models

data class ShareNotesToClass(
    val name: String? = null,
    val userID: String? = null,
    val courseName: String? = null,
    val year: String? = null,
    val timestamp: Long? = null,
    val pdfTitle: String? = null,
    val pdfDescription: String? = null,
    val pdfStringFile: String? = null,
    val likesCount: Int = 0,
    val commentCount: Int = 0
)