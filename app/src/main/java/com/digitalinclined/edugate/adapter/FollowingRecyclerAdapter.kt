package com.digitalinclined.edugate.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.digitalinclined.edugate.databinding.FollowingLayoutItemBinding
import com.digitalinclined.edugate.models.UserFollowingProfile

class FollowingRecyclerAdapter : RecyclerView.Adapter<FollowingRecyclerAdapter.UserFollowingProfilesViewHolder>() {

    // Diff Util Call Back
    private val differCallback = object : DiffUtil.ItemCallback<UserFollowingProfile>() {
        override fun areItemsTheSame(
            oldItem: UserFollowingProfile,
            newItem: UserFollowingProfile
        ): Boolean {
            return oldItem.profilephotolink == newItem.profilephotolink
        }

        override fun areContentsTheSame(
            oldItem: UserFollowingProfile,
            newItem: UserFollowingProfile
        ): Boolean {
            return oldItem == newItem
        }
    }

    // Differ Value Setup
    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): UserFollowingProfilesViewHolder {
        val binding =
            FollowingLayoutItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserFollowingProfilesViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserFollowingProfilesViewHolder, position: Int) {

        val data = differ.currentList[position]

        holder.binding.apply {

            // Image View
            val requestOptions = RequestOptions()
            requestOptions.diskCacheStrategy(DiskCacheStrategy.ALL)
            requestOptions.centerCrop()
            if (!data.profilephotolink.isNullOrEmpty()) {
                Glide.with(root)
                    .load(data.profilephotolink)
                    .apply(requestOptions)
                    .into(userProfileImage)
            }

            // user name
            nameTV.text = data.name

            // course name
            courseTV.text = data.course

            // year name
            yearTV.text = data.year

            unFollow.setOnClickListener {
                onItemClickListener?.let { it(data) }
            }

        }

    }

    override fun getItemCount() = differ.currentList.size

    // Inner Class ViewHolder
    inner class UserFollowingProfilesViewHolder(val binding: FollowingLayoutItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    // On click listener
    private var onItemClickListener: ((UserFollowingProfile) -> Unit)? = null

    fun setOnItemClickListener(listener: (UserFollowingProfile) -> Unit) {
        onItemClickListener = listener
    }
}