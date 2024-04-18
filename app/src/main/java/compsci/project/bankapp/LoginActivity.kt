package compsci.project.bankapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.UUID

class LoginActivity : AppCompatActivity() {

    private lateinit var username: EditText
    private lateinit var password: EditText
    private lateinit var login: Button
    private lateinit var rememberMe: CheckBox
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var forgotPassword: Button


    private val TAG = "LoginActivity"

    override fun onStart() {
        super.onStart()

        // Load the saved username, password and rememberMe status from SharedPreferences
        val sharedPref = getSharedPreferences("compsci.project.bankapp.PREFERENCE_FILE_KEY", MODE_PRIVATE)
        val savedUsername = sharedPref.getString("username", "")
        val savedPassword = sharedPref.getString("password", "")
        val rememberMeStatus = sharedPref.getBoolean("rememberMe", false)

        // Set the username and password EditText fields if "Remember Me" is checked and there are saved credentials
        if (rememberMeStatus && savedUsername != null && savedPassword != null) {
            if (savedUsername.isNotBlank() && savedPassword.isNotBlank()) {
                username.setText(savedUsername)
                password.setText(savedPassword)
            }
        }
        rememberMe.isChecked = rememberMeStatus
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        username = findViewById(R.id.username)
        password = findViewById(R.id.password)
        login = findViewById(R.id.login)
        rememberMe = findViewById(R.id.rememberMe)
        forgotPassword = findViewById(R.id.forgotPassword)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        login.setOnClickListener {
            val email = username.text.toString()
            val passwordStr = password.text.toString()

            if (email.isEmpty() || passwordStr.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            } else {
                auth.signInWithEmailAndPassword(email, passwordStr)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            Log.d(TAG, "signInWithEmail:success")
                            Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show()

                            // Save the username, password and rememberMe status in SharedPreferences
                            val sharedPref = getSharedPreferences("compsci.project.bankapp.PREFERENCE_FILE_KEY", MODE_PRIVATE)
                            with(sharedPref.edit()) {
                                putString("username", email)
                                putString("password", passwordStr)
                                putBoolean("rememberMe", rememberMe.isChecked)
                                apply()
                            }

                            // Retrieve the user's details
                            retrieveUserDetails(email)

                        } else {
                            Log.w(TAG, "signInWithEmail:failure", task.exception)
                            Toast.makeText(this, "Invalid username or password", Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        }

        forgotPassword.setOnClickListener {
            val email = username.text.toString()

            if (email.isEmpty()) {
                Toast.makeText(this, "Please enter your email address", Toast.LENGTH_SHORT).show()
            } else {
                auth.sendPasswordResetEmail(email)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Log.d(TAG, "Email sent.")
                            Toast.makeText(this, "Password reset email sent", Toast.LENGTH_SHORT).show()
                        } else {
                            Log.w(TAG, "Failed to send password reset email.", task.exception)
                            Toast.makeText(this, "Failed to send password reset email", Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        }



    }

    private fun retrieveUserDetails(email: String) {
        val intent = Intent(this, MainActivity::class.java)

        // Get the user's details from Firestore

        db.collection("users").document(auth.currentUser?.uid ?: "").get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val name = document.getString("name")
                    val checkID = document.getString("checkingId")
                    val saveID = document.getString("savingId")

                    db.collection("accounts").document(checkID ?: "").get()
                        .addOnSuccessListener { document ->
                            if (document != null) {
                                val checkBal = document.getDouble("balance")

                                db.collection("accounts").document(saveID ?: "").get()
                                    .addOnSuccessListener { document ->
                                        if (document != null) {
                                            val saveBal = document.getDouble("balance")

                                            // Pass the name, checkID, saveID, checkBal, and saveBal as extras in the intent
                                            intent.putExtra("name", name)
                                            intent.putExtra("checkID", checkID)
                                            intent.putExtra("saveID", saveID)
                                            intent.putExtra("checkBal", checkBal ?: 0.02)
                                            intent.putExtra("saveBal", saveBal ?: 0.02)

                                            // Log with all the data
                                            Log.d(TAG, "name: $name, checkID: $checkID, saveID: $saveID, checkBal: $checkBal, saveBal: $saveBal")

                                            Log.d(TAG, "Before starting MainActivity")
                                            // Start the MainActivity
                                            startActivity(intent)
                                            Log.d(TAG, "After starting MainActivity")

                                            finish() // Optional: if you want LoginActivity to be removed from the back stack
                                        } else {
                                            Log.d(TAG, "No such document")
                                        }
                                    }
                            } else {
                                Log.d(TAG, "No such document")
                            }
                        }
                } else {
                    Log.d(TAG, "No such document")
                }
            }
    }
}