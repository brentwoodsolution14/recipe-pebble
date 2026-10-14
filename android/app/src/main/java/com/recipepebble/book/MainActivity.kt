package com.recipepebble.book

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.recipepebble.book.core.di.ServiceLocator
import com.recipepebble.book.core.navigation.Navigator
import com.recipepebble.book.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    lateinit var navigator: Navigator
        private set

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ServiceLocator.init(applicationContext)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        navigator = Navigator(supportFragmentManager, binding.fragmentContainer.id)
        if (savedInstanceState == null) {
            navigator.showSplash()
        }
    }
}
