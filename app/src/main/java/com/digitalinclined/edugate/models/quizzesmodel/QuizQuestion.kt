package com.digitalinclined.edugate.models.quizzesmodel

data class QuizQuestion(
    val answer: Int? = null,
    val options: List<String>? = null,
    val question: String? = null
)