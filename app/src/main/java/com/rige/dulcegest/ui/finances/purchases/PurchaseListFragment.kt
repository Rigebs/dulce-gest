package com.rige.dulcegest.ui.finances.purchases

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.rige.dulcegest.R
import com.rige.dulcegest.databinding.FragmentPurchaseListBinding
import com.rige.dulcegest.ui.common.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PurchaseListFragment :
    BaseFragment<FragmentPurchaseListBinding>(FragmentPurchaseListBinding::inflate) {

    override val toolbarTitle = "Compras"
    override val showToolbar = true
    override val showBackButton = true

    private val viewModel: PurchaseViewModel by viewModels()
    private lateinit var adapter: PurchaseAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = PurchaseAdapter { purchase ->
            // Aquí puedes manejar el clic en una compra (por ejemplo, ir al detalle)
            // findNavController().navigate(...)
        }

        binding.recyclerPurchases.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@PurchaseListFragment.adapter
        }

        viewModel.purchases.observe(viewLifecycleOwner) { list ->
            adapter.submitList(list)

            binding.emptyStateLayout.visibility =
                if (list.isNullOrEmpty()) View.VISIBLE else View.GONE
        }

        binding.fabAddPurchase.setOnClickListener {
            findNavController().navigate(R.id.action_purchaseListFragment_to_purchaseFormFragment)
        }
    }
}
