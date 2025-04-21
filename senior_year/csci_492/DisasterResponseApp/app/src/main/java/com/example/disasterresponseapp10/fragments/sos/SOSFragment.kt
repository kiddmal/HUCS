package com.example.disasterresponseapp10.fragments.sos

// SOSFragment.kt
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import com.example.disasterresponseapp10.R
import com.example.disasterresponseapp10.databinding.FragmentSosBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class SOSFragment : DialogFragment() {
    private var _binding: FragmentSosBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_FRAME, R.style.Theme_App_Dialog_FullScreen)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSosBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
    }

    private fun setupUI() {
        binding.apply {
            toolbar.setNavigationOnClickListener { dismiss() }

            btnSendSOS.setOnClickListener {
                // Implement SOS sending logic
                sendSOS()
            }

            chipGroupEmergencyType.setOnCheckedStateChangeListener { group, _ ->
                updateEmergencyType(group.checkedChipId)
            }
        }
    }

    private fun updateEmergencyType(checkedId: Int) {
        // Update emergency type based on selected chip
        when (checkedId) {
            R.id.chipMedical -> binding.etDescription.setText(R.string.medical_emergency)
            R.id.chipTrapped -> binding.etDescription.setText(R.string.trapped)
            R.id.chipEvacuation -> binding.etDescription.setText(R.string.need_evacuation)
        }
    }

    private fun sendSOS() {
        // Implementation for sending SOS
        lifecycleScope.launch {
            try {
                binding.progressBar.isVisible = true
                // Implement SOS sending logic
                showSuccessAndDismiss()
            } catch (e: Exception) {
                showError()
            } finally {
                binding.progressBar.isVisible = false
            }
        }
    }

    private fun showSuccessAndDismiss() {
        Snackbar.make(
            binding.root,
            R.string.sos_sent_successfully,
            Snackbar.LENGTH_SHORT
        ).show()
        dismiss()
    }

    private fun showError() {
        Snackbar.make(
            binding.root,
            R.string.error_sending_sos,
            Snackbar.LENGTH_LONG
        ).setAction(R.string.retry) {
            sendSOS()
        }.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
