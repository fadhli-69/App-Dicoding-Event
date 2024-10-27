package com.dicoding.aplikasidicodingevent.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.aplikasidicodingevent.adapter.EventAdapter
import com.dicoding.aplikasidicodingevent.data.Resource
import com.dicoding.aplikasidicodingevent.databinding.FragmentFinishedBinding
import com.dicoding.aplikasidicodingevent.extensions.setVisible
import com.dicoding.aplikasidicodingevent.ui.activity.DetailActivity
import com.dicoding.aplikasidicodingevent.viewmodel.MainViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FinishedFragment : Fragment() {
    private var _binding: FragmentFinishedBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainViewModel by viewModels()
    private lateinit var eventAdapter: EventAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFinishedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupSearchView()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        eventAdapter = EventAdapter(requireContext()) { event ->
            val intent = Intent(requireContext(), DetailActivity::class.java).apply {
                putExtra("event", event)
            }
            startActivity(intent)
        }
        binding.recycleApiFinish.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = eventAdapter
        }
    }

    private fun setupSearchView() {
        binding.searchViewFinished.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    viewModel.searchEvents(it, isActive = false)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let {
                    if (it.isEmpty()) {
                        viewModel.resetSearch(isActive = false)
                    } else {
                        viewModel.searchEvents(it, isActive = false)
                    }
                }
                return true
            }
        })
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {

                viewModel.searchResults.collect { result ->
                    when(result) {
                        is Resource.Loading -> {
                            binding.progressBar.setVisible(true)
                        }
                        is Resource.Success -> {
                            binding.progressBar.setVisible(false)
                            result.data?.let { eventAdapter.submitList(it) }
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}