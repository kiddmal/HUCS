package com.example.disasterresponseapp10.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.disasterresponseapp10.databinding.ItemDashboardCategoryBinding
import com.example.disasterresponseapp10.models.DashboardCategory
import com.example.disasterresponseapp10.models.DashboardItem

class DashboardAdapter(
    private var items: List<DashboardItem>,
    private var onCategorySelected: (DashboardCategory) -> Unit
) : ListAdapter<DashboardItem, DashboardAdapter.DashboardViewHolder>(DashboardDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DashboardViewHolder {
        val binding = ItemDashboardCategoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return DashboardViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DashboardViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    private class DashboardDiffCallback : DiffUtil.ItemCallback<DashboardItem>() {
        override fun areItemsTheSame(oldItem: DashboardItem, newItem: DashboardItem): Boolean {
            return oldItem.category == newItem.category
        }

        override fun areContentsTheSame(oldItem: DashboardItem, newItem: DashboardItem): Boolean {
            return oldItem == newItem
        }
    }


    inner class DashboardViewHolder(
        private val binding: ItemDashboardCategoryBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: DashboardItem) {
            binding.apply {
                // Set icon
                categoryIcon.setImageResource(item.category.iconRes)

                // Set text resources
                categoryTitle.setText(item.category.titleRes)
                categoryDescription.setText(item.category.descriptionRes)

                // Handle enabled state
                root.isEnabled = item.isEnabled
                root.alpha = if (item.isEnabled) 1.0f else 0.5f

                // Set click listener
                root.setOnClickListener {
                    if (item.isEnabled) {
                        onCategorySelected(item.category)
                    }
                }

                // Handle status chip if present
                item.statusText?.let { statusText ->
                    statusChip.apply {
                        text = statusText
                        visibility = android.view.View.VISIBLE
                    }
                } ?: run {
                    statusChip.visibility = android.view.View.GONE
                }
            }
        }
    }
}