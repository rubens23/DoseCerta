package com.rubens.applembretemedicamento.presentation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.rubens.applembretemedicamento.R
import com.rubens.applembretemedicamento.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    companion object{
        lateinit var binding: ActivityMainBinding

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = (supportFragmentManager.findFragmentById(binding.fragmentContainerView.id)) as NavHostFragment
        val navController = navHostFragment.navController

        binding.bottomNavigationView.setupWithNavController(navController)

        val appBarConfiguration = AppBarConfiguration(navController.graph)
        binding.toolbar.setupWithNavController(navController, appBarConfiguration)
        binding.toolbar.visibility = View.GONE
//        binding.toolbar.setNavigationOnClickListener {
//            Toast.makeText(this, "eu cliquei pra voltar", Toast.LENGTH_SHORT).show()
//        }


    }

    override fun onResume() {
        super.onResume()
        binding.toolbar.visibility = View.GONE


    }
}