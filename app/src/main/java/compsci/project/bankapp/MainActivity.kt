package compsci.project.bankapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import compsci.project.bankapp.databinding.ActivityMainBinding
import compsci.project.bankapp.ui.home.HomeFragment
import androidx.navigation.ui.setupWithNavController

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        // Retrieve the username from the intent
        val username = intent.getStringExtra("username")

        // Create a new instance of HomeFragment and pass the username as an argument
        val homeFragment = HomeFragment().apply {
            arguments = Bundle().apply {
                putString("username", username)
            }
        }

        // Replace the start destination with the new instance of HomeFragment
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment
        navHostFragment.childFragmentManager.beginTransaction().replace(R.id.nav_host_fragment_activity_main, homeFragment).commit()

        // Set up the BottomNavigationView with the NavController
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        navView.setupWithNavController(navController)
    }
}