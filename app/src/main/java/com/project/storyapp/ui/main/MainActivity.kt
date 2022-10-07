package com.project.storyapp.ui.main

import android.app.ActivityOptions
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Pair
import android.view.Menu
import android.view.MenuItem
import com.project.storyapp.R
import com.project.storyapp.databinding.ActivityMainBinding
import com.project.storyapp.ui.settings.SettingsActivity
import com.project.storyapp.ui.add.AddStoryActivity
import com.project.storyapp.ui.detail.DetailActivity
import com.project.storyapp.ui.explore_location.ExploreLocationActivity
import com.project.storyapp.utils.gone
import com.project.storyapp.utils.scope
import com.project.storyapp.utils.showToast
import com.project.storyapp.utils.visible
import kotlinx.coroutines.*
import org.koin.android.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModel()

    private lateinit var storiesAdapter: StoriesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // init
        storiesAdapter = StoriesAdapter().apply {
            onClick { story, itemListStoryBinding ->
                val optionsCompat = ActivityOptions.makeSceneTransitionAnimation(
                    this@MainActivity,
                    Pair(itemListStoryBinding.imgPoster, "image_story"),
                    Pair(itemListStoryBinding.tvName, "name"),
                    Pair(itemListStoryBinding.tvUploadTimeStory, "uploaded_story")
                )

                Intent(this@MainActivity, DetailActivity::class.java).also { intent ->
                    intent.putExtra(DetailActivity.DATA_STORY, story)
                    startActivity(intent, optionsCompat.toBundle())
                }
            }
        }

        getStories()

        setUpToolbar()

        onAction()

        getNewStory()
    }

    private fun getNewStory() {
        binding.apply {
            if (intent != null) {

                val isNewStory = intent.extras?.getBoolean(SUCCESS_UPLOAD_STORY, false)

                if (isNewStory != null && isNewStory) {
                    swipeMain.isRefreshing = true
                    getStories()
                    this@MainActivity.showToast(getString(R.string.story_uploaded))
                }
            }
        }
    }

    private fun onAction() {
        binding.apply {
            swipeMain.setOnRefreshListener {
                swipeMain.isRefreshing = true
                getStories()
            }

            fabAddNewStory.setOnClickListener {
                Intent(this@MainActivity, AddStoryActivity::class.java).also { intent ->
                    startActivity(intent)
                }
            }
        }
    }

    private fun getStories() {
        binding.apply {

            loading.visible()

            storiesAdapter.refresh()
            scope.launch {
                val token = "Bearer ${viewModel.getUserToken()}"

                withContext(Dispatchers.Main) {
                    viewModel.getStories(token).observe(this@MainActivity) {
                        storiesAdapter.submitData(lifecycle, it)
                    }

                    loading.gone()
                    swipeMain.isRefreshing = false

                    rvStory.apply {
                        adapter =
                            storiesAdapter.withLoadStateFooter(footer = StoryLoadingStateAdapter { storiesAdapter.retry() })
                        setHasFixedSize(true)
                    }
                }
                storiesAdapter.loadStateFlow.collect{
                    rvStory.smoothScrollToPosition(0)
                }

            }
        }
    }

    private fun setUpToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.elevation = 0f
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.option_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_settings ->
                Intent(this, SettingsActivity::class.java).also { intent ->
                    startActivity(intent)
                }
            R.id.action_explore_location -> Intent(
                this,
                ExploreLocationActivity::class.java
            ).also { intent ->
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        const val SUCCESS_UPLOAD_STORY = "success upload story"
    }
}