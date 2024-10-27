package com.dicoding.aplikasidicodingevent.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.aplikasidicodingevent.data.ListEventsItem
import com.dicoding.aplikasidicodingevent.databinding.ItemRowImageBinding
import com.dicoding.aplikasidicodingevent.extensions.loadImage

class EventAdapter(
    private val context: Context,
    private val itemClickListener: (ListEventsItem) -> Unit
) : RecyclerView.Adapter<EventAdapter.EventViewHolder>() {

    private var events: List<ListEventsItem> = listOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val binding = ItemRowImageBinding.inflate(LayoutInflater.from(context), parent, false)
        return EventViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val event = events[position]
        holder.itemText.text = event.name
        holder.itemImage.loadImage(event.imageLogo ?: event.mediaCover)

        holder.itemView.setOnClickListener {
            itemClickListener(event)
        }
    }

    override fun getItemCount(): Int = events.size

    fun submitList(newEvents: List<ListEventsItem>) {
        val diffCallback = EventsDiffCallback(events, newEvents)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        events = newEvents
        diffResult.dispatchUpdatesTo(this)
    }

    class EventViewHolder(binding: ItemRowImageBinding) : RecyclerView.ViewHolder(binding.root) {
        val itemImage = binding.itemImage
        val itemText = binding.itemText
    }

    class EventsDiffCallback(
        private val oldList: List<ListEventsItem>,
        private val newList: List<ListEventsItem>
    ) : DiffUtil.Callback() {

        override fun getOldListSize(): Int = oldList.size
        override fun getNewListSize(): Int = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].id == newList[newItemPosition].id
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }
    }
}
