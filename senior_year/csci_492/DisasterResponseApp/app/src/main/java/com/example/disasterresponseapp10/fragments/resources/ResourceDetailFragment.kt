package com.example.disasterresponseapp10.fragments.resources

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.example.disasterresponseapp10.databinding.ResourceDetailFragmentBinding
import com.example.disasterresponseapp10.models.Resource

class ResourceDetailFragment : Fragment() {
    private var _binding: ResourceDetailFragmentBinding? = null
    private val binding get() = _binding!!
    private val args: ResourceDetailFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ResourceDetailFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        displayResourceDetails(args.resource)
    }

    private fun displayResourceDetails(resource: Resource) {
        binding.apply {
            tvDetailName.text = resource.name
            tvDetailType.text = resource.type.name
            tvDetailStatus.text = resource.status.name
            tvDetailAddress.text = resource.location.address
            tvDetailCapacity.text = resource.capacity?.let {
                "Capacity: ${it.current}/${it.maximum}"
            } ?: "Capacity: N/A"
            tvDetailHours.text = "Hours: ${resource.hours}"
            tvDetailContact.text = "Contact: ${resource.contact}"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
