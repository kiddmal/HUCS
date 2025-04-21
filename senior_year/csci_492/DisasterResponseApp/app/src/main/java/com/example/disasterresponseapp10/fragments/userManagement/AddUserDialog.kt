package com.example.disasterresponseapp10.fragments.userManagement

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ArrayAdapter
import androidx.fragment.app.DialogFragment
import com.example.disasterresponseapp10.databinding.DialogAddUserBinding
import com.example.disasterresponseapp10.models.User
import com.example.disasterresponseapp10.models.UserRole
import java.util.Date

class AddUserDialog : DialogFragment() {
    private var _binding: DialogAddUserBinding? = null
    private val binding get() = _binding!!

    var onUserAdded: ((User, String) -> Unit)? = null // User and password

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, com.google.android.material.R.style.ThemeOverlay_Material3_Dialog_Alert)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogAddUserBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRoleSpinner()
        setupButtons()

        // Set dialog width to match parent with margins
        dialog?.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
    }

    private fun setupRoleSpinner() {
        val roles = UserRole.entries.toTypedArray()
        ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            roles.map { it.name }
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinnerRole.adapter = adapter
        }
    }

    private fun setupButtons() {
        binding.apply {
            btnAdd.setOnClickListener {
                if (validateInput()) {
                    val user = createUserFromInput()
                    onUserAdded?.invoke(user, etPassword.text.toString())
                    dismiss()
                }
            }

            btnCancel.setOnClickListener {
                dismiss()
            }
        }
    }

    private fun validateInput(): Boolean {
        var isValid = true
        binding.apply {
            if (etEmail.text.isNullOrBlank()) {
                etEmail.error = "Email is required"
                isValid = false
            }
            if (etPassword.text.isNullOrBlank()) {
                etPassword.error = "Password is required"
                isValid = false
            }
            if (etDisplayName.text.isNullOrBlank()) {
                etDisplayName.error = "Display name is required"
                isValid = false
            }
        }
        return isValid
    }

    private fun createUserFromInput(): User {
        return User(
            id = "", // Will be set by Firebase
            email = binding.etEmail.text.toString(),
            displayName = binding.etDisplayName.text.toString(),
            phoneNumber = binding.etPhone.text.toString().takeIf { it.isNotBlank() },
            role = UserRole.valueOf(binding.spinnerRole.selectedItem.toString()),
            createdAt = Date(),
            lastActiveAt = Date()
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}