package com.digitalinclined.edugate.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.digitalinclined.edugate.R
import com.digitalinclined.edugate.databinding.ClassroomItemLayoutBinding
import com.digitalinclined.edugate.databinding.ClassroomRecyclerLayoutBinding
import com.digitalinclined.edugate.models.ClassroomDetailsClass
import com.digitalinclined.edugate.utils.DateTimeFormatFetcher
import com.google.android.material.snackbar.Snackbar

class ClassroomRecyclerAdapter: RecyclerView.Adapter<ClassroomRecyclerAdapter.ClassroomViewHolder>() {

    inner class ColorComboRecyclerClass(val backColor: String, val iconColor: String)

    val mapOfColors = mapOf(
        0 to ColorComboRecyclerClass("#FEF8E2","#FFDC5C"),
        1 to ColorComboRecyclerClass("#E7FAE9","#27FF3D"),
        2 to ColorComboRecyclerClass("#EEF9FF","#49BEFD"),
        3 to ColorComboRecyclerClass("#FDEBF9","#E529BC")
    )

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

            val colorPosition = position % 4

            // Image View
            // code here to load image...
            classroomLayout.setBackgroundColor(Color.parseColor(mapOfColors[colorPosition]!!.backColor))
            classroomIconImg.apply {
                setImageResource(data.imageInt!!.toInt())
                DrawableCompat.setTint(
                    DrawableCompat.wrap(this.drawable),
                    Color.parseColor(mapOfColors[colorPosition]!!.iconColor)
                )
            }

            // classroom name
            classroomNameTv.text = data.classroomName

            // due date
            classroomLastUpdateTv.text = "Last updated on - ${DateTimeFormatFetcher().getDateTime(data.classDueDate!!.toLong())}"

            // click listener
            openClassroom.setOnClickListener {
                onClassroomItemClickListener?.let { it(data, mapOfColors[colorPosition]!!.backColor, mapOfColors[colorPosition]!!.iconColor)}
            }
        }

    }

    override fun getItemCount() = differ.currentList.size

    // Inner Class ViewHolder
    inner class ClassroomViewHolder(val binding: ClassroomRecyclerLayoutBinding) : RecyclerView.ViewHolder(binding.root)

    // On click listener
    private var onClassroomItemClickListener: ((ClassroomDetailsClass, String, String) -> Unit)? = null

    fun setClassroomOnItemClickListener(listener: (ClassroomDetailsClass, String, String) -> Unit) {
        onClassroomItemClickListener = listener
    }
}