package dev.denrick.gpacalculator

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import dev.denrick.gpacalculator.databinding.ActivityMainBinding
import dev.denrick.gpacalculator.home.HomeFragment

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportFragmentManager.beginTransaction()
            .add(R.id.fragmentContainer, HomeFragment::class.java, null).commit()
    }
}