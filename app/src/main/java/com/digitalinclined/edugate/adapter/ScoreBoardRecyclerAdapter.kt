package com.digitalinclined.edugate.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.digitalinclined.edugate.databinding.ScoreBoardLayoutItemBinding
import com.digitalinclined.edugate.models.quizzesmodel.ClassWorkSubmissionDataClass

class ScoreBoardRecyclerAdapter :
    RecyclerView.Adapter<ScoreBoardRecyclerAdapter.ScoreProfilesViewHolder>() {

    // Diff Util Call Back
    private val differCallback = object : DiffUtil.ItemCallback<ClassWorkSubmissionDataClass>() {
        override fun areItemsTheSame(
            oldItem: ClassWorkSubmissionDataClass,
            newItem: ClassWorkSubmissionDataClass
        ): Boolean {
            return oldItem.userImage == newItem.userImage
        }

        override fun areContentsTheSame(
            oldItem: ClassWorkSubmissionDataClass,
            newItem: ClassWorkSubmissionDataClass
        ): Boolean {
            return oldItem == newItem
        }
    }

    // Differ Value Setup
    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScoreProfilesViewHolder {
        val binding =
            ScoreBoardLayoutItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ScoreProfilesViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ScoreProfilesViewHolder, position: Int) {

        val data = differ.currentList[position]

        holder.binding.apply {

            // Image View
            val requestOptions = RequestOptions()
            requestOptions.diskCacheStrategy(DiskCacheStrategy.ALL)
            requestOptions.centerCrop()
            if (!data.userImage.isNullOrEmpty()) {
                Glide.with(root)
                    .load(data.userImage)
                    .apply(requestOptions)
                    .into(userProfileImage)
            }

            // user name
            nameTV.text = data.userName

            // course name
            courseTV.text = data.userCourse

            // year name
            yearTV.text = data.userSemester

            // user marks
            scoreTV.text = "Score : ${data.userMarks}"

            // user position
            unFollow.text = "${position + 1}"
        }

    }

    override fun getItemCount() = differ.currentList.size

    // Inner Class ViewHolder
    inner class ScoreProfilesViewHolder(val binding: ScoreBoardLayoutItemBinding) :
        RecyclerView.ViewHolder(binding.root)

}