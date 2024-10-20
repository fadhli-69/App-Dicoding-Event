package com.dicoding.aplikasidicodingevent.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.dicoding.aplikasidicodingevent.R
import com.dicoding.aplikasidicodingevent.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Mengatur warna status bar
        window.statusBarColor = ContextCompat.getColor(this, R.color.primaryColor)

        // Menggunakan ViewBinding untuk inflating layout
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Mengatur ulang warna status bar (jika berbeda dari primaryColor)
        window.statusBarColor = ContextCompat.getColor(this, R.color.teal_700)

        // Menghubungkan BottomNavigationView dengan NavController
        val navView: BottomNavigationView = binding.navView

        // Setup NavHostFragment dan NavController untuk navigasi
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment
        navController = navHostFragment.navController

        // Setup AppBarConfiguration untuk menentukan fragment utama
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_upcoming, R.id.navigation_finished
            )
        )

        // Menghubungkan NavController dengan ActionBar
        setupActionBarWithNavController(navController, appBarConfiguration)

        // Menghubungkan BottomNavigationView dengan NavController
        navView.setupWithNavController(navController)
    }

    // Mengatur navigasi ketika tombol "Up" di toolbar ditekan
    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}
