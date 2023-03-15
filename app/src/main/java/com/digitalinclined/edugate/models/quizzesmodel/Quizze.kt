package com.digitalinclined.edugate.models.quizzesmodel

import java.io.Serializable

data class Quizze(
    val quizName: String,
    val quizQuestions: List<QuizQuestion>,
    val quizTotalNumbers: Int
) : Serializable