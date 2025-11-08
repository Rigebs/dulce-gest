package com.rige.dulcegest.ui.common

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding

import com.rige.dulcegest.ui.MainActivity

abstract class BaseFragment<VB : ViewBinding>(
    private val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> VB
) : Fragment() {

    private var _binding: VB? = null
    protected val binding get() = _binding!!

    interface SearchableFragment {
        fun onQueryTextChange(newText: String?)
        fun onQueryTextSubmit(query: String?)
    }

    protected val mainActivity: MainActivity
        get() = requireActivity() as MainActivity

    protected fun clearSearchViewText() {
        mainActivity.clearSearchViewText()
    }

    open val showToolbar: Boolean = true
    open val toolbarTitle: String? = null
    open val showBackButton: Boolean = false

    open val showSearchView: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = bindingInflater(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val activity = requireActivity() as MainActivity

        activity.setToolbarVisible(showToolbar)

        updateToolbarState()
    }

    private fun updateToolbarState() {
        val activity = requireActivity() as MainActivity
        activity.setToolbarVisible(showToolbar)

        if (showToolbar) {
            activity.setupToolbar(toolbarTitle ?: "", showBackButton, showSearchView)        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}