package com.digitalinclined.edugate.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.digitalinclined.edugate.databinding.BranchSelectionItemBinding
import com.digitalinclined.edugate.models.BranchListDataClass

class BranchesListAdapter : RecyclerView.Adapter<BranchesListAdapter.QuizMainViewHolder>() {

    // Diff Util Call Back
    private val differCallback = object : DiffUtil.ItemCallback<BranchListDataClass>() {
        override fun areItemsTheSame(
            oldItem: BranchListDataClass,
            newItem: BranchListDataClass
        ): Boolean {
            return oldItem.branchCode == newItem.branchCode
        }

        override fun areContentsTheSame(
            oldItem: BranchListDataClass,
            newItem: BranchListDataClass
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
            BranchSelectionItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return QuizMainViewHolder(binding)
    }

    override fun onBindViewHolder(holder: QuizMainViewHolder, position: Int) {

        // data from the dataclasses
        val data = differ.currentList[position]

        holder.binding.apply {

            // quiz name
            branchId.text = data.branchCode

            // quiz numbers
            branchName.text = data.branchName

            // on click listeners
            branchRootLayout.setOnClickListener {
                onItemClickListener?.let { it(data) }
            }
        }
    }

    override fun getItemCount() = differ.currentList.size

    // Inner Class ViewHolder
    inner class QuizMainViewHolder(val binding: BranchSelectionItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    // On click listener
    private var onItemClickListener: ((quizQuestions: BranchListDataClass) -> Unit)? = null

    fun setOnItemClickListener(listener: (quizQuestions: BranchListDataClass) -> Unit) {
        onItemClickListener = listener
    }
}