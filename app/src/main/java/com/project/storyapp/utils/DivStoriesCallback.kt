package com.project.storyapp.utils

import androidx.recyclerview.widget.DiffUtil
import com.project.core.domain.model.Story

class DivStoriesCallback(private val oldStories: List<Story>, private val newStories: List<Story>): DiffUtil.Callback() {
    override fun getOldListSize(): Int = oldStories.size

    override fun getNewListSize(): Int = newStories.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
        oldStories[oldItemPosition].id == newStories[newItemPosition].id

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
        oldStories[oldItemPosition].id == newStories[newItemPosition].id
}