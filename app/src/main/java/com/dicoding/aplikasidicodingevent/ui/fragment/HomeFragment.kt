package com.dicoding.aplikasidicodingevent.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.aplikasidicodingevent.data.ListEventsItem
import com.dicoding.aplikasidicodingevent.databinding.FragmentHomeBinding
import com.dicoding.aplikasidicodingevent.adapter.EventAdapter
import com.dicoding.aplikasidicodingevent.viewmodel.MainViewModel
import com.dicoding.aplikasidicodingevent.ui.activity.DetailActivity
import com.google.android.material.snackbar.Snackbar

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var mainViewModel: MainViewModel
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

        mainViewModel = ViewModelProvider(this)[MainViewModel::class.java]

        setupRecyclerViews()

        // Observe event yang sedang berlangsung (upcoming)
        mainViewModel.activeEvents.observe(viewLifecycleOwner) { events ->
            val limitedActiveEvents = events.take(5) // Batasi event hanya 5
            activeEventAdapter.submitList(limitedActiveEvents)
        }

        // Observe event yang sudah selesai (finished)
        mainViewModel.finishedEvents.observe(viewLifecycleOwner) { events ->
            val limitedFinishedEvents = events.take(5) // Batasi event hanya 5
            finishedEventAdapter.submitList(limitedFinishedEvents)
        }

        // Observe state loading untuk menampilkan loading indicator
        mainViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            showLoading(isLoading)
        }

        // Observe pesan error jika ada masalah dalam pengambilan data
        mainViewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
            errorMessage?.let {
                Snackbar.make(binding.root, it, Snackbar.LENGTH_LONG).show()
            }
        }

        mainViewModel.fetchEvents() // Mengambil event
    }

    private fun setupRecyclerViews() {
        // Inisialisasi adapter untuk event aktif (horizontal)
        activeEventAdapter = EventAdapter(requireContext()) { event ->
            openDetailActivity(event)
        }
        binding.recyclerViewActiveEvents.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerViewActiveEvents.adapter = activeEventAdapter

        // Inisialisasi adapter untuk event selesai (vertikal)
        finishedEventAdapter = EventAdapter(requireContext()) { event ->
            openDetailActivity(event)
        }
        binding.recyclerViewFinishedEvents.layoutManager = LinearLayoutManager(context)
        binding.recyclerViewFinishedEvents.adapter = finishedEventAdapter
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun openDetailActivity(event: ListEventsItem) {
        val intent = Intent(requireContext(), DetailActivity::class.java).apply {
            putExtra("event", event)
        }
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
