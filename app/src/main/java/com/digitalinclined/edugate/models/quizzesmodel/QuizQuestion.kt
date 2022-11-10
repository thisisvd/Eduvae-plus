package com.digitalinclined.edugate.models.quizzesmodel

data class QuizQuestion(
    val answer: Int,
    val options: List<String>,
    val question: String
)