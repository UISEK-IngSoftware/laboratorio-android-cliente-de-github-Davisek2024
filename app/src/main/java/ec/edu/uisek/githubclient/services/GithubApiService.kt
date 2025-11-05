package ec.edu.uisek.githubclient.services

import ec.edu.uisek.githubclient.models.Repo
import ec.edu.uisek.githubclient.models.RepoRequest
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface GithubApiService {
    // hace la llamada al api rest de github.
    @GET("user/repos")
    fun getRepos(
        @Query(value = "sort") sort: String = "created",
        @Query(value = "direction") direction: String = "desc"
    ): Call<List<Repo>>


    @POST(value = "user/repos")
    fun addRepo(
        @Body repoRequest: RepoRequest
    ): Call<Repo>
}