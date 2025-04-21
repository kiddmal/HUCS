package com.example.disasterresponseapp10.fragments.resources


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.DialogFragment
import com.example.disasterresponseapp10.databinding.DialogAddResourceBinding
import com.example.disasterresponseapp10.models.Resource
import java.util.UUID

class AddResourceDialog : DialogFragment() {
    private var _binding: DialogAddResourceBinding? = null
    private val binding get() = _binding!!

    var onResourceAdded: ((Resource) -> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogAddResourceBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupSpinners()
        setupButtons()
    }

    private fun setupSpinners() {
        val resourceTypes = Resource.ResourceType.entries
        val statusTypes = Resource.Status.entries

        ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            resourceTypes.map { it.name }
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinnerType.adapter = adapter
        }

        ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            statusTypes.map { it.name }
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinnerStatus.adapter = adapter
        }
    }

    private fun setupButtons() {
        binding.btnAdd.setOnClickListener {
            val resource = createResourceFromInput()
            onResourceAdded?.invoke(resource)
            dismiss()
        }

        binding.btnCancel.setOnClickListener {
            dismiss()
        }
    }

    private fun createResourceFromInput(): Resource {
        return Resource(
            id = UUID.randomUUID().toString(),
            name = binding.etName.text?.toString() ?: "",
            type = Resource.ResourceType.valueOf(
                binding.spinnerType.selectedItem.toString()
            ),
            location = Resource.Location(
                address = binding.etAddress.text?.toString() ?: "",
                latitude = binding.etLatitude.text?.toString()?.toDoubleOrNull() ?: 0.0,
                longitude = binding.etLongitude.text?.toString()?.toDoubleOrNull() ?: 0.0
            ),
            status = Resource.Status.valueOf(
                binding.spinnerStatus.selectedItem.toString()
            ),
            capacity = if (!binding.etCapacityCurrent.text.isNullOrEmpty() &&
                !binding.etCapacityMax.text.isNullOrEmpty()) {
                Resource.Capacity(
                    current = binding.etCapacityCurrent.text.toString().toInt(),
                    maximum = binding.etCapacityMax.text.toString().toInt()
                )
            } else null,
            hours = binding.etHours.text?.toString() ?: "",
            distance = 0f,
            contact = binding.etContact.text?.toString() ?: ""
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}