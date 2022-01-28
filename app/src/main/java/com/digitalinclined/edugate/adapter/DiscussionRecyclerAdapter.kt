package com.digitalinclined.edugate.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.digitalinclined.edugate.R
import com.digitalinclined.edugate.databinding.DiscussionFormListItemBinding
import com.digitalinclined.edugate.databinding.SubjectComponentLayoutBinding
import com.digitalinclined.edugate.models.DiscussionDataClass

class DiscussionRecyclerAdapter: RecyclerView.Adapter<DiscussionRecyclerAdapter.DiscussionViewHolder>() {

    // Diff Util Call Back
    private val differCallback = object : DiffUtil.ItemCallback<DiscussionDataClass>() {
        override fun areItemsTheSame(
            oldItem: DiscussionDataClass,
            newItem: DiscussionDataClass
        ): Boolean {
            return oldItem.title == newItem.title
        }

        override fun areContentsTheSame(
            oldItem: DiscussionDataClass,
            newItem: DiscussionDataClass
        ): Boolean {
            return oldItem == newItem
        }
    }

    // Differ Value Setup
    val differ = AsyncListDiffer(this,differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiscussionViewHolder {
        val binding = DiscussionFormListItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return DiscussionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DiscussionViewHolder, position: Int) {

        // data from dataclasses
        val data = differ.currentList[position]

        holder.binding.apply {

            // Image View
            val requestOptions = RequestOptions()
            requestOptions.diskCacheStrategy(DiskCacheStrategy.ALL)
            requestOptions.centerCrop()
            if(data.userImage != null) {
                Glide.with(root)
                    .load(data.userImage)
                    .apply(requestOptions)
                    .into(userProfileImage)
            }

            // name
            name.text = data.name

            // follow / unfollow
            followUser.text = data.follow

            // course and year
            courseYear.text = "${data.course} ${data.courseYear}"

            // date
            date.text = data.publishedDate

            // title
            title.text = data.title

            // content
            content.text = data.content

            // pdf name
            pdfName.text = data.pdfName

            // likes
            likes.text = data.likes.toString()

            // comment
            comments.text = data.comment.toString()

            root.setOnClickListener {
                onItemClickListener?.let { it(data) }
            }
        }

    }

    override fun getItemCount() = differ.currentList.size

    // Inner Class ViewHolder
    inner class DiscussionViewHolder(val binding: DiscussionFormListItemBinding) : RecyclerView.ViewHolder(binding.root)

    // On click listener
    private var onItemClickListener: ((DiscussionDataClass) -> Unit)? = null

    fun setOnItemClickListener(listener: (DiscussionDataClass) -> Unit) {
        onItemClickListener = listener
    }

}