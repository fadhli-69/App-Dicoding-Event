package com.dicoding.aplikasidicodingevent.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.dicoding.aplikasidicodingevent.databinding.FragmentSettingBinding
import com.dicoding.aplikasidicodingevent.viewmodel.SettingViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingFragment : Fragment() {
    private var _binding: FragmentSettingBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SettingViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupThemeSetting()
        setupReminderSetting()
    }

    private fun setupThemeSetting() {
        viewModel.themeSettings.observe(viewLifecycleOwner) { isDarkModeActive ->
            binding.switchTheme.setOnCheckedChangeListener(null)
            binding.switchTheme.isChecked = isDarkModeActive

            if (isDarkModeActive) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }

            binding.switchTheme.setOnCheckedChangeListener { _, isChecked ->
                viewModel.saveThemeSetting(isChecked)
            }
        }
    }

    private fun setupReminderSetting() {
        viewModel.reminderSettings.observe(viewLifecycleOwner) { isEnabled ->

            binding.switchReminder.setOnCheckedChangeListener(null)
            binding.switchReminder.isChecked = isEnabled

            binding.switchReminder.setOnCheckedChangeListener { _, isChecked ->
                viewModel.saveReminderSetting(isChecked)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}