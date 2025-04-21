package com.example.disasterresponseapp10.fragments.notifications

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.disasterresponseapp10.R
import com.example.disasterresponseapp10.adapters.NotificationsAdapter
import com.example.disasterresponseapp10.databinding.FragmentNotificationsBinding
import com.example.disasterresponseapp10.models.Notification
import com.example.disasterresponseapp10.models.NotificationType
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NotificationsFragment : Fragment() {
    private var _binding: FragmentNotificationsBinding? = null
    private val binding get() = _binding!!
    private lateinit var notificationsAdapter: NotificationsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
//        setupToolbar()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        loadNotifications()
    }

    private fun setupRecyclerView() {
        notificationsAdapter = NotificationsAdapter { notification ->
            handleNotificationClick(notification)
        }

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = notificationsAdapter
            addItemDecoration(
                DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
            )
        }

        binding.swipeRefreshLayout.setOnRefreshListener {
            loadNotifications()
        }
    }

//    private fun setupToolbar() {
//        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.toolbar)
//    }

    private fun loadNotifications() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                binding.progressBar.isVisible = true
                val notifications = withContext(Dispatchers.IO) {
                    // TODO: Replace with actual API call
                    emptyList<Notification>()
                }
                notificationsAdapter.submitList(notifications)
                binding.emptyView.isVisible = notifications.isEmpty()
            } catch (e: Exception) {
                showError()
            } finally {
                binding.progressBar.isVisible = false
                binding.swipeRefreshLayout.isRefreshing = false
            }
        }
    }

    private fun handleNotificationClick(notification: Notification) {
        when (notification.type) {
            NotificationType.RESOURCE_UPDATE -> findNavController()
                .navigate(R.id.unifiedResourceFragment)
            NotificationType.EMERGENCY_ALERT -> findNavController()
                .navigate(R.id.unifiedResourceFragment)
            NotificationType.DELIVERY_UPDATE -> findNavController()
                .navigate(R.id.deliveriesFragment)
            NotificationType.FAMILY_ALERT -> findNavController()
                .navigate(R.id.notifyFamilyFragment)
            NotificationType.SYSTEM_MESSAGE -> {
                // Handle system messages
            }
        }
    }

    private fun showError() {
        Snackbar.make(
            binding.root,
            R.string.error_loading_notifications,
            Snackbar.LENGTH_LONG
        ).setAction(R.string.retry) {
            loadNotifications()
        }.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}