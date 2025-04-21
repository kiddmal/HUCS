package com.example.disasterresponseapp10.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.disasterresponseapp10.databinding.ItemCommunicationOptionBinding
import com.example.disasterresponseapp10.models.CommunicationOption
import com.example.disasterresponseapp10.utils.UserManager.isAuthenticated

// CommunicationAdapter.kt
class CommunicationAdapter(
    private val options: List<CommunicationOption>,
    private val onOptionSelected: (CommunicationOption) -> Unit
) : RecyclerView.Adapter<CommunicationAdapter.OptionViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OptionViewHolder {
        val binding = ItemCommunicationOptionBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return OptionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OptionViewHolder, position: Int) {
        holder.bind(options[position])
    }

    override fun getItemCount(): Int = options.size

    inner class OptionViewHolder(
        private val binding: ItemCommunicationOptionBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(option: CommunicationOption) {
            binding.apply {
                icon.setImageResource(option.iconRes)
                title.setText(option.titleRes)
                description.setText(option.descriptionRes)

                root.isEnabled = !option.requiresAuth || isAuthenticated()
                root.alpha = if (root.isEnabled) 1.0f else 0.5f

                root.setOnClickListener {
                    if (root.isEnabled) {
                        onOptionSelected(option)
                    }
                }
            }
        }
    }
}

