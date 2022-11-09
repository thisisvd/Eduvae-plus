package com.digitalinclined.edugate.models

data class QuizDataClass(
    val answer: Int? = null,
    val question: String? = null,
    val options: List<String>? = null
)
