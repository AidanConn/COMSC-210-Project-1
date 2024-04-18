package compsci.project.bankapp.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import compsci.project.bankapp.R
import compsci.project.bankapp.databinding.FragmentHomeBinding
import android.widget.Toast
import compsci.project.bankapp.LoginActivity

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val name = arguments?.getString("name") ?: "User"
        val checkBal = arguments?.getDouble("checkBal") ?: 0.00
        val saveBal = arguments?.getDouble("saveBal") ?: 0.00
        val checkID = arguments?.getString("checkID") ?: "N/A"
        val saveID = arguments?.getString("saveID") ?: "N/A"
        val homeViewModel = ViewModelProvider(this, HomeViewModelFactory(name)).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textHome
        homeViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }

        val checkBalTextView: TextView = binding.checkingAccountBalance
        val saveBalTextView: TextView = binding.savingsAccountBalance
        val checkIDTextView: TextView = binding.checkingAccountNumber
        val saveIDTextView: TextView = binding.savingsAccountNumber
        checkBalTextView.text = "$$checkBal"
        saveBalTextView.text = "$$saveBal"
        checkIDTextView.text = "#$checkID"
        saveIDTextView.text = "#$saveID"

        // Inflate the Button
        val logoutButton: Button = root.findViewById(R.id.logoutButton)

        logoutButton.setOnClickListener {
            logout()
        }

        return root
    }

    private fun logout() {
        // Implement logout functionality here
        // For example, navigate to the login screen
        Toast.makeText(context, "Logout button clicked", Toast.LENGTH_SHORT).show()

        // Create an intent to start the login activity
        val intent = Intent(activity, LoginActivity::class.java)

        // Set the flag to clear the back stack
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

        // Start the login activity
        startActivity(intent)

        // Finish the current activity (optional)
        activity?.finish()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
