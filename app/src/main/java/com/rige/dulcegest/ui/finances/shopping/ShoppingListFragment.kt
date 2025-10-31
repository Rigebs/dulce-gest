package com.rige.dulcegest.ui.finances.shopping

import com.rige.dulcegest.R
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.rige.dulcegest.databinding.FragmentShoppingListBinding
import com.rige.dulcegest.ui.common.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ShoppingListFragment :
    BaseFragment<FragmentShoppingListBinding>(FragmentShoppingListBinding::inflate) {

    override val toolbarTitle = "Lista de compras"
    override val showToolbar = true
    override val showBackButton = true

    private val viewModel: ShoppingListViewModel by viewModels()
    private lateinit var adapter: ShoppingListAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = ShoppingListAdapter(
            onItemClick = { itemWithSupply ->

                val action = ShoppingListFragmentDirections
                    .actionShoppingListFragmentToPurchaseFormFragment(itemWithSupply.item.supplyId)

                findNavController().navigate(action)
            },
            onDelete = { itemWithSupply ->
                viewModel.deleteItem(itemWithSupply.item)
            }
        )

        binding.recyclerShoppingList.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@ShoppingListFragment.adapter
        }

        viewModel.items.observe(viewLifecycleOwner) { items ->
            adapter.submitList(items)
        }

        binding.fabAddShoppingItem.setOnClickListener {
            findNavController().navigate(R.id.action_shoppingListFragment_to_addShoppingItemDialog)
        }
    }
}