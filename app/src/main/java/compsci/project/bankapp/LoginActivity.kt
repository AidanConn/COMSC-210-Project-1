package compsci.project.bankapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
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
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    // Declare checkingId and savingId as properties of the class
    private var checkingId: String? = null
    private var savingId: String? = null

    private val TAG = "LoginActivity"

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        username = findViewById(R.id.username)
        password = findViewById(R.id.password)
        login = findViewById(R.id.login)

        // Initialize Firebase Auth and Firestore
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        login.setOnClickListener {
            val email = username.text.toString()
            val passwordStr = password.text.toString()

            if (email.isEmpty() || passwordStr.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            } else {
                // Authenticate user with Firebase
                auth.signInWithEmailAndPassword(email, passwordStr)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            Log.d(TAG, "signInWithEmail:success")
                            Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show()

                            // Store the email in SharedPreferences
                            val sharedPref = getSharedPreferences("compsci.project.bankapp.PREFERENCE_FILE_KEY", MODE_PRIVATE)
                            with(sharedPref.edit()) {
                                putString("username", email)
                                apply()
                            }

                            // Create an Intent to start MainActivity
                            val intent = Intent(this, MainActivity::class.java)

                            // Retrieve the username from SharedPreferences
                            val usernameFromSharedPref = sharedPref.getString("username", "User") ?: "User"

                            // Pass the username as an extra in the intent
                            intent.putExtra("username", usernameFromSharedPref)

                            // Get the document reference
                            val docRef = db.collection("users").document(auth.currentUser?.uid ?: "")

                            // Run a transaction
                            db.runTransaction { transaction ->
                                val snapshot = transaction.get(docRef)

                                if (snapshot.exists()) {
                                    // If the document exists, do nothing
                                } else {
                                    // If the document does not exist, create a new document
                                    val user: MutableMap<String, Any> = HashMap()
                                    user["name"] = usernameFromSharedPref

                                    // Generate random checking and saving IDs
                                    val tempCheckingId = UUID.randomUUID().toString().substring(0, 6)
                                    val tempSavingId = UUID.randomUUID().toString().substring(0, 6)

                                    // Add the IDs to the user map
                                    user["checkingId"] = tempCheckingId
                                    user["savingId"] = tempSavingId

                                    // Create a new document for the checking account in the "accounts" collection
                                    val checkingAccount: MutableMap<String, Any> = HashMap()
                                    checkingAccount["balance"] = 0
                                    checkingAccount["userId"] = auth.currentUser?.uid ?: ""
                                    db.collection("accounts").document(tempCheckingId).set(checkingAccount)

                                    // Create a new document for the savings account in the "accounts" collection
                                    val savingsAccount: MutableMap<String, Any> = HashMap()
                                    savingsAccount["balance"] = 0
                                    savingsAccount["userId"] = auth.currentUser?.uid ?: ""
                                    db.collection("accounts").document(tempSavingId).set(savingsAccount)

                                    // Set the checkingId and savingId after successfully creating the documents
                                    checkingId = tempCheckingId
                                    savingId = tempSavingId

                                    transaction.set(docRef, user)
                                }
                            }.addOnSuccessListener {
                                Log.d(TAG, "Transaction success!")

                                // Using the users collection, retrieve name, checkingId, and savingId then using the checkingId and savingId, retrieve the balance of the checking and savings accounts
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

                                                                    // Start the MainActivity
                                                                    startActivity(intent)

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
                            }.addOnFailureListener { e ->
                                Log.w(TAG, "Transaction failure.", e)
                            }

                            // startActivity(intent)
                            finish() // Optional: if you want LoginActivity to be removed from the back stack

                        } else {
                            Log.w(TAG, "signInWithEmail:failure", task.exception)
                            Toast.makeText(this, "Invalid username or password", Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        }
    }
}