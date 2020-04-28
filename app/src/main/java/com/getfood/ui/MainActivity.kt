package com.getfood.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.getfood.databinding.ActivityMainBinding
import androidx.databinding.DataBindingUtil.setContentView
import com.getfood.R

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView<ActivityMainBinding>(this,
            R.layout.activity_main
        )
    }
}
