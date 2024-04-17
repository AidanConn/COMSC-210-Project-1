package compsci.project.bankapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.FirebaseFirestore
import compsci.project.bankapp.databinding.ActivityMainBinding
import compsci.project.bankapp.ui.home.HomeFragment
import androidx.navigation.ui.setupWithNavController
import android.util.Log

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var db: FirebaseFirestore
    private val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        // Initialize Firestore
        db = FirebaseFirestore.getInstance()

        // Retrieve the extras from the intent
        val name = intent.getStringExtra("name")
        val checkID = intent.getStringExtra("checkID")
        val saveID = intent.getStringExtra("saveID")
        val checkBal = intent.getDoubleExtra("checkBal", 0.00)
        val saveBal = intent.getDoubleExtra("saveBal", 0.00)

        // Log showing the values of the extras
        Log.d(TAG, "Name: $name")
        Log.d(TAG, "Check ID: $checkID")
        Log.d(TAG, "Save ID: $saveID")
        Log.d(TAG, "Check Bal: $checkBal")
        Log.d(TAG, "Save Bal: $saveBal")


        // Get the NavController
        val navController = findNavController(R.id.nav_host_fragment_activity_main)

        // Create a Bundle and put the data into it
        val bundle = Bundle()
        bundle.putString("name", name)
        bundle.putString("checkID", checkID)
        bundle.putString("saveID", saveID)
        bundle.putDouble("checkBal", checkBal)
        bundle.putDouble("saveBal", saveBal)

        // Pass the bundle to the HomeFragment
        navController.navigate(R.id.navigation_home, bundle)

        // Show the navigation with the NavController
        navView.setupWithNavController(navController)

    }
}