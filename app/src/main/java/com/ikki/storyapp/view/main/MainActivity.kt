package com.ikki.storyapp.view.main

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityOptionsCompat
import androidx.core.view.ViewCompat
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.ikki.storyapp.DetailStory
import com.ikki.storyapp.R
import com.ikki.storyapp.ViewModelFactory
import com.ikki.storyapp.databinding.ActivityMainBinding
import com.ikki.storyapp.response.ListStoryItem
import com.ikki.storyapp.view.post.PostActivity
import com.ikki.storyapp.view.ui.StoryAdapter
import com.ikki.storyapp.view.welcome.WelcomeActivity
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var binding: ActivityMainBinding
    private lateinit var storyAdapter: StoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupViewModel()
        setupToolbar()
        setupView()
        setupFab()
    }

    private fun setupRecyclerView() {
        storyAdapter = StoryAdapter { story, imageView, textView ->
            val intent = Intent(this, DetailStory::class.java).apply {
                putExtra("story_image_url", story.photoUrl)
                putExtra("story_author", story.name)
                putExtra("story_description", story.description)
            }
            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                this,
                androidx.core.util.Pair(imageView, ViewCompat.getTransitionName(imageView) ?: ""),
                androidx.core.util.Pair(textView, ViewCompat.getTransitionName(textView) ?: "")
            )
            startActivity(intent, options.toBundle())
        }

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = storyAdapter
            addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL))
        }
    }

    private fun setupViewModel() {
        viewModel.getSession()

        viewModel.session.observe(this) { user ->
            if (user.isLogin) {
                // Observe Paging data Flow
                viewModel.stories.observe(this) { flow ->
                    lifecycleScope.launch {
                        flow.collectLatest { pagingData ->
                            // Submit the collected PagingData to the adapter
                            storyAdapter.submitData(pagingData)
                        }
                    }
                }

                // Listen to the refresh trigger and reload the data
                viewModel.refreshTrigger.observe(this) {
                    // Manually trigger a refresh of the stories list
                    lifecycleScope.launch {
                        // Clear any cached data
                        storyAdapter.refresh()
                    }
                }

            } else {
                navigateToWelcome()
            }
        }

        // Add a loading state observer
        lifecycleScope.launch {
            storyAdapter.loadStateFlow.collectLatest { loadStates ->
                // Handle loading states, show/hide progress bar etc.
                val isLoading = loadStates.refresh is LoadState.Loading
                // Update UI accordingly
            }
        }

        viewModel.error.observe(this) { errorMessage ->
            Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupToolbar() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
    }

    private fun setupFab() {
        val fabPost = findViewById<AppCompatImageButton>(R.id.fabPost)
        fabPost.setOnClickListener {
            val intent = Intent(this, PostActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
    }

    private fun navigateToWelcome() {
        startActivity(Intent(this, WelcomeActivity::class.java))
        finish()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                viewModel.logout()
                navigateToWelcome()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}