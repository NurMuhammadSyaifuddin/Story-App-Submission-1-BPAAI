package com.project.storyapp.ui.detail

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.project.core.domain.model.Story
import com.project.storyapp.R
import com.project.storyapp.databinding.ActivityDetailBinding
import com.project.storyapp.utils.getTimeLineUploaded
import com.project.storyapp.utils.loadImage

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getStory()
    }

    @SuppressLint("SetTextI18n")
    private fun getStory() {
        binding.apply {
            val story = intent?.extras?.getParcelable<Story>(DATA_STORY)

            imgPoster.loadImage(story?.photoUrl.toString())
            tvName.text = story?.name.toString()
            tvDesc.text = story?.description.toString()
            tvUploadedStory.text = "🕓 ${getString(R.string.text_uploaded)} ${getTimeLineUploaded(this@DetailActivity, story?.createdAt.toString())}"
        }
    }

    companion object{
        const val DATA_STORY = "data_story"
    }
}