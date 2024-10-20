package com.dicoding.aplikasidicodingevent.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.aplikasidicodingevent.adapter.EventAdapter
import com.dicoding.aplikasidicodingevent.databinding.FragmentFinishedBinding
import com.dicoding.aplikasidicodingevent.viewmodel.MainViewModel
import com.dicoding.aplikasidicodingevent.ui.activity.DetailActivity
import com.google.android.material.snackbar.Snackbar

class FinishedFragment : Fragment() {

    private var _binding: FragmentFinishedBinding? = null
    private val binding get() = _binding!!

    private lateinit var mainViewModel: MainViewModel
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
        setupViewModel()
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

    private fun setupViewModel() {
        mainViewModel = ViewModelProvider(this)[MainViewModel::class.java]
    }

    private fun setupSearchView() {
        binding.searchViewFinished.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { mainViewModel.setSearchQuery(it, false) }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText.isNullOrEmpty()) {
                    mainViewModel.resetSearch(false)
                } else {
                    mainViewModel.setSearchQuery(newText, false)
                }
                return true
            }
        })
    }

    private fun observeViewModel() {
        mainViewModel.finishedEvents.observe(viewLifecycleOwner) { events ->
            if (binding.searchViewFinished.query.isNullOrEmpty()) {
                eventAdapter.submitList(events)
                toggleNoResults(events.isEmpty())
            }
        }

        mainViewModel.searchResults.observe(viewLifecycleOwner) { searchResults ->
            eventAdapter.submitList(searchResults)
            toggleNoResults(searchResults.isEmpty())
        }

        mainViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        mainViewModel.errorMessage.observe(viewLifecycleOwner) { errorMsg ->
            errorMsg?.let {
                Snackbar.make(binding.root, it, Snackbar.LENGTH_LONG).show()
            }
        }
    }

    private fun toggleNoResults(isEmpty: Boolean) {
        binding.tvNoResults.visibility = if (isEmpty) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}