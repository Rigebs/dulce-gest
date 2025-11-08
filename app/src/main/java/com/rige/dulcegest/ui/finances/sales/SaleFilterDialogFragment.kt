package com.rige.dulcegest.ui.finances.sales

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.core.util.Pair
import androidx.fragment.app.activityViewModels
import androidx.core.view.isVisible
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.datepicker.MaterialDatePicker
import com.rige.dulcegest.core.utils.ISO_DATE_FORMATTER
import com.rige.dulcegest.databinding.DialogSaleFilterBinding
import com.rige.dulcegest.domain.enums.DateRangeFilter
import dagger.hilt.android.AndroidEntryPoint
import org.threeten.bp.Instant
import org.threeten.bp.LocalDate
import org.threeten.bp.ZoneOffset
import org.threeten.bp.format.DateTimeFormatter

@AndroidEntryPoint
class SaleFilterDialogFragment : BottomSheetDialogFragment() {

    private var _binding: DialogSaleFilterBinding? = null
    private val binding get() = _binding!!

    private val saleViewModel: SaleViewModel by activityViewModels()

    private var customStartDateStr: String? = null
    private var customEndDateStr: String? = null
    private val displayFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogSaleFilterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initializeCurrentFilter()

        binding.radioGroupRange.setOnCheckedChangeListener { _, checkedId ->
            val selectedRadioButton = view.findViewById<RadioButton>(checkedId)
            val selectedTag = selectedRadioButton?.tag?.toString()

            if (selectedTag != "CUSTOM") {
                val newRange = DateRangeFilter.valueOf(selectedTag!!)
                saleViewModel.setFilterRange(newRange)
                dismiss()
            }
        }

        binding.btnCustomRange.setOnClickListener {
            showDateRangePicker()
        }
    }

    private fun initializeCurrentFilter() {
        saleViewModel.filterState.value?.let { state ->
            for (i in 0 until binding.radioGroupRange.childCount) {
                val radioButton = binding.radioGroupRange.getChildAt(i) as? RadioButton
                if (radioButton?.tag == state.selectedRange.name) {
                    radioButton.isChecked = true
                    break
                }
            }

            if (state.selectedRange == DateRangeFilter.CUSTOM && state.startDate != null && state.endDate != null) {
                customStartDateStr = state.startDate
                customEndDateStr = state.endDate
                updateCustomRangeSummary(state.startDate, state.endDate)
            }
        }
    }

    private fun showDateRangePicker() {
        val picker = MaterialDatePicker.Builder.dateRangePicker()
            .setTitleText("Seleccionar rango de fechas")
            .setSelection(
                Pair(
                    MaterialDatePicker.todayInUtcMilliseconds(),
                    MaterialDatePicker.todayInUtcMilliseconds()
                )
            )
            .build()

        picker.addOnPositiveButtonClickListener { selection ->
            val startMillis = selection.first ?: return@addOnPositiveButtonClickListener
            val endMillis = selection.second ?: return@addOnPositiveButtonClickListener

            val startLocalDate = Instant.ofEpochMilli(startMillis)
                .atOffset(ZoneOffset.UTC)
                .toLocalDate()

            val endLocalDate = Instant.ofEpochMilli(endMillis)
                .atOffset(ZoneOffset.UTC)
                .toLocalDate()

            customStartDateStr = startLocalDate.format(ISO_DATE_FORMATTER)
            customEndDateStr = endLocalDate.format(ISO_DATE_FORMATTER)

            saleViewModel.setFilterRange(DateRangeFilter.CUSTOM, customStartDateStr, customEndDateStr)

            dismiss()
        }

        picker.show(parentFragmentManager, "DATE_RANGE_PICKER")
    }

    private fun updateCustomRangeSummary(start: String, end: String) {
        try {
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            val startDateDisplay = LocalDate.parse(start, formatter).format(displayFormatter)
            val endDateDisplay = LocalDate.parse(end, formatter).format(displayFormatter)

            binding.txtCustomRangeSummary.text = "$startDateDisplay - $endDateDisplay"
            binding.txtCustomRangeSummary.isVisible = true
        } catch (e: Exception) {
            binding.txtCustomRangeSummary.isVisible = false
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}