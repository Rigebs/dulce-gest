package com.rige.dulcegest.ui.more.settings

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.content.edit
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.rige.dulcegest.databinding.FragmentSettingsBinding
import com.rige.dulcegest.ui.common.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SettingsFragment : BaseFragment<FragmentSettingsBinding>(FragmentSettingsBinding::inflate) {

    private val settingViewModel: SettingViewModel by activityViewModels()

    override val toolbarTitle: String? = "Ajustes"
    override val showBackButton: Boolean = true

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadSettings()

        binding.btnManageUnits.setOnClickListener {
            // Acción para Manage Units
        }

        binding.btnBackupData.setOnClickListener {
            Toast.makeText(requireContext(), "Funcionalidad próximamente", Toast.LENGTH_SHORT).show()
        }

        binding.btnDeleteAll.setOnClickListener { confirmDeleteAll() }
    }

    private fun loadSettings() {
        val prefs = requireContext().getSharedPreferences("app_settings", Context.MODE_PRIVATE)
        binding.inputBusinessName.setText(prefs.getString("business_name", ""))
        binding.inputCurrency.setText(prefs.getString("currency", "S/"))
        // Mejor práctica: usar un formato de cadena en lugar de .toString() directo
        binding.inputWeeklyGoal.setText(prefs.getFloat("weekly_goal", 0f).let { if (it == 0f) "" else it.toString() })
        binding.inputMonthlyGoal.setText(prefs.getFloat("monthly_goal", 0f).let { if (it == 0f) "" else it.toString() })
    }

    override fun onPause() {
        super.onPause()
        saveSettings()
    }

    private fun saveSettings() {
        val prefs = requireContext().getSharedPreferences("app_settings", Context.MODE_PRIVATE)
        prefs.edit {
            putString("business_name", binding.inputBusinessName.text.toString())
            putString("currency", binding.inputCurrency.text.toString())
            putFloat("weekly_goal", binding.inputWeeklyGoal.text.toString().toFloatOrNull() ?: 0f)
            putFloat("monthly_goal", binding.inputMonthlyGoal.text.toString().toFloatOrNull() ?: 0f)
        }
    }

    private fun confirmDeleteAll() {
        AlertDialog.Builder(requireContext())
            .setTitle("¿Eliminar todos los datos?")
            .setMessage("Esta acción eliminará todas las ventas, gastos, producciones e inventarios. No se puede deshacer.")
            .setPositiveButton("Eliminar") { _, _ -> deleteAllData() }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun deleteAllData() {
        viewLifecycleOwner.lifecycleScope.launch {
            settingViewModel.deleteAllData()
            Toast.makeText(requireContext(), "Todos los datos fueron eliminados", Toast.LENGTH_LONG).show()
        }
    }
}