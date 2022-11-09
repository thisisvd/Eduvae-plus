package com.digitalinclined.edugate.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.digitalinclined.edugate.databinding.PreviousYearItemLayoutBinding
import com.digitalinclined.edugate.models.QuestionsNotesDataClass
import com.digitalinclined.edugate.utils.DateTimeFormatFetcher
import com.google.android.material.snackbar.Snackbar

class PreviousYearsPaperAdapter(val itemView: String): RecyclerView.Adapter<PreviousYearsPaperAdapter.PreviousYearsPaperViewHolder>(){

    // Diff Util Call Back
    private val differCallback = object : DiffUtil.ItemCallback<QuestionsNotesDataClass>() {
        override fun areItemsTheSame(
            oldItem: QuestionsNotesDataClass,
            newItem: QuestionsNotesDataClass
        ): Boolean {
            return oldItem.timestamp == newItem.timestamp
        }

        override fun areContentsTheSame(
            oldItem: QuestionsNotesDataClass,
            newItem: QuestionsNotesDataClass
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

                // set year date of paper
                monthYearTV.text = DateTimeFormatFetcher().getDateTime(timestamp!!.toLong())

                // set title of paper
                paperTitleTV.text = paperName

                // change & disable text for itemView
                if(itemView == "notes") {
                    syllabusTV.visibility = View.GONE
                    viewPaper.text = "View Notes"
                }

                // set on Click listeners
                // set paper link
                viewPaper.setOnClickListener {
                    onItemClickListener?.let { it(pdfFileId!!, "viewPaper") }
                }

                // set syllabus
                syllabusTV.setOnClickListener {
                    Snackbar.make(it,"No syllabus found!",Snackbar.LENGTH_SHORT).show()
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