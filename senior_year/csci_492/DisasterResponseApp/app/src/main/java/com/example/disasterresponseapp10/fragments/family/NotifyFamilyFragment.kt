package com.example.disasterresponseapp10.fragments.family

// NotifyFamilyFragment.kt
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.disasterresponseapp10.R
import com.example.disasterresponseapp10.databinding.FragmentNotifyFamilyBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class NotifyFamilyFragment : DialogFragment() {
    private var _binding: FragmentNotifyFamilyBinding? = null
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
        _binding = FragmentNotifyFamilyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
    }

    private fun setupUI() {
        binding.apply {
            toolbar.setNavigationOnClickListener { dismiss() }

            chipGroupStatus.setOnCheckedStateChangeListener { group, _ ->
                updateMessage(group.checkedChipId)
            }

            btnNotifyFamily.setOnClickListener {
                notifyFamily()
            }

            btnAddContacts.setOnClickListener {
                findNavController().navigate(R.id.action_notifyFamily_to_contacts)
            }
        }
    }

    private fun updateMessage(checkedChipId: Int) {
        val defaultMessage = when (checkedChipId) {
            R.id.chipSafe -> getString(R.string.safe_message_template)
            R.id.chipNeedHelp -> getString(R.string.need_help_message_template)
            else -> ""
        }
        binding.etMessage.setText(defaultMessage)
    }

    private fun notifyFamily() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                binding.progressBar.isVisible = true
                // TODO: Implement actual notification sending
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
            R.string.family_notified_successfully,
            Snackbar.LENGTH_SHORT
        ).show()
        dismiss()
    }

    private fun showError() {
        Snackbar.make(
            binding.root,
            R.string.error_notifying_family,
            Snackbar.LENGTH_LONG
        ).setAction(R.string.retry) {
            notifyFamily()
        }.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}