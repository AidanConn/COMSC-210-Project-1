package compsci.project.bankapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var username: EditText
    private lateinit var password: EditText
    private lateinit var login: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        username = findViewById(R.id.username)
        password = findViewById(R.id.password)
        login = findViewById(R.id.login)

        login.setOnClickListener {
            val usernameStr = username.text.toString()
            val passwordStr = password.text.toString()

            if (usernameStr.isEmpty() || passwordStr.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            } else {
                // Check if username and password are correct
                if (usernameStr == "admin" && passwordStr == "admin") {
                    Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show()

                    // Store the username in SharedPreferences
                    val sharedPref = getSharedPreferences("compsci.project.bankapp.PREFERENCE_FILE_KEY", MODE_PRIVATE)
                    with(sharedPref.edit()) {
                        putString("username", usernameStr)
                        apply()
                    }

                    // Create an Intent to start MainActivity
                    val intent = Intent(this, MainActivity::class.java)

                    // Retrieve the username from SharedPreferences
                    val usernameFromSharedPref = sharedPref.getString("username", "User")

                    // Pass the username as an extra in the intent
                    intent.putExtra("username", usernameFromSharedPref)

                    startActivity(intent)
                    finish() // Optional: if you want LoginActivity to be removed from the back stack

                } else {
                    Toast.makeText(this, "Invalid username or password", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}