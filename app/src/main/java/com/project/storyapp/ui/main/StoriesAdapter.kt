package com.project.storyapp.ui.main

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.project.core.domain.model.Story
import com.project.storyapp.R
import com.project.storyapp.databinding.ItemListStoryBinding
import com.project.storyapp.utils.DivStoriesCallback
import com.project.storyapp.utils.getTimeLineUploaded
import com.project.storyapp.utils.loadImage

class StoriesAdapter: RecyclerView.Adapter<StoriesAdapter.ViewHolder>() {

    private var listener: ((Story, ItemListStoryBinding) -> Unit)? = null

    var stories = mutableListOf<Story>()
    set(value) {
        val callback = DivStoriesCallback(field, value)
        val result = DiffUtil.calculateDiff(callback)
        field.clear()
        field.addAll(value)
        result.dispatchUpdatesTo(this)
    }

    inner class ViewHolder(private val binding: ItemListStoryBinding): RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(story: Story){
            binding.apply {
                tvName.text = story.name
                tvUploadTimeStory.text = "🕓 ${itemView.context.getString(R.string.text_uploaded)} ${getTimeLineUploaded(itemView.context, story.createdAt)}"
                imgPoster.loadImage(story.photoUrl)

                listener?.let {
                    itemView.setOnClickListener {
                        it(story, binding)
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemListStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(stories[position])
    }

    override fun getItemCount(): Int = stories.size

    fun onClick(listener: ((Story, ItemListStoryBinding) -> Unit)?){
        this.listener = listener
    }
}