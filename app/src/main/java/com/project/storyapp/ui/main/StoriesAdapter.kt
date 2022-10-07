package com.project.storyapp.ui.main

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.project.core.domain.model.Story
import com.project.storyapp.R
import com.project.storyapp.databinding.ItemListStoryBinding
import com.project.storyapp.utils.getTimeLineUploaded
import com.project.storyapp.utils.loadImage

class StoriesAdapter: PagingDataAdapter<Story, StoriesAdapter.ViewHolder>(DIFF_CALLBACK) {

    private var listener: ((Story, ItemListStoryBinding) -> Unit)? = null

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
        getItem(position)?.let {
            holder.bind(it)
        }
    }

    fun onClick(listener: ((Story, ItemListStoryBinding) -> Unit)?){
        this.listener = listener
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Story>() {
            override fun areItemsTheSame(oldItem: Story, newItem: Story): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: Story, newItem: Story): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }

}