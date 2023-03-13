package com.digitalinclined.edugate.adapter

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.drawable.DrawableCompat
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.digitalinclined.edugate.constants.Constants.mapOfColors
import com.digitalinclined.edugate.databinding.ClassroomRecyclerLayoutBinding
import com.digitalinclined.edugate.models.ClassroomDetailsClass
import com.digitalinclined.edugate.utils.DateTimeFormatFetcher
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

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

            val colorPosition = position % 4

            // Image View
            // code here to load image...
            classroomLayout.setBackgroundColor(Color.parseColor(mapOfColors[colorPosition]!!.backColor))

            classroomIconImg.apply {
                val imageView = this
                val requestOptions = RequestOptions()
                requestOptions.diskCacheStrategy(DiskCacheStrategy.ALL)
                requestOptions.centerCrop()
                if(!data.imageInt!!.isNullOrEmpty()) {
                    Glide.with(root)
                        .load(data.imageInt!!)
                        .apply(requestOptions)
                        .listener(object : RequestListener<Drawable?> {
                            override fun onLoadFailed(
                                e: GlideException?,
                                model: Any?,
                                target: com.bumptech.glide.request.target.Target<Drawable?>?,
                                isFirstResource: Boolean
                            ): Boolean {
                                // log exception
                                Log.d("Glide", "Error loading image")
                                return false // important to return false so the error placeholder can be placed
                            }

                            override fun onResourceReady(
                                resource: Drawable?,
                                model: Any?,
                                target: com.bumptech.glide.request.target.Target<Drawable?>?,
                                dataSource: DataSource?,
                                isFirstResource: Boolean
                            ): Boolean {
                                Log.d("Glide", "Loaded image")
                                DrawableCompat.setTint(
                                    DrawableCompat.wrap(resource!!),
                                    Color.parseColor(mapOfColors[colorPosition]!!.iconColor)
                                )
                                return false
                            }
                        })
                        .into(this)
                }
            }

            // classroom name
            classroomNameTv.text = data.classroomName

            // due date
            classroomLastUpdateTv.text = "Last updated on - ${DateTimeFormatFetcher().getDateTime(data.classDueDate!!.toLong())}"

            // pending work tv
            if (data.hasClassWork) {
                if (data.classworkStudentList != null) {
                    if (Firebase.auth.currentUser != null) {
                        if (!data.classworkStudentList.contains(Firebase.auth.currentUser!!.uid)) {
                            pendingWorkLeft.visibility = View.VISIBLE
                        }
                    }
                }
            }

            // click listener
            openClassroom.setOnClickListener {
                onClassroomItemClickListener?.let { it(
                    data,
                    mapOfColors[colorPosition]!!.backColor,
                    mapOfColors[colorPosition]!!.iconColor,
                )}
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