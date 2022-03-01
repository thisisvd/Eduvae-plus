package com.digitalinclined.edugate.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.digitalinclined.edugate.databinding.NotesComponentLayoutBinding
import com.digitalinclined.edugate.databinding.SubjectComponentLayoutBinding
import com.digitalinclined.edugate.restapi.models.notes.Note

class NotesRecyclerAdapter: RecyclerView.Adapter<NotesRecyclerAdapter.NotesViewHolder>() {

    // Diff Util Call Back
    private val differCallback = object : DiffUtil.ItemCallback<Note>() {
        override fun areItemsTheSame(
            oldItem: Note,
            newItem: Note
        ): Boolean {
            return oldItem.notesPDF == newItem.notesPDF
        }

        override fun areContentsTheSame(
            oldItem: Note,
            newItem: Note
        ): Boolean {
            return oldItem == newItem
        }
    }

    // Differ Value Setup
    val differ = AsyncListDiffer(this,differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotesViewHolder {
        val binding = NotesComponentLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return NotesViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NotesViewHolder, position: Int) {

        val data = differ.currentList[position]

        holder.binding.apply {

            // main text
            textValueMain.text = data.notesName

            root.setOnClickListener {
                onItemClickListener?.let { it(data) }
            }
        }

    }

    override fun getItemCount() = differ.currentList.size

    // Inner Class ViewHolder
    inner class NotesViewHolder(val binding: NotesComponentLayoutBinding) : RecyclerView.ViewHolder(binding.root)

    // On click listener
    private var onItemClickListener: ((Note) -> Unit)? = null

    fun setOnItemClickListener(listener: (Note) -> Unit) {
        onItemClickListener = listener
    }

}