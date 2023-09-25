package com.icm2330.location

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.icm2330.location.databinding.ActivityDashboardBinding


class DashboardActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDashboardBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        val view: View = binding.getRoot()
        setContentView(view)
    }

    fun loadLocationSingleUse(view: View?) {
        val intent = Intent(this@DashboardActivity, LocationSingleUseActivity::class.java)
        startActivity(intent)
    }

    fun loadLocationAware(view: View?) {
        val intent = Intent(this@DashboardActivity, LocationAwareActivity::class.java)
        startActivity(intent)
    }
}