package com.rige.dulcegest.domain.models

import com.rige.dulcegest.domain.enums.DateRangeFilter

data class SaleFilterState(
    val selectedRange: DateRangeFilter = DateRangeFilter.CURRENT_MONTH,
    val startDate: String? = null,
    val endDate: String? = null
)