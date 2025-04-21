package com.example.disasterresponseapp10.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.disasterresponseapp10.R
import com.example.disasterresponseapp10.databinding.ItemNotificationBinding
import com.example.disasterresponseapp10.models.Notification
import com.example.disasterresponseapp10.models.NotificationType
import java.text.SimpleDateFormat
import java.util.Locale

class NotificationsAdapter(
    private val onNotificationClick: (Notification) -> Unit
) : ListAdapter<Notification, NotificationsAdapter.NotificationViewHolder>(NotificationDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val binding = ItemNotificationBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return NotificationViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class NotificationViewHolder(
        private val binding: ItemNotificationBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        // Cache frequently accessed views and objects
        private val notificationIcon = binding.notificationIcon
        private val notificationTitle = binding.notificationTitle
        private val notificationMessage = binding.notificationMessage
        private val notificationTime = binding.notificationTime
        private val unreadIndicator = binding.unreadIndicator
        private val root = binding.root
        private val dateFormat = SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault())

        fun bind(notification: Notification) {
            // Reset view states
            root.alpha = 1.0f
            notificationIcon.clearColorFilter()

            // Get notification resources
            val (iconRes, colorRes) = getNotificationTypeResources(notification.type)

            // Update views
            notificationIcon.setImageResource(iconRes)
            notificationIcon.setColorFilter(
                ContextCompat.getColor(root.context, colorRes)
            )
            notificationTitle.text = notification.title
            notificationMessage.text = notification.message
            notificationTime.text = dateFormat.format(notification.timestamp)

            // Handle read status
            root.alpha = if (notification.isRead) 0.7f else 1.0f
            unreadIndicator.isVisible = !notification.isRead

            root.setOnClickListener { onNotificationClick(notification) }
        }

        private fun getNotificationTypeResources(type: NotificationType): Pair<Int, Int> =
            when (type) {
                NotificationType.EMERGENCY_ALERT -> R.drawable.ic_emergency to R.color.notification_emergency
                NotificationType.RESOURCE_UPDATE -> R.drawable.ic_resources to R.color.notification_resource
                NotificationType.DELIVERY_UPDATE -> R.drawable.ic_delivery to R.color.notification_delivery
                NotificationType.SYSTEM_MESSAGE -> R.drawable.ic_info to R.color.notification_system
                NotificationType.FAMILY_ALERT -> R.drawable.ic_family to R.color.notification_family
            }
    }

    private class NotificationDiffCallback : DiffUtil.ItemCallback<Notification>() {
        override fun areItemsTheSame(oldItem: Notification, newItem: Notification): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Notification, newItem: Notification): Boolean =
            oldItem == newItem
    }
}