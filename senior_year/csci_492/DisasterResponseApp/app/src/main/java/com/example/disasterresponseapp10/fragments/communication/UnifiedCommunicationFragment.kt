package com.example.disasterresponseapp10.fragments.communication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.disasterresponseapp10.adapters.BaseCommunicationSection
import com.example.disasterresponseapp10.adapters.CommunicationPagerAdapter
import com.example.disasterresponseapp10.adapters.CommunitySection
import com.example.disasterresponseapp10.adapters.NotifySection
import com.example.disasterresponseapp10.adapters.TeamSection
import com.example.disasterresponseapp10.databinding.FragmentUnifiedCommunicationBinding
import com.example.disasterresponseapp10.models.UserRole
import com.example.disasterresponseapp10.utils.UserManager
import com.example.disasterresponseapp10.viewmodels.UnifiedCommunicationViewModel
import com.google.android.material.tabs.TabLayoutMediator

class UnifiedCommunicationFragment : Fragment() {
    private var _binding: FragmentUnifiedCommunicationBinding? = null
    private val binding get() = _binding!!
    private val viewModel: UnifiedCommunicationViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUnifiedCommunicationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        observeViewModel()
    }

    private fun setupUI() {
        setupViewPager()
        setupTabs()
    }

    private fun setupViewPager() {
        val sections = getSectionsForRole()
        sections.forEach { section ->
            when (section) {
                is BaseCommunicationSection -> section.initializeViewModel(viewModel)
            }
        }

        binding.viewPager.apply {
            adapter = CommunicationPagerAdapter(
                fragments = sections,
                viewModel = viewModel,
                fm = childFragmentManager,
                lifecycle = lifecycle
            )
            isUserInputEnabled = true
        }
    }

    private fun setupTabs() {
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = getTitleForPosition(position)
        }.attach()
    }

    private fun getSectionsForRole(): List<Fragment> =
        when (UserManager.getCurrentRole()) {
            UserRole.CIVILIAN -> listOf(
                NotifySection(),
                CommunitySection()
            )
            else -> listOf(
                TeamSection(),
                CommunitySection()
            )
        }

    private fun getTitleForPosition(position: Int): String =
        when (position) {
            0 -> if (UserManager.getCurrentRole() == UserRole.CIVILIAN) "Notify" else "Team"
            1 -> "Community"
            else -> ""
        }

    private fun observeViewModel() {
        // Implement viewModel observation logic here
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}