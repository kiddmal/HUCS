// Source: BaseDashboardFragment.kt

package com.example.disasterresponseapp10

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.example.disasterresponseapp10.models.DashboardCategory
import com.example.disasterresponseapp10.models.DashboardItem
import com.example.disasterresponseapp10.utils.ToolbarManager

abstract class BaseDashboardFragment<T : ViewBinding> : Fragment() {
    private var _binding: T? = null
    protected val binding get() = _binding!!

    abstract fun inflateBinding(inflater: LayoutInflater, container: ViewGroup?): T

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = inflateBinding(inflater, container)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupDashboard()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    protected abstract fun getTitle(): String
    abstract fun setupDashboard()
    abstract fun handleCategorySelection(category: DashboardCategory)
    abstract fun getDashboardItems(): List<DashboardItem>
}
