package com.example.recyclomate

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager.widget.ViewPager
import com.example.recyclomate.adapters.ViewPagerAdpater
import com.example.recyclomate.databinding.ActivityMainBinding
import com.example.recyclomate.model.MainViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        //check streak
        viewModel.checkDayStreak()
        viewModel.CleanUp()



        binding.container.adapter = ViewPagerAdpater(supportFragmentManager)

        // setting home as default
        binding.container.currentItem = 1
        binding.bottomNavigationView.menu.findItem(R.id.nav_home).isChecked = true


        binding.bottomNavigationView.setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.nav_map -> {
                    binding.container.currentItem = 0
                    return@setOnNavigationItemSelectedListener true
                }

                R.id.nav_home -> {
                    binding.container.currentItem = 1
                    return@setOnNavigationItemSelectedListener true
                }

                R.id.nav_profile -> {
                    binding.container.currentItem = 2
                    return@setOnNavigationItemSelectedListener true
                }

                else -> {
                    false
                }
            }
        }

        binding.container.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

            override fun onPageSelected(position: Int) {
                binding.bottomNavigationView.menu.getItem(position).isChecked = true
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })
    }


}