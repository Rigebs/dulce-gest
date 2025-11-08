package com.rige.dulcegest.ui.finances.sales

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.map
import com.rige.dulcegest.core.utils.DateUtils
import com.rige.dulcegest.data.local.entities.Sale
import com.rige.dulcegest.data.local.entities.SaleItem
import com.rige.dulcegest.domain.enums.DateRangeFilter
import com.rige.dulcegest.domain.models.SaleFilterState
import com.rige.dulcegest.domain.usecases.finances.sales.GetSalesByDateRangeUseCase
import com.rige.dulcegest.domain.usecases.finances.sales.GetTotalSalesForCurrentWeekUseCase
import com.rige.dulcegest.domain.usecases.finances.sales.RegisterSaleUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject

@HiltViewModel
class SaleViewModel @Inject constructor(
    private val registerSaleUseCase: RegisterSaleUseCase,
    private val getTotalSalesForCurrentWeekUseCase: GetTotalSalesForCurrentWeekUseCase,
    private val getSalesByDateRangeUseCase: GetSalesByDateRangeUseCase
) : ViewModel() {

    private val _filterState = MutableLiveData(
        SaleFilterState(DateRangeFilter.CURRENT_MONTH, null, null)
    )
    val filterState: LiveData<SaleFilterState> = _filterState

    private val _filteredSales = MediatorLiveData<List<Sale>>()
    val filteredSales: LiveData<List<Sale>> = _filteredSales

    init {
        _filteredSales.addSource(_filterState) { state ->
            loadSales(state)
        }
    }

    private fun loadSales(state: SaleFilterState) {
        val (start, end) =
            if (state.selectedRange == DateRangeFilter.CUSTOM &&
                state.startDate != null && state.endDate != null
            ) {
                state.startDate to state.endDate
            } else {
                DateUtils.getRangeStrings(state.selectedRange)
            }

        println("se filtra")
        getSalesByDateRangeUseCase.execute(start, end)
            .asLiveData()
            .observeForever { sales ->
                _filteredSales.value = sales
            }
    }

    val totalAmount: LiveData<Double> = filteredSales.map { sales ->
        sales.sumOf { it.totalAmount }
    }

    fun setFilterRange(newRange: DateRangeFilter, start: String? = null, end: String? = null) {
        val newState = SaleFilterState(newRange, start, end)
        if (_filterState.value == newState) return
        _filterState.value = newState
    }

    fun resetFilters() {
        setFilterRange(DateRangeFilter.CURRENT_MONTH)
    }

    suspend fun insertSale(sale: Sale, items: List<SaleItem>) {
        registerSaleUseCase.execute(sale, items)
    }

    fun getTotalSalesThisWeek() = getTotalSalesForCurrentWeekUseCase.execute()
}