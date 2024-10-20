package com.dicoding.aplikasidicodingevent.ui.activity

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import com.bumptech.glide.Glide
import com.dicoding.aplikasidicodingevent.R
import com.dicoding.aplikasidicodingevent.data.ListEventsItem
import com.dicoding.aplikasidicodingevent.databinding.ActivityDetailBinding
import com.google.android.material.snackbar.Snackbar

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Mengambil data event dari intent
        val event = if (Build.VERSION.SDK_INT >= 35) {
            intent.getParcelableExtra("event", ListEventsItem::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra("event")
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
            val imageView: ImageView = binding.ivImageUpcoming
            Glide.with(this).load(event.imageLogo ?: event.mediaCover).into(imageView)

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