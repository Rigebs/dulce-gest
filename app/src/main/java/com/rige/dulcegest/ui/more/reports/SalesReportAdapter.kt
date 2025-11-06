package com.rige.dulcegest.ui.more.reports

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rige.dulcegest.core.utils.toSoles
import com.rige.dulcegest.databinding.ItemSalesReportLineBinding
import com.rige.dulcegest.domain.models.DailySalesSummary

class SalesReportAdapter : ListAdapter<DailySalesSummary, SalesReportAdapter.ReportViewHolder>(ReportDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReportViewHolder {
        val binding = ItemSalesReportLineBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ReportViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ReportViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ReportViewHolder(private val binding: ItemSalesReportLineBinding) :
        RecyclerView.ViewHolder(binding.root) {
        
        fun bind(summary: DailySalesSummary) {
            binding.tvPeriodLabel.text = summary.dateOrPeriodLabel
            binding.tvTotalSales.text = summary.totalSales.toSoles()
        }
    }

    object ReportDiffCallback : DiffUtil.ItemCallback<DailySalesSummary>() {
        override fun areItemsTheSame(oldItem: DailySalesSummary, newItem: DailySalesSummary): Boolean {
            return oldItem.date == newItem.date && oldItem.dateOrPeriodLabel == newItem.dateOrPeriodLabel
        }

        override fun areContentsTheSame(oldItem: DailySalesSummary, newItem: DailySalesSummary): Boolean {
            return oldItem == newItem
        }
    }
}