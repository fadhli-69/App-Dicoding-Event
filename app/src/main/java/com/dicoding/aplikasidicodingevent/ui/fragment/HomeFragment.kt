package com.dicoding.aplikasidicodingevent.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.aplikasidicodingevent.adapter.EventAdapter
import com.dicoding.aplikasidicodingevent.data.Resource
import com.dicoding.aplikasidicodingevent.databinding.FragmentHomeBinding
import com.dicoding.aplikasidicodingevent.extensions.setVisible
import com.dicoding.aplikasidicodingevent.ui.activity.DetailActivity
import com.dicoding.aplikasidicodingevent.viewmodel.MainViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainViewModel by viewModels()
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

        setupRecyclerViews()
        observeViewModel()
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
                                result.data?.let { activeEventAdapter.submitList(it.take(5)) }
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
                                result.data?.let { finishedEventAdapter.submitList(it.take(5)) }
                            }
                            is Resource.Error -> {
                                binding.progressBar.setVisible(false)
                                Snackbar.make(binding.root, result.message.toString(), Snackbar.LENGTH_LONG).show()
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}