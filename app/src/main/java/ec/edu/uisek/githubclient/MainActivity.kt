package ec.edu.uisek.githubclient

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import ec.edu.uisek.githubclient.databinding.ActivityMainBinding
import ec.edu.uisek.githubclient.models.Repo
import ec.edu.uisek.githubclient.services.GithubApiService
import ec.edu.uisek.githubclient.services.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var reposAdapter: ReposAdapter
    private val apiService: GithubApiService by lazy {
        RetrofitClient.gitHubApiService
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()

        binding.repoFab.setOnClickListener {
            displayRepoForm()
        }

    }

    override fun onResume() {
        super.onResume()
        fetchRepositories()
    }


    private fun setupRecyclerView() {
        reposAdapter = ReposAdapter(
            onEditClick = { repo ->
                val intent = Intent(this, RepoForm::class.java).apply {
                    putExtra("REPO_NAME", repo.name)
                    putExtra("REPO_DESCRIPTION", repo.description)
                    putExtra("REPO_OWNER", repo.owner.login)
                }
                startActivity(intent)
            },
            onDeleteClick = { repo ->
                showDeleteConfirmationDialog(repo)
            }
        )
        binding.reposRecyclerView.adapter = reposAdapter
    }

    private fun fetchRepositories() {
        val call = apiService.getRepos()

        call.enqueue(object : Callback<List<Repo>> {
            override fun onResponse(call: Call<List<Repo>?>, response: Response<List<Repo>?>) {
                if (response.isSuccessful) {
                    val repos = response.body()
                    if (repos != null && repos.isNotEmpty()) {
                        reposAdapter.updateRepositories(repos)
                        showMessage("Se cargaron los repositorios")
                    } else {
                        showMessage("No se encontraron repositorios")
                        reposAdapter.updateRepositories(emptyList()) // Clear the list
                    }
                } else {
                    val errorMessage = when (response.code()) {
                        401 -> "No autorizado"
                        403 -> "Prohibido"
                        404 -> "No encontrado"
                        else -> "Error ${response.code()}"

                    }
                    showMessage("Error $errorMessage")
                }
            }

            override fun onFailure(call: Call<List<Repo>?>, t: Throwable) {
                showMessage("No se pudieron cargar los repositorios")
            }
        })
    }

    private fun showDeleteConfirmationDialog(repo: Repo) {
        AlertDialog.Builder(this)
            .setTitle(R.string.confirm_delete_title)
            .setMessage(R.string.confirm_delete_message)
            .setPositiveButton(R.string.yes) { _, _ ->
                deleteRepository(repo)
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    private fun deleteRepository(repo: Repo) {
        val call = apiService.deleteRepo(repo.owner.login, repo.name)
        call.enqueue(object : Callback<Unit> {
            override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                if (response.isSuccessful) {
                    showMessage(getString(R.string.repo_deleted_successfully))
                    fetchRepositories() // Refresh the list
                } else {
                    showMessage("Error al eliminar el repositorio: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<Unit>, t: Throwable) {
                showMessage("Fallo al eliminar el repositorio: ${t.message}")
            }
        })
    }

    private fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    private fun displayRepoForm() {
        Intent(this, RepoForm::class.java).apply {
            startActivity(this)
        }
    }
}
