package com.ikki.storyapp

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import com.bumptech.glide.Glide

class DetailStory : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_story)

        val storyImageUrl = intent.getStringExtra("story_image_url")
        val storyAuthor = intent.getStringExtra("story_author")
        val storyDescription = intent.getStringExtra("story_description")

        val imageView: ImageView = findViewById(R.id.storyImage)
        val authorTextView: TextView = findViewById(R.id.storyAuthor)
        val descriptionTextView: TextView = findViewById(R.id.storyDesc)

        ViewCompat.setTransitionName(imageView, intent.getStringExtra("transition_image_name"))
        ViewCompat.setTransitionName(authorTextView, intent.getStringExtra("transition_author_name"))

        storyAuthor?.let { authorTextView.text = it }
        storyDescription?.let { descriptionTextView.text = it }

        storyImageUrl?.let {
            Glide.with(this)
                .load(it)
                .centerCrop()
                .into(imageView)
        }
    }
}