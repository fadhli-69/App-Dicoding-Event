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
import com.dicoding.aplikasidicodingevent.databinding.FragmentUpcomingBinding
import com.dicoding.aplikasidicodingevent.viewmodel.MainViewModel
import com.dicoding.aplikasidicodingevent.ui.activity.DetailActivity
import com.google.android.material.snackbar.Snackbar

class UpcomingFragment : Fragment() {

    private var _binding: FragmentUpcomingBinding? = null
    private val binding get() = _binding!!

    private lateinit var mainViewModel: MainViewModel
    private lateinit var eventAdapter: EventAdapter

    // Menyimpan query pencarian
    private var savedQuery: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUpcomingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupViewModel()

        // Memulihkan query pencarian dari savedInstanceState (jika ada)
        savedInstanceState?.let {
            savedQuery = it.getString("SEARCH_QUERY")
        }

        setupSearchView()
        observeViewModel()

        // Jika ada query yang disimpan, set kembali ke SearchView
        savedQuery?.let {
            binding.searchViewUpcoming.setQuery(it, false)
            mainViewModel.setSearchQuery(it, true) // Memulihkan pencarian
        }
    }

    private fun setupRecyclerView() {
        eventAdapter = EventAdapter(requireContext()) { event ->
            val intent = Intent(requireContext(), DetailActivity::class.java).apply {
                putExtra("event", event)
            }
            startActivity(intent)
        }
        binding.recycleApiUpcoming.layoutManager = LinearLayoutManager(context)
        binding.recycleApiUpcoming.adapter = eventAdapter
    }

    private fun setupViewModel() {
        mainViewModel = ViewModelProvider(this)[MainViewModel::class.java]
    }

    private fun setupSearchView() {
        binding.searchViewUpcoming.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    savedQuery = it // Simpan query pencarian
                    mainViewModel.setSearchQuery(it, true) // true untuk upcoming events
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText.isNullOrEmpty()) {
                    mainViewModel.resetSearch(true) // true untuk upcoming events
                } else {
                    savedQuery = newText // Simpan query pencarian
                    mainViewModel.setSearchQuery(newText, true) // true untuk upcoming events
                }
                return true
            }
        })
    }

    private fun observeViewModel() {
        // Mengamati query pencarian dan memperbarui SearchView
        mainViewModel.searchQuery.observe(viewLifecycleOwner) { query ->
            if (query != null && query.isNotEmpty()) {
                binding.searchViewUpcoming.setQuery(query, false) // Memulihkan query pencarian ke SearchView
            }
        }

        // Mengamati hasil pencarian
        mainViewModel.searchResults.observe(viewLifecycleOwner) { searchResults ->
            eventAdapter.submitList(searchResults)

            if (searchResults.isEmpty()) {
                binding.tvNoResults.visibility = View.VISIBLE // Tampilkan pesan "Tidak ditemukan" jika hasil kosong
            } else {
                binding.tvNoResults.visibility = View.GONE // Sembunyikan pesan "Tidak ditemukan" jika ada hasil
            }
        }

        // Mengamati event upcoming
        mainViewModel.activeEvents.observe(viewLifecycleOwner) { events ->
            eventAdapter.submitList(events)
        }

        // Mengamati loading indicator
        mainViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        // Mengamati pesan error
        mainViewModel.errorMessage.observe(viewLifecycleOwner) { errorMsg ->
            errorMsg?.let {
                Snackbar.make(binding.root, it, Snackbar.LENGTH_LONG).show()
            }
        }
    }

    // Menyimpan query pencarian saat orientasi berubah
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("SEARCH_QUERY", savedQuery)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
