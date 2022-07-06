package com.waitinglobby.waveplan.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.waitinglobby.waveplan.R
import com.waitinglobby.waveplan.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Get the navigation host fragment from this Activity
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment

        // Instantiate the navController using the NavHostFragment
        navController = navHostFragment.navController

        //Set up the bottom nav menu with the nav controller
        val bottomNavigationView = binding.bottomNav
        bottomNavigationView.setupWithNavController(navController)

        // Make sure actions in the ActionBar get propagated to the NavController
        // Set up the app bar to ensure titles are correctly listed
        // *this uses the fragment labels from nav_graph to set title*
        val appBarConfiguration = AppBarConfiguration(setOf(
            R.id.exploreFragment,
            R.id.watchlistFragment,
            R.id.beachDetailFragment,
            R.id.continentFragment,
            R.id.countryFragment,
            R.id.stateFragment,
            R.id.cityFragment
        ))
        setupActionBarWithNavController(navController, appBarConfiguration)
    }

    /**
     * Enables back button support. Simply navigates one element up on the stack.
     */
    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

}