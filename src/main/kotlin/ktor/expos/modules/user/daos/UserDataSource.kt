package ktor.expos.modules.user.daos

import ktor.expos.modules.user.models.responses.UserData

interface UserDataSource {
    suspend fun getUserByUsername(username: String): UserData?
    suspend fun insertUser(user: UserData): Boolean
}