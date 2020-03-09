package com.example.simplecount.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.simplecount.data.db.entity.Event
import com.example.simplecount.databinding.EventBinding
import androidx.recyclerview.widget.ListAdapter

class HomeEventAdapter (
    private val onEventClickListener : EventClickListener) : ListAdapter<Event, HomeEventAdapter.HomeEventViewHolder>(EventDiffCallback()) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeEventViewHolder {
        return HomeEventViewHolder.from(parent)
    }


    override fun onBindViewHolder(holder: HomeEventViewHolder, position: Int) {
        holder.bind(onEventClickListener, getItem(position))
    }

    class HomeEventViewHolder private constructor(val binding: EventBinding) : RecyclerView.ViewHolder(binding.root){

        fun bind(clickListener: EventClickListener, event: Event){
            binding.clickListener = clickListener
            binding.event = event
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): HomeEventViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = EventBinding.inflate(layoutInflater, parent, false)

                return HomeEventViewHolder(binding)
            }
        }

    }
}

class EventDiffCallback : DiffUtil.ItemCallback<Event>() {

    override fun areItemsTheSame(oldItem: Event, newItem: Event): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Event, newItem: Event): Boolean {
        return oldItem == newItem
    }
}

class EventClickListener(val clickListener : (id : Long) -> Unit) {

    fun onClick(event: Event) = clickListener(event.id)
}