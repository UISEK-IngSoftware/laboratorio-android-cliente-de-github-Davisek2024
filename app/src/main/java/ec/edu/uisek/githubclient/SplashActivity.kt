package ec.edu.uisek.githubclient

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ec.edu.uisek.githubclient.services.RetrofitClient
import ec.edu.uisek.githubclient.services.SessionManager

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sessionManager = SessionManager(this)
        val credentials = sessionManager.getCredentials()

        if (credentials != null) {
            RetrofitClient.createAuthenticatedClient(credentials.first, credentials.second)
            startActivity(Intent(this, MainActivity::class.java))

        } else {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        finish()
    }
}
