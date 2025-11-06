package com.rige.dulcegest.ui.more.reports

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.rige.dulcegest.databinding.FragmentReportListBinding
import com.rige.dulcegest.ui.common.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ReportListFragment : BaseFragment<FragmentReportListBinding>(
    FragmentReportListBinding::inflate
) {
    override val toolbarTitle: String = "Reportes"
    override val showBackButton: Boolean = true

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupListeners()
    }

    private fun setupListeners() {
        binding.cardSalesReport.setOnClickListener {
            // Navegar al fragmento de Reporte de Ventas
            findNavController().navigate(
                ReportListFragmentDirections.actionReportListToSalesReportsFragment()
            )
        }

        // Agregar listeners para otras tarjetas de reportes aquí...
    }
}