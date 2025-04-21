package com.example.disasterresponseapp10.fragments.communication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.disasterresponseapp10.databinding.FragmentTeamChatBinding

class TeamChatFragment : Fragment() {
    private var _binding: FragmentTeamChatBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTeamChatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Initialize team chat UI
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}