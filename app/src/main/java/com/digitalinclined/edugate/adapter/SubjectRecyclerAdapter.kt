package com.digitalinclined.edugate.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.digitalinclined.edugate.databinding.SubjectComponentLayoutBinding
import com.digitalinclined.edugate.models.SubjectRecyclerData

class SubjectRecyclerAdapter: RecyclerView.Adapter<SubjectRecyclerAdapter.SyllabusViewHolder>() {

    // Diff Util Call Back
    private val differCallback = object : DiffUtil.ItemCallback<SubjectRecyclerData>() {
        override fun areItemsTheSame(
            oldItem: SubjectRecyclerData,
            newItem: SubjectRecyclerData
        ): Boolean {
            return oldItem.textMain == newItem.textMain
        }

        override fun areContentsTheSame(
            oldItem: SubjectRecyclerData,
            newItem: SubjectRecyclerData
        ): Boolean {
            return oldItem == newItem
        }
    }

    // Differ Value Setup
    val differ = AsyncListDiffer(this,differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SyllabusViewHolder {
        val binding = SubjectComponentLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return SyllabusViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SyllabusViewHolder, position: Int) {

        val data = differ.currentList[position]

        holder.binding.apply {

            // mark text
            textMark.text = data.textMark

            // main text
            textValueMain.text = data.textMain

            root.setOnClickListener {
                onItemClickListener?.let { it(data) }
            }
        }

    }

    override fun getItemCount() = differ.currentList.size

    // Inner Class ViewHolder
    inner class SyllabusViewHolder(val binding: SubjectComponentLayoutBinding) : RecyclerView.ViewHolder(binding.root)

    // On click listener
    private var onItemClickListener: ((SubjectRecyclerData) -> Unit)? = null

    fun setOnItemClickListener(listener: (SubjectRecyclerData) -> Unit) {
        onItemClickListener = listener
    }

}