package ktor.expos.services

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.util.pipeline.*
import ktor.expos.data.models.BankAppResponseData

object ServiceHelpers {
    fun getUserIdFromToken(call: ApplicationCall): String{
        val principal = call.principal<JWTPrincipal>()
        val userId = principal?.getClaim("userId", String::class)
        return userId!!
    }

    suspend inline fun <reified T : Any> PipelineContext<Unit, ApplicationCall>.checkRequestNullability(requestBody: T): T {
        val request = call.receiveOrNull<T>() ?: kotlin.run {
            call.respond(
                HttpStatusCode.BadRequest, BankAppResponseData(
                    false,
                    HttpStatusCode.BadRequest.value,
                    HttpStatusCode.BadRequest.description,
                    "Fields cannot be empty"
                )
            )
            return@run requestBody
        }
        return request
    }

}