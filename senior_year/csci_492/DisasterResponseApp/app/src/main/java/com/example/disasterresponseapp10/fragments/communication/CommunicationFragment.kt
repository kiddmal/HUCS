package com.example.disasterresponseapp10.fragments.communication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.disasterresponseapp10.R
import com.example.disasterresponseapp10.databinding.FragmentCommunicationBinding
import com.example.disasterresponseapp10.adapters.CommunicationAdapter
import com.example.disasterresponseapp10.models.CommunicationOption
import com.example.disasterresponseapp10.utils.UserManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class CommunicationFragment : Fragment() {
    private var _binding: FragmentCommunicationBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCommunicationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = CommunicationAdapter(getCommunicationOptions()) { option ->
                handleCommunicationSelection(option)
            }
        }
    }

    private fun handleCommunicationSelection(option: CommunicationOption) {
        when (option.type) {
            CommunicationOption.Type.SAFETY_CHECK ->
                findNavController().navigate(R.id.notifyFamilyFragment)
            CommunicationOption.Type.EMERGENCY_CONTACTS ->
                findNavController().navigate(R.id.emergencyContactsFragment)
            CommunicationOption.Type.COMMUNITY_UPDATES ->
                findNavController().navigate(R.id.communityUpdatesFragment)
            CommunicationOption.Type.TEAM_CHAT -> {
                if (UserManager.isAuthenticated()) {
                    findNavController().navigate(R.id.teamChatFragment)
                } else {
                    showAuthDialog()
                }
            }
        }
    }

    private fun showAuthDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.auth_required)
            .setMessage(R.string.auth_required_message)
            .setPositiveButton(R.string.login) { _, _ ->
                findNavController().navigate(R.id.action_global_to_login)
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    private fun getCommunicationOptions(): List<CommunicationOption> = listOf(
        CommunicationOption(
            type = CommunicationOption.Type.SAFETY_CHECK,
            titleRes = R.string.safety_check,
            iconRes = R.drawable.ic_safety_check,
            descriptionRes = R.string.safety_check_description
        ),
        CommunicationOption(
            type = CommunicationOption.Type.EMERGENCY_CONTACTS,
            titleRes = R.string.emergency_contacts,
            iconRes = R.drawable.ic_contacts,
            descriptionRes = R.string.emergency_contacts_description
        ),
        CommunicationOption(
            type = CommunicationOption.Type.COMMUNITY_UPDATES,
            titleRes = R.string.community_updates,
            iconRes = R.drawable.ic_community,
            descriptionRes = R.string.community_updates_description
        ),
        CommunicationOption(
            type = CommunicationOption.Type.TEAM_CHAT,
            titleRes = R.string.team_chat,
            iconRes = R.drawable.ic_chat,
            descriptionRes = R.string.team_chat_description,
            requiresAuth = true
        )
    )

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}