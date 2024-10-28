package com.dicoding.aplikasidicodingevent.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.aplikasidicodingevent.R
import com.dicoding.aplikasidicodingevent.adapter.EventAdapter
import com.dicoding.aplikasidicodingevent.data.remote.ListEventsItem
import com.dicoding.aplikasidicodingevent.utils.Resource
import com.dicoding.aplikasidicodingevent.databinding.FragmentHomeBinding
import com.dicoding.aplikasidicodingevent.extensions.setVisible
import com.dicoding.aplikasidicodingevent.ui.activity.DetailActivity
import com.dicoding.aplikasidicodingevent.viewmodel.MainViewModel
import com.dicoding.aplikasidicodingevent.viewmodel.SettingViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    @Suppress("UNUSED")
    private val viewModel: MainViewModel by viewModels()

    @Suppress("UNUSED")
    private val settingViewModel: SettingViewModel by viewModels()

    private lateinit var activeEventAdapter: EventAdapter
    private lateinit var finishedEventAdapter: EventAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupMenu()
        setupRecyclerViews()
        observeViewModel()
        observeThemeSettings()
    }

    private fun setupMenu() {
        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.option_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.action_settings -> {
                        findNavController().navigate(R.id.action_home_to_setting)
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun setupRecyclerViews() {
        activeEventAdapter = EventAdapter(requireContext()) { event ->
            val intent = Intent(requireContext(), DetailActivity::class.java).apply {
                putExtra("event", event)
            }
            startActivity(intent)
        }
        binding.recyclerViewActiveEvents.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = activeEventAdapter
        }

        finishedEventAdapter = EventAdapter(requireContext()) { event ->
            val intent = Intent(requireContext(), DetailActivity::class.java).apply {
                putExtra("event", event)
            }
            startActivity(intent)
        }
        binding.recyclerViewFinishedEvents.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = finishedEventAdapter
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.activeEvents.collect { result ->
                        when(result) {
                            is Resource.Loading -> {
                                binding.progressBar.setVisible(true)
                            }
                            is Resource.Success -> {
                                binding.progressBar.setVisible(false)
                                result.data?.let {
                                    val events = it.take(5)
                                    viewModel.updateFavoriteStatuses(events)
                                    activeEventAdapter.submitList(events)
                                }
                            }
                            is Resource.Error -> {
                                binding.progressBar.setVisible(false)
                                Snackbar.make(binding.root, result.message.toString(), Snackbar.LENGTH_LONG).show()
                            }
                        }
                    }
                }

                launch {
                    viewModel.finishedEvents.collect { result ->
                        when(result) {
                            is Resource.Loading -> {
                                binding.progressBar.setVisible(true)
                            }
                            is Resource.Success -> {
                                binding.progressBar.setVisible(false)
                                result.data?.let {
                                    val events = it.take(5)
                                    viewModel.updateFavoriteStatuses(events)
                                    finishedEventAdapter.submitList(events)
                                }
                            }
                            is Resource.Error -> {
                                binding.progressBar.setVisible(false)
                                Snackbar.make(binding.root, result.message.toString(), Snackbar.LENGTH_LONG).show()
                            }
                        }
                    }
                }

                launch {
                    viewModel.favoriteStatus.collect { _ ->
                        activeEventAdapter.currentItems.forEachIndexed { index: Int, event: ListEventsItem ->
                            event.id?.let { activeEventAdapter.notifyItemChanged(index) }
                        }
                        finishedEventAdapter.currentItems.forEachIndexed { index: Int, event: ListEventsItem ->
                            event.id?.let { finishedEventAdapter.notifyItemChanged(index) }
                        }
                    }
                }
            }
        }
    }

    private fun observeThemeSettings() {
        settingViewModel.themeSettings.observe(viewLifecycleOwner) { isDarkModeActive ->
            if (isDarkModeActive) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}