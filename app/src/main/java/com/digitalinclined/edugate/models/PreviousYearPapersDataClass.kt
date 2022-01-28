package com.digitalinclined.edugate.models

data class PreviousYearPapersDataClass(
    val isFree: Boolean? = true,
    val date: String? = null,
    val paperTitle: String? = null,
    val noOfQuestions: Int? = null,
    val paperTime: Int? = null,
    val totalMarks: Int? = null,
    val viewPaperLink: String? = null,
    val syllabus: String? = null,
    val languageMedium: String? = null
)
