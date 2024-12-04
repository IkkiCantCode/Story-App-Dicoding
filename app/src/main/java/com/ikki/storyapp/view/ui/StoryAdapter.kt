package com.ikki.storyapp.view.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ikki.storyapp.databinding.ItemStoryBinding
import com.ikki.storyapp.response.ListStoryItem

class StoryAdapter(
    private var stories: List<ListStoryItem>,
    private val onItemClick: (ListStoryItem, ImageView, TextView) -> Unit
) : RecyclerView.Adapter<StoryAdapter.StoryViewHolder>() {

    inner class StoryViewHolder(private val binding: ItemStoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

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
        holder.bind(stories[position])
    }

    override fun getItemCount(): Int = stories.size

    fun updateData(newStories: List<ListStoryItem>) {
        stories = newStories
        notifyDataSetChanged()
    }
}