package ec.edu.uisek.githubclient

import android.content.Intent
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import ec.edu.uisek.githubclient.databinding.ActivityLoginBinding
import ec.edu.uisek.githubclient.services.RetrofitClient
import ec.edu.uisek.githubclient.services.SessionManager

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sessionManager = SessionManager(this)

        if (sessionManager.isLoggedIn()) {
            val credentials = sessionManager.getCredentials()
            if (credentials != null) {
                RetrofitClient.createAuthenticatedClient(credentials.first, credentials.second)
                navigateToMain()
                return // Skip the rest of onCreate
            }
        }

        binding = ActivityLoginBinding.inflate(layoutInflater)
        binding.loginButton.setOnClickListener { loginUser() }
        setContentView(binding.root)

    }

    private fun loginUser() {
        val username = binding.userInput.text.toString()
        val password = binding.passwordInput.text.toString()

        if (username.isNotEmpty() && password.isNotEmpty()) {
            RetrofitClient.createAuthenticatedClient(username, password)
            sessionManager.saveCredentials(username, password)
            navigateToMain()
        }

    }

    private fun navigateToMain(){
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }
}