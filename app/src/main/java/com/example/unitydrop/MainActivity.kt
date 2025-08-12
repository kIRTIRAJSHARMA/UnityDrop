package com.example.unitydrop

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.unitydrop.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        binding.button1.setOnClickListener {
            val email = binding.signinmail.text.toString().trim()
            val password = binding.signinPassword.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                lifecycleScope.launch {
                    withContext(Dispatchers.IO) {
                        delay(500) // simulate lazy operation (e.g., loading user data)
                    }

                    firebaseAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this@MainActivity) { task ->
                            if (task.isSuccessful) {
                                // Cache credentials
                                val sharedPrefs = getSharedPreferences("UserPrefs", MODE_PRIVATE)
                                sharedPrefs.edit()
                                    .putString("email", email)
                                    .putString("password", password)
                                    .putBoolean("isUserDataSaved", true)
                                    .apply()

                                Toast.makeText(this@MainActivity, "Login Successful", Toast.LENGTH_SHORT).show()
                                startActivity(Intent(this@MainActivity, Dashboard::class.java))
                                finish()
                            } else {
                                Toast.makeText(this@MainActivity, "Login Failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
                }
            } else {
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
            }
        }

        binding.signupText.setOnClickListener {
            startActivity(Intent(this, Signup::class.java))
            finish()
        }
    }

    override fun onStart() {
        super.onStart()
        if (firebaseAuth.currentUser != null) {
            startActivity(Intent(this, MainActivity::class.java)) // cache check for logged in user
            finish()
        }
    }
}
