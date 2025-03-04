package com.digitalinclined.edugate.models

data class DiscussionDataClass(
    val userID: String? = "",
    val comment: Int? = 0,
    val content: String? = "",
    val course: String? = "",
    val courseYear: String? = "",
    val likes: Int? = 0,
    val name: String? = "",
    val pdfFile: String? = "",
    val pdfName: String? = "",
    val publishedDate: String? = "",
    val title: String? = "",
    val userImage: String? = "",
)
