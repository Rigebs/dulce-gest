package com.rige.dulcegest.ui.more

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.rige.dulcegest.R
import com.rige.dulcegest.databinding.FragmentMoreMenuBinding
import com.rige.dulcegest.ui.common.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MoreMenuFragment :
    BaseFragment<FragmentMoreMenuBinding>(FragmentMoreMenuBinding::inflate) {

    override val showToolbar: Boolean = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.cardReports.setOnClickListener {
            findNavController().navigate(R.id.action_moreMenu_to_reportsFragment)
        }

        binding.cardSuppliers.setOnClickListener {
            findNavController().navigate(R.id.action_moreMenu_to_suppliersFragment)
        }

        binding.cardSettings.setOnClickListener {
            findNavController().navigate(R.id.action_moreMenu_to_settingsFragment)
        }
    }
}
