package com.dicoding.aplikasidicodingevent.ui.activity

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import com.dicoding.aplikasidicodingevent.R
import com.dicoding.aplikasidicodingevent.data.ListEventsItem
import com.dicoding.aplikasidicodingevent.databinding.ActivityDetailBinding
import com.dicoding.aplikasidicodingevent.extensions.loadImage
import com.google.android.material.snackbar.Snackbar

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding

    companion object {
        private const val EVENT_KEY = "event"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Mengambil data event dari intent
        val event = if (Build.VERSION.SDK_INT >= 35) {
            intent.getParcelableExtra(EVENT_KEY, ListEventsItem::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(EVENT_KEY)
        }

        // Jika event tidak null, tampilkan datanya
        event?.let {
            // Mengisi data event ke view
            with(binding) {
                tvDetailName.text = event.name
                tvDetailOwnername.text = event.ownerName
                tvDetailBegintime.text = event.beginTime
                tvDetailQuota.text = getString(R.string.quota_left, event.quota?.minus(event.registrants ?: 0))
                tvDetailDescription.text = event.description?.let {
                    HtmlCompat.fromHtml(it, HtmlCompat.FROM_HTML_MODE_LEGACY)
                } ?: ""
            }

            // Menampilkan gambar event menggunakan Glide
            binding.ivImageUpcoming.loadImage(event.imageLogo ?: event.mediaCover)

            // Klik tombol untuk membuka link pendaftaran event
            binding.btnDetailSign.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse(event.link)
                }
                startActivity(intent)
            }

        } ?: run {
            // Menangani jika event null
            Snackbar.make(binding.root, "Event tidak ditemukan", Snackbar.LENGTH_SHORT).show()
            finish()
        }
    }
}
