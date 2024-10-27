package com.dicoding.aplikasidicodingevent.ui.activity

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import androidx.lifecycle.lifecycleScope
import com.dicoding.aplikasidicodingevent.R
import com.dicoding.aplikasidicodingevent.data.remote.ListEventsItem
import com.dicoding.aplikasidicodingevent.databinding.ActivityDetailBinding
import com.dicoding.aplikasidicodingevent.extensions.loadImage
import com.dicoding.aplikasidicodingevent.viewmodel.FavoriteViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding
    private val viewModel: FavoriteViewModel by viewModels()
    private var currentEvent: ListEventsItem? = null

    companion object {
        private const val EVENT_KEY = "event"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Mengambil data event dari intent
        currentEvent = if (Build.VERSION.SDK_INT >= 35) {
            intent.getParcelableExtra(EVENT_KEY, ListEventsItem::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(EVENT_KEY)
        }

        currentEvent?.let { event ->
            setupEventDetails(event)
            setupFavoriteButton()
            observeFavoriteStatus()
        } ?: run {
            Snackbar.make(binding.root, "Event tidak ditemukan", Snackbar.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun setupEventDetails(event: ListEventsItem) {
        with(binding) {
            tvDetailName.text = event.name
            tvDetailOwnername.text = event.ownerName
            tvDetailBegintime.text = event.beginTime
            tvDetailQuota.text = getString(R.string.quota_left, event.quota?.minus(event.registrants ?: 0))
            tvDetailDescription.text = event.description?.let {
                HtmlCompat.fromHtml(it, HtmlCompat.FROM_HTML_MODE_LEGACY)
            } ?: ""

            ivImageUpcoming.loadImage(event.imageLogo ?: event.mediaCover)

            btnDetailSign.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse(event.link)
                }
                startActivity(intent)
            }
        }
    }

    private fun setupFavoriteButton() {
        currentEvent?.let { event ->
            viewModel.checkFavoriteStatus(event.id ?: 0)

            binding.fabFavorite.setOnClickListener {
                viewModel.toggleFavorite(event)
                showFavoriteMessage(event)
            }
        }
    }

    private fun showFavoriteMessage(event: ListEventsItem) {
        val message = if (viewModel.isFavorite.value) {
            getString(R.string.remove_from_favorite)
        } else {
            getString(R.string.add_to_favorite)
        }
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
    }

    private fun observeFavoriteStatus() {
        lifecycleScope.launch {
            viewModel.isFavorite.collect { isFavorite ->
                updateFavoriteIcon(isFavorite)
            }
        }
    }

    private fun updateFavoriteIcon(isFavorite: Boolean) {
        binding.fabFavorite.setImageResource(
            if (isFavorite) R.drawable.ic_favorite
            else R.drawable.ic_favorite_border
        )
    }
}