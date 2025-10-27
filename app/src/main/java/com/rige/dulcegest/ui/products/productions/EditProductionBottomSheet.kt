package com.rige.dulcegest.ui.products.productions

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.rige.dulcegest.data.local.entities.ProductionBatch
import com.rige.dulcegest.data.local.entities.relations.ProductionBatchWithProduct
import com.rige.dulcegest.databinding.BottomSheetEditProductionBinding

class EditProductionBottomSheet : BottomSheetDialogFragment() {

    private var _binding: BottomSheetEditProductionBinding? = null
    private val binding get() = _binding!!

    var onSave: ((ProductionBatch) -> Unit)? = null
    private lateinit var batch: ProductionBatchWithProduct

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetEditProductionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        with(binding) {
            etQuantity.setText(batch.batch.quantityProduced.toString())
            etTotalCost.setText(batch.batch.totalCost.toString())

            btnSave.setOnClickListener {
                val updated = batch.batch.copy(
                    quantityProduced = etQuantity.text.toString().toDoubleOrNull() ?: 0.0,
                    totalCost = etTotalCost.text.toString().toDoubleOrNull() ?: 0.0
                )
                onSave?.invoke(updated)
                dismiss()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_BATCH = "arg_batch"

        fun newInstance(batch: ProductionBatchWithProduct) =
            EditProductionBottomSheet().apply {
                arguments = Bundle().apply {
                }
            }
    }
}