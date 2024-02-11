package ktor.expos.data.models.user_models

interface UserDataSource {
    suspend fun getUserByUsername(username: String): UserData?
    suspend fun insertUser(user: UserData): Boolean
}