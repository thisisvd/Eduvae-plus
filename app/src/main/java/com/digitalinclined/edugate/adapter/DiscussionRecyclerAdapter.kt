package com.digitalinclined.edugate.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.digitalinclined.edugate.R
import com.digitalinclined.edugate.databinding.DiscussionFormListItemBinding
import com.digitalinclined.edugate.models.DiscussionDataClass
import com.digitalinclined.edugate.utils.DateTimeFormatFetcher

class DiscussionRecyclerAdapter(val context: Context): RecyclerView.Adapter<DiscussionRecyclerAdapter.DiscussionViewHolder>() {

    // temp followers list
    private var followersList = ArrayList<String>()

    // add Followers In The List
    fun addFollowersInTheList(list: ArrayList<String>){
        followersList.addAll(list)
    }

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
            nameTV.text = data.name

            // follow / unfollow
            followUser.text = if(checkForExistFollower(data.userID.toString())){
                followUser.setTextColor(ContextCompat.getColor(context, R.color.green_color))
                "Following"
            } else {
                "Follow"
            }

            // course and year
            courseYear.text = "${data.course} ${data.courseYear}"

            // date
            date.text = DateTimeFormatFetcher().getDateTime(data.publishedDate!!.toLong())

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

            // pdf click listener
            discussLL2.setOnClickListener {
                pdfItemClickListener?.let {
                    it(data.pdfFile!!)
                }
            }

            // follow user click listener
            followUser.setOnClickListener {
                followItemClickListener?.let { it(data,followUser) }
            }
        }

    }

    // check for existing follower
    private fun checkForExistFollower(id: String): Boolean {
        for(item in followersList) {
            if(item == id) {
                return true
            }
        }
        return false
    }

    override fun getItemCount() = differ.currentList.size

    // Inner Class ViewHolder
    inner class DiscussionViewHolder(val binding: DiscussionFormListItemBinding) : RecyclerView.ViewHolder(binding.root)

    // On follow click listener
    private var followItemClickListener: ((DiscussionDataClass, view: TextView) -> Unit)? = null

    fun setOnFollowItemClickListener(listener: (DiscussionDataClass, view: TextView) -> Unit) {
        followItemClickListener = listener
    }

    // On pdf click listener
    private var pdfItemClickListener: ((pdfLink: String) -> Unit)? = null

    fun setOnPdfItemClickListener(listener: (pdfLink: String) -> Unit) {
        pdfItemClickListener = listener
    }
}