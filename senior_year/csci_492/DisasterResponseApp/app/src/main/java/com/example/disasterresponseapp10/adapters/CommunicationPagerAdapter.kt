package com.example.disasterresponseapp10.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.disasterresponseapp10.viewmodels.UnifiedCommunicationViewModel

// CommunicationPagerAdapter.kt
class CommunicationPagerAdapter(
    private val fragments: List<Fragment>,
    private val viewModel: UnifiedCommunicationViewModel,
    fm: FragmentManager,
    lifecycle: Lifecycle
) : FragmentStateAdapter(fm, lifecycle) {
    override fun getItemCount(): Int = fragments.size
    override fun createFragment(position: Int): Fragment = fragments[position]
}

// Communication Sections
abstract class BaseCommunicationSection : Fragment() {
    // Use proper property declaration
    private var _viewModel: UnifiedCommunicationViewModel? = null
    protected val viewModel: UnifiedCommunicationViewModel
        get() = _viewModel ?: throw IllegalStateException("ViewModel not initialized")

    abstract fun getTitle(): String
    abstract fun onMessageSent(message: String)

    fun initializeViewModel(model: UnifiedCommunicationViewModel) {
        _viewModel = model
    }
}

class NotifySection : BaseCommunicationSection() {
    override fun getTitle() = "Notify Family"

    override fun onMessageSent(message: String) {
        viewModel.sendNotification(message)
    }
}

class TeamSection : BaseCommunicationSection() {
    override fun getTitle() = "Team Chat"

    override fun onMessageSent(message: String) {
        viewModel.sendTeamMessage(message)
    }
}

class CommunitySection : BaseCommunicationSection() {
    override fun getTitle() = "Community"

    override fun onMessageSent(message: String) {
        viewModel.sendCommunityMessage(message)
    }
}