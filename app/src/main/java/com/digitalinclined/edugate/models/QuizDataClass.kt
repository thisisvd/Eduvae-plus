package com.digitalinclined.edugate.models

data class QuizDataClass(
    val correctAnswer: Int? = null,
    val questionUID: String? = null,
    val quizName: String? = null,
    val options: List<String>? = null
)
