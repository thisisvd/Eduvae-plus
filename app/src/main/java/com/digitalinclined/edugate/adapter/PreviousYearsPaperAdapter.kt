package com.digitalinclined.edugate.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.digitalinclined.edugate.databinding.PreviousYearItemLayoutBinding
import com.digitalinclined.edugate.models.PreviousYearPapersDataClass

class PreviousYearsPaperAdapter: RecyclerView.Adapter<PreviousYearsPaperAdapter.PreviousYearsPaperViewHolder>(){

    // Diff Util Call Back
    private val differCallback = object : DiffUtil.ItemCallback<PreviousYearPapersDataClass>() {
        override fun areItemsTheSame(
            oldItem: PreviousYearPapersDataClass,
            newItem: PreviousYearPapersDataClass
        ): Boolean {
            return oldItem.paperTitle == newItem.paperTitle
        }

        override fun areContentsTheSame(
            oldItem: PreviousYearPapersDataClass,
            newItem: PreviousYearPapersDataClass
        ): Boolean {
            return oldItem == newItem
        }
    }

    // Differ Value Setup
    val differ = AsyncListDiffer(this,differCallback)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PreviousYearsPaperViewHolder {
        val binding = PreviousYearItemLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return PreviousYearsPaperViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PreviousYearsPaperViewHolder, position: Int) {

        // data from the dataclasses
        val data = differ.currentList[position]

        holder.binding.apply {
            data.apply {

                // set is paper free or not
                if (isFree!!){
                    isPaperFree.text = "FREE"
                } else {
                    isPaperFree.text = "PREMIUM"
                }

                // set year date of paper
                monthYearTV.text = date

                // set title of paper
                paperTitleTV.text = paperTitle

                // questionTimeMarkTV
                questionTimeMarkTV.text = "${noOfQuestions}Qs. $paperTime min, $totalMarks marks"

                // set paper language medium
                languageMediumTV.text = languageMedium

                // set on Click listeners
                // set paper link
                viewPaper.setOnClickListener {
                    onItemClickListener?.let { it(data.viewPaperLink!!, "viewPaper") }
                }

                // set syllabus
                syllabusTV.setOnClickListener {
                    onItemClickListener?.let { it(data.syllabus!!, "syllabusTV") }
                }

            }
        }
    }

    override fun getItemCount() = differ.currentList.size

    // Inner Class ViewHolder
    inner class PreviousYearsPaperViewHolder(val binding: PreviousYearItemLayoutBinding): RecyclerView.ViewHolder(binding.root)

    // On click listener
    private var onItemClickListener: ((str: String, view: String) -> Unit)? = null

    fun setOnItemClickListener(listener: (str: String, view: String) -> Unit) {
        onItemClickListener = listener
    }

}