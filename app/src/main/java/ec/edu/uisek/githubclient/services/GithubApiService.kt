package ec.edu.uisek.githubclient.services

import ec.edu.uisek.githubclient.models.Repo
import retrofit2.Call
import retrofit2.http.GET

interface GithubApiService {
    // hace la llamada al api rest de github.
    @GET("user/repos")
    fun getRepos() : Call<List<Repo>>
}