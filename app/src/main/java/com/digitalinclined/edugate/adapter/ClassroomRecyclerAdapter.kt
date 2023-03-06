package com.digitalinclined.edugate.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.digitalinclined.edugate.databinding.ClassroomItemLayoutBinding
import com.digitalinclined.edugate.databinding.ClassroomRecyclerLayoutBinding
import com.digitalinclined.edugate.models.ClassroomDetailsClass
import com.digitalinclined.edugate.utils.DateTimeFormatFetcher
import com.google.android.material.snackbar.Snackbar

class ClassroomRecyclerAdapter: RecyclerView.Adapter<ClassroomRecyclerAdapter.ClassroomViewHolder>() {

    // Diff Util Call Back
    private val differCallback = object : DiffUtil.ItemCallback<ClassroomDetailsClass>() {
        override fun areItemsTheSame(
            oldItem: ClassroomDetailsClass,
            newItem: ClassroomDetailsClass
        ): Boolean {
            return oldItem.classroomName == newItem.classroomName
        }

        override fun areContentsTheSame(
            oldItem: ClassroomDetailsClass,
            newItem: ClassroomDetailsClass
        ): Boolean {
            return oldItem == newItem
        }
    }

    // Differ Value Setup
    val differ = AsyncListDiffer(this,differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClassroomViewHolder {
        val binding = ClassroomRecyclerLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ClassroomViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ClassroomViewHolder, position: Int) {

        // data from dataclasses
        val data = differ.currentList[position]

        holder.binding.apply {

            // Image View
            // code here to load image...

            // classroom name
            classroomNameTv.text = data.classroomName

            // due date
            classroomLastUpdateTv.text = "Last updated on - ${DateTimeFormatFetcher().getDateTime(data.classDueDate!!.toLong())}"

            // classroom color
            classroomLayout.setBackgroundColor(Color.parseColor(data.classColor))

            // click listener
            openClassroom.setOnClickListener {
                onClassroomItemClickListener?.let { it(data) }
            }
        }

    }

    override fun getItemCount() = differ.currentList.size

    // Inner Class ViewHolder
    inner class ClassroomViewHolder(val binding: ClassroomRecyclerLayoutBinding) : RecyclerView.ViewHolder(binding.root)

    // On click listener
    private var onClassroomItemClickListener: ((ClassroomDetailsClass) -> Unit)? = null

    fun setClassroomOnItemClickListener(listener: (ClassroomDetailsClass) -> Unit) {
        onClassroomItemClickListener = listener
    }
}