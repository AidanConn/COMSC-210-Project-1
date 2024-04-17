package compsci.project.bankapp.ui.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import compsci.project.bankapp.R
import compsci.project.bankapp.databinding.FragmentHomeBinding
import android.widget.Toast

// Rest of your code...

class   HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
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

        // Find the TextViews for checkBal, saveBal, checkID, and saveID by their IDs and set their text
        val checkBalTextView: TextView = binding.checkingAccountBalance
        val saveBalTextView: TextView = binding.savingsAccountBalance
        val checkIDTextView: TextView = binding.checkingAccountNumber
        val saveIDTextView: TextView = binding.savingsAccountNumber
        checkBalTextView.text = "$$checkBal"
        saveBalTextView.text = "$$saveBal"
        checkIDTextView.text = "#$checkID"
        saveIDTextView.text = "#$saveID"

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Find the CardViews by their IDs
        val checkingAccountCard: CardView = view.findViewById(R.id.checking_account_card)
        val savingsAccountCard: CardView = view.findViewById(R.id.savings_account_card)

        // Set onClickListeners to the CardViews
        checkingAccountCard.setOnClickListener {
            // Handle the click event for the checking account card
            // Show a toast message
            Toast.makeText(context, "Checking Account Clicked", Toast.LENGTH_SHORT).show()
        }

        savingsAccountCard.setOnClickListener {
            // Handle the click event for the savings account card
            // Show a toast message
            Toast.makeText(context, "Savings Account Clicked", Toast.LENGTH_SHORT).show()
        }
    }
}