package com.dicoding.aplikasidicodingevent.extensions

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.dicoding.aplikasidicodingevent.R

fun ImageView.loadImage(url: String?) {
    Glide.with(this.context)
        .load(url)
        .centerCrop()
        .placeholder(R.drawable.baseline_image_24) // Placeholder saat gambar dimuat
        .error(R.drawable.baseline_broken_image_24) // Gambar gagal dimuat
        .into(this)
}