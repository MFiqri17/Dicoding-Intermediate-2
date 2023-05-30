package com.project.storyapp.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.project.storyapp.R
import com.project.storyapp.data.remote.response.Story

class StoryAdapter(private val listStory: List<Story>, private val onItemClick: (data: Story) -> Unit) : RecyclerView.Adapter<StoryAdapter.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvName: TextView = view.findViewById(R.id.tv_item_name)
        val image: ImageView = view.findViewById(R.id.iv_item_photo)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_story, parent, false))


    override fun getItemCount() = listStory.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val story = listStory[position]
        holder.tvName.text = story.name

        Glide.with(holder.itemView.context)
            .load(story.photoUrl)
            .into(holder.image)

        holder.itemView.setOnClickListener {onItemClick(story)}
    }
}