package ec.edu.uisek.githubclient.models

import kotlinx.coroutines.internal.OpDescriptor

data class Repo (
    val id: Long,
    val name: String,
    val description: String,
    val language: String?,
    val owner: RepoOwner,




)