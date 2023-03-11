package com.digitalinclined.edugate.models.quizzesmodel

data class QuizSubmissionDataClass(
    val isTaken: Boolean = false,
    val userMarks: Int? = null,
    val totalMarks: Int? = null
)
