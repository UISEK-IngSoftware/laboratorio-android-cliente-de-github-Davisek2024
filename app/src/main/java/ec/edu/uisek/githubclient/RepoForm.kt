package ec.edu.uisek.githubclient

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import ec.edu.uisek.githubclient.databinding.ActivityRepoFormBinding
import ec.edu.uisek.githubclient.models.Repo
import ec.edu.uisek.githubclient.models.RepoRequest
import ec.edu.uisek.githubclient.services.GithubApiService
import ec.edu.uisek.githubclient.services.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RepoForm : AppCompatActivity() {

    private lateinit var binding: ActivityRepoFormBinding
    private val apiService: GithubApiService by lazy {
        RetrofitClient.getApiService()
    }

    private var isEditMode = false
    private var repoName: String? = null
    private var repoOwner: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRepoFormBinding.inflate(layoutInflater)
        setContentView(binding.root)

        repoName = intent.getStringExtra("REPO_NAME")
        repoOwner = intent.getStringExtra("REPO_OWNER")
        val repoDescription = intent.getStringExtra("REPO_DESCRIPTION")

        isEditMode = repoName != null

        if (isEditMode) {
            title = "Edit Repository"
            binding.repoNameInput.setText(repoName)
            binding.repoNameInput.isEnabled = false // Disable editing of repo name
            binding.repoDescriptionInput.setText(repoDescription)
        }

        binding.cancelButton.setOnClickListener { finish() }
        binding.saveButton.setOnClickListener {
            if (isEditMode) {
                updateRepo()
            } else {
                createRepo()
            }
        }
    }

    private fun validateForm(): Boolean {
        if (binding.repoNameInput.text.toString().isBlank()) {
            binding.repoNameInput.error = "El nombre del repositorio es requerido."
            return false
        }

        if (binding.repoNameInput.text.toString().contains(" ")) {
            binding.repoNameInput.error = "El nombre del repositorio no puede contener espacios."
            return false
        }

        binding.repoNameInput.error = null
        return true
    }

    private fun createRepo() {
        if (!validateForm()) {
            return
        }
        val repoName = binding.repoNameInput.text.toString().trim()
        val repoDescription = binding.repoDescriptionInput.text.toString().trim()

        val repoRequest = RepoRequest(repoName, repoDescription)
        val call = apiService.addRepo(repoRequest)

        call.enqueue(object : Callback<Repo> {
            override fun onResponse(call: Call<Repo?>, response: Response<Repo?>) {
                if (response.isSuccessful) {
                    showMessage("Repositorio creado exitosamente.")
                    finish()
                } else {
                    val errorMessage = when (response.code()) {
                        401 -> "No autorizado"
                        403 -> "Prohibido"
                        404 -> "No encontrado"
                        else -> "Error: ${response.code()}"
                    }
                    showMessage("Error $errorMessage")
                }
            }

            override fun onFailure(call: Call<Repo?>, t: Throwable) {
                val errorMsg = "Error al crear el repositorio: ${t.message}"
                Log.d("RepoForm", errorMsg, t)
                showMessage(errorMsg)
            }
        })
    }

    private fun updateRepo() {
        val repoDescription = binding.repoDescriptionInput.text.toString().trim()
        val repoRequest = RepoRequest(repoName!!, repoDescription)

        val call = apiService.updateRepo(repoOwner!!, repoName!!, repoRequest)
        call.enqueue(object : Callback<Repo> {
            override fun onResponse(call: Call<Repo>, response: Response<Repo>) {
                if (response.isSuccessful) {
                    showMessage("Repositorio actualizado exitosamente.")
                    finish()
                } else {
                    showMessage("Error al actualizar el repositorio: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<Repo>, t: Throwable) {
                showMessage("Fallo al actualizar el repositorio: ${t.message}")
            }
        })
    }

    private fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}
