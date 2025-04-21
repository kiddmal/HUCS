package com.example.disasterresponseapp10.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.disasterresponseapp10.R
import com.example.disasterresponseapp10.models.Resource

class ResourceAdapter(
    private val onItemClick: (Resource) -> Unit
) : ListAdapter<Resource, ResourceAdapter.ResourceViewHolder>(ResourceDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResourceViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.resource_item, parent, false)
        return ResourceViewHolder(view, onItemClick)
    }

    override fun onBindViewHolder(holder: ResourceViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ResourceViewHolder(
        itemView: View,
        private val onItemClick: (Resource) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.tvResourceName)
        private val typeTextView: TextView = itemView.findViewById(R.id.tvResourceType)
        private val statusTextView: TextView = itemView.findViewById(R.id.tvResourceStatus)
        private val addressTextView: TextView = itemView.findViewById(R.id.tvResourceAddress)

        fun bind(resource: Resource) {
            nameTextView.text = resource.name
            typeTextView.text = resource.type.name
            statusTextView.text = resource.status.name
            addressTextView.text = resource.location.address

            itemView.setOnClickListener { onItemClick(resource) }
        }
    }

    class ResourceDiffCallback : DiffUtil.ItemCallback<Resource>() {
        override fun areItemsTheSame(oldItem: Resource, newItem: Resource): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Resource, newItem: Resource): Boolean {
            return oldItem == newItem
        }
    }
}