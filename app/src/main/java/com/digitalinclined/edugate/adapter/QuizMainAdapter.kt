package com.digitalinclined.edugate.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.digitalinclined.edugate.databinding.QuizItemLayoutBinding
import com.digitalinclined.edugate.models.quizzesmodel.Quizze

class QuizMainAdapter : RecyclerView.Adapter<QuizMainAdapter.QuizMainViewHolder>() {

    // Diff Util Call Back
    private val differCallback = object : DiffUtil.ItemCallback<Quizze>() {
        override fun areItemsTheSame(
            oldItem: Quizze,
            newItem: Quizze
        ): Boolean {
            return oldItem.quizName == newItem.quizName
        }

        override fun areContentsTheSame(
            oldItem: Quizze,
            newItem: Quizze
        ): Boolean {
            return oldItem == newItem
        }
    }

    // Differ Value Setup
    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): QuizMainViewHolder {
        val binding =
            QuizItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return QuizMainViewHolder(binding)
    }

    override fun onBindViewHolder(holder: QuizMainViewHolder, position: Int) {

        // data from the dataclasses
        val data = differ.currentList[position]

        holder.binding.apply {

            // quiz name
            quizName.text = data.quizName

            // quiz numbers
            questionsNumbers.text = "${data.quizQuestions.size} Questions"

            // on click listeners
            startQuiz.setOnClickListener {
                onItemClickListener?.let { it(data) }
            }
        }
    }

    override fun getItemCount() = differ.currentList.size

    // Inner Class ViewHolder
    inner class QuizMainViewHolder(val binding: QuizItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root)

    // On click listener
    private var onItemClickListener: ((quizQuestions: Quizze) -> Unit)? = null

    fun setOnItemClickListener(listener: (quizQuestions: Quizze) -> Unit) {
        onItemClickListener = listener
    }
}