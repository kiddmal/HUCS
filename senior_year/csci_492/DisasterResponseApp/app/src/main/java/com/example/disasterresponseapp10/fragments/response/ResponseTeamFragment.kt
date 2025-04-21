package com.example.disasterresponseapp10.fragments.response

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.disasterresponseapp10.R
import com.example.disasterresponseapp10.adapters.ResponseTeamAdapter
import com.example.disasterresponseapp10.databinding.FragmentResponseTeamBinding
import com.example.disasterresponseapp10.models.ResponseTeamOption

class ResponseTeamFragment : Fragment() {
    private var _binding: FragmentResponseTeamBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentResponseTeamBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = ResponseTeamAdapter(getresponseTeamOptions()) { option ->
                handleOptionSelection(option)
            }
        }
    }

    private fun handleOptionSelection(option: ResponseTeamOption) {
        when (option.type) {
            ResponseTeamOption.Type.response_OPS -> findNavController()
                .navigate(R.id.responseOperationsFragment)
            ResponseTeamOption.Type.RESOURCE_MGMT -> findNavController()
                .navigate(R.id.unifiedResourceFragment)
            ResponseTeamOption.Type.ROUTE_PLANNING -> findNavController()
                .navigate(R.id.routeNavigationFragment)
            ResponseTeamOption.Type.TEAM_COORD -> findNavController()
                .navigate(R.id.responseOperationsFragment)
        }
    }

    private fun getresponseTeamOptions(): List<ResponseTeamOption> = listOf(
        ResponseTeamOption(
            type = ResponseTeamOption.Type.response_OPS,
            titleRes = R.string.response_operations,
            iconRes = R.drawable.ic_response,
            descriptionRes = R.string.response_operations_description
        ),
        ResponseTeamOption(
            type = ResponseTeamOption.Type.RESOURCE_MGMT,
            titleRes = R.string.resource_management,
            iconRes = R.drawable.ic_resource_management,
            descriptionRes = R.string.resource_management_description
        ),
        ResponseTeamOption(
            type = ResponseTeamOption.Type.ROUTE_PLANNING,
            titleRes = R.string.route_planning,
            iconRes = R.drawable.ic_route,
            descriptionRes = R.string.route_planning_description
        ),
        ResponseTeamOption(
            type = ResponseTeamOption.Type.TEAM_COORD,
            titleRes = R.string.team_coordination,
            iconRes = R.drawable.ic_team_coordination,
            descriptionRes = R.string.team_coordination_description
        )
    )

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}