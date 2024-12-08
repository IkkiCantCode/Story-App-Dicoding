package com.ikki.storyapp.view.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.ViewCompat
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ikki.storyapp.databinding.ItemStoryBinding
import com.ikki.storyapp.response.ListStoryItem

class StoryAdapter(
    private val onItemClick: (ListStoryItem, ImageView, TextView) -> Unit
) : PagingDataAdapter<ListStoryItem, StoryAdapter.StoryViewHolder>(DIFF_CALLBACK) {

    inner class StoryViewHolder(private val binding: ItemStoryBinding) :
        androidx.recyclerview.widget.RecyclerView.ViewHolder(binding.root) {

        fun bind(story: ListStoryItem) {
            binding.apply {
                textEventName.text = story.name ?: "Unknown User"

                Glide.with(itemView.context)
                    .load(story.photoUrl)
                    .centerCrop()
                    .into(imageEvent)

                ViewCompat.setTransitionName(imageEvent, "image_${story.id}")
                ViewCompat.setTransitionName(textEventName, "author_${story.id}")

                root.setOnClickListener {
                    onItemClick(story, imageEvent, textEventName)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewHolder {
        val binding = ItemStoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return StoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
        val story = getItem(position)
        if (story != null) {
            holder.bind(story)
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListStoryItem>() {
            override fun areItemsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem == newItem
            }
        }
    }
}