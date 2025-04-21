package com.example.disasterresponseapp10.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.disasterresponseapp10.databinding.ItemResponseTeamOptionBinding
import com.example.disasterresponseapp10.models.ResponseTeamOption
import com.example.disasterresponseapp10.utils.UserManager.isAuthenticated

class ResponseTeamAdapter(
    private val options: List<ResponseTeamOption>,
    private val onOptionSelected: (ResponseTeamOption) -> Unit
) : RecyclerView.Adapter<ResponseTeamAdapter.OptionViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OptionViewHolder {
        val binding = ItemResponseTeamOptionBinding.inflate(
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
        private val binding: ItemResponseTeamOptionBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(option: ResponseTeamOption) {
            binding.apply {
                responseIcon.setImageResource(option.iconRes)
                responseTitle.setText(option.titleRes)
                responseDescription.setText(option.descriptionRes)

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