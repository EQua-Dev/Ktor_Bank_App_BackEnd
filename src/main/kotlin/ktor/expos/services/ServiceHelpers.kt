package ktor.expos.services

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*

object ServiceHelpers {
    fun getUserIdFromToken(call: ApplicationCall): String{
        val principal = call.principal<JWTPrincipal>()
        val userId = principal?.getClaim("userId", String::class)
        return userId!!
    }
}