package com.project.storyapp.ui.main

import android.app.ActivityOptions
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Pair
import android.view.Menu
import android.view.MenuItem
import com.project.core.data.source.Resource
import com.project.core.domain.model.Story
import com.project.storyapp.R
import com.project.storyapp.databinding.ActivityMainBinding
import com.project.storyapp.ui.settings.SettingsActivity
import com.project.storyapp.ui.add.AddStoryActivity
import com.project.storyapp.ui.detail.DetailActivity
import com.project.storyapp.utils.gone
import com.project.storyapp.utils.showToast
import com.project.storyapp.utils.visible
import kotlinx.coroutines.*
import org.koin.android.viewmodel.ext.android.viewModel
import timber.log.Timber
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModel()

    private lateinit var adapter: StoriesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // init
        adapter = StoriesAdapter().apply {
            onClick { story, itemListStoryBinding ->
                val optionsCompat = ActivityOptions.makeSceneTransitionAnimation(this@MainActivity,
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

                val isNewStory = intent.extras?.getBoolean(SUCCESS_UPLOAD_STORY)

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
            val dispatcher = Executors.newSingleThreadExecutor().asCoroutineDispatcher()
            val scope = CoroutineScope(dispatcher)

            scope.launch {
                val token = "Bearer ${viewModel.getUserToken()}"

                withContext(Dispatchers.Main) {
                    viewModel.getStories(token).observe(this@MainActivity) { resource ->
                        when (resource) {
                            is Resource.Loading -> {
                                loading.visible()
                                rvStory.gone()
                            }
                            is Resource.Success -> {
                                loading.gone()
                                rvStory.visible()

                                val stories = resource.data?.sortedByDescending { it.createdAt }

                                if (!stories.isNullOrEmpty()) {
                                    adapter.stories = stories as MutableList<Story>
                                    showEmptyStories(false)
                                } else showEmptyStories(true)

                            }
                            is Resource.Error -> {
                                loading.gone()
                                rvStory.gone()
                                this@MainActivity.showToast(resource.message.toString())
                                Timber.e(resource.message.toString())
                            }
                        }
                    }

                    rvStory.setHasFixedSize(true)
                    rvStory.adapter = adapter

                    swipeMain.isRefreshing = false
                }

            }
        }
    }

    private fun showEmptyStories(state: Boolean) {
        binding.apply {
            if (state) {
                imgEmptyState.visible()
                titleEmptyState.visible()
                descEmptyState.visible()
                rvStory.gone()
            } else {
                imgEmptyState.gone()
                titleEmptyState.gone()
                descEmptyState.gone()
                rvStory.visible()
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
            R.id.action_settings -> {
                Intent(this, SettingsActivity::class.java).also { intent ->
                    startActivity(intent)
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        const val SUCCESS_UPLOAD_STORY = "success upload story"
    }
}