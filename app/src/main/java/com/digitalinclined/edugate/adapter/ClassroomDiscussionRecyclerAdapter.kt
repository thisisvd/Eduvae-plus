package com.digitalinclined.edugate.adapter

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
import com.digitalinclined.edugate.models.ClassroomObjectsDataClass
import com.digitalinclined.edugate.utils.DateTimeFormatFetcher
import com.google.android.material.snackbar.Snackbar

class ClassroomDiscussionRecyclerAdapter: RecyclerView.Adapter<ClassroomDiscussionRecyclerAdapter.ClassroomViewHolder>() {

    // Diff Util Call Back
    private val differCallback = object : DiffUtil.ItemCallback<ClassroomObjectsDataClass>() {
        override fun areItemsTheSame(
            oldItem: ClassroomObjectsDataClass,
            newItem: ClassroomObjectsDataClass
        ): Boolean {
            return oldItem.timestamp == newItem.timestamp
        }

        override fun areContentsTheSame(
            oldItem: ClassroomObjectsDataClass,
            newItem: ClassroomObjectsDataClass
        ): Boolean {
            return oldItem == newItem
        }
    }

    // Differ Value Setup
    val differ = AsyncListDiffer(this,differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClassroomViewHolder {
        val binding = ClassroomItemLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ClassroomViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ClassroomViewHolder, position: Int) {

        // data from dataclasses
        val data = differ.currentList[position]

        holder.binding.apply {

            // Image View
            val requestOptions = RequestOptions()
            requestOptions.diskCacheStrategy(DiskCacheStrategy.ALL)
            requestOptions.centerCrop()
            if(!data.userImage.isNullOrEmpty()) {
                Glide.with(root)
                    .load(data.userImage)
                    .apply(requestOptions)
                    .into(classroomUserImageView)
            }

            // name
            classroomUsernameTv.text = data.userName

            // timestamp
            postTime.text = DateTimeFormatFetcher().getDateWithIncludedTime(data.timestamp!!.toLong())

            // pdf
            if(!data.pdfNameStored.isNullOrEmpty()) {
                classroomPdfView.visibility = View.VISIBLE
                pdfName.text = data.pdfNameStored
                classroomPdfView.setOnClickListener {
                    onClassroomItemClickListener?.let {
                        it(data.pdfNameStored!!)
                    }
                }
            }

            // image
            if(!data.imageLink.isNullOrEmpty()) {
                classroomImageView.visibility = View.VISIBLE
                Glide.with(root)
                    .load(data.imageLink)
                    .apply(requestOptions)
                    .into(classroomImage)
            }

            // discussion
            classroomDiscussionTv.text = data.description

            // click listener
            likeIcon1.setOnClickListener {
                Snackbar.make(it,"Post appreciated",Snackbar.LENGTH_SHORT).show()
            }

        }

    }

    override fun getItemCount() = differ.currentList.size

    // Inner Class ViewHolder
    inner class ClassroomViewHolder(val binding: ClassroomItemLayoutBinding) : RecyclerView.ViewHolder(binding.root)

    // On click listener
    private var onClassroomItemClickListener: ((String) -> Unit)? = null

    fun setClassroomOnItemClickListener(listener: (String) -> Unit) {
        onClassroomItemClickListener = listener
    }
}