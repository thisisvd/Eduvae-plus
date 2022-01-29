package com.digitalinclined.edugate.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.digitalinclined.edugate.databinding.NotificationItemLayoutBinding
import com.digitalinclined.edugate.models.NotificationDataClass

class NotificationAdapter: RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>(){

    // Diff Util Call Back
    private val differCallback = object : DiffUtil.ItemCallback<NotificationDataClass>() {
        override fun areItemsTheSame(
            oldItem: NotificationDataClass,
            newItem: NotificationDataClass
        ): Boolean {
            return oldItem.title == newItem.title
        }

        override fun areContentsTheSame(
            oldItem: NotificationDataClass,
            newItem: NotificationDataClass
        ): Boolean {
            return oldItem == newItem
        }
    }

    // Differ Value Setup
    val differ = AsyncListDiffer(this,differCallback)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): NotificationViewHolder {
        val binding = NotificationItemLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return NotificationViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {

        // data from the dataclasses
        val data = differ.currentList[position]

        holder.binding.apply {
            data.apply {

                // set date
                date
                
                // title
                notificationTitleTV.text = title
                
                //content
                notificationContentTV.text = content
                
                // details
                viewDetailsTV.setOnClickListener {
                    onItemClickListener?.let { it(data) }
                }
               
            }
        }
    }

    override fun getItemCount() = differ.currentList.size

    // Inner Class ViewHolder
    inner class NotificationViewHolder(val binding: NotificationItemLayoutBinding): RecyclerView.ViewHolder(binding.root)

    // On click listener
    private var onItemClickListener: ((NotificationDataClass) -> Unit)? = null

    fun setOnItemClickListener(listener: (NotificationDataClass) -> Unit) {
        onItemClickListener = listener
    }

}