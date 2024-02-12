package ktor.expos.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import ktor.expos.data.models.requests.SignInRequest
import ktor.expos.data.models.requests.SignUpRequest
import ktor.expos.data.models.responses.AuthResponse
import ktor.expos.data.models.user_models.UserData
import ktor.expos.data.models.user_models.UserDataSource
import ktor.expos.security.hashing.HashingService
import ktor.expos.security.hashing.SaltedHash
import ktor.expos.security.token.TokenClaim
import ktor.expos.security.token.TokenConfig
import ktor.expos.security.token.TokenService
import ktor.expos.services.ServiceHelpers.getUserIdFromToken
import org.apache.commons.codec.digest.DigestUtils

/*
* The route extension file to sign up users
* pass as arguments the interfaces of the related services as opposed to the implementation classes...
* ...that way, if there is a change in the database used, the required services (interface) will remain unchanged
* */

fun Route.signUp(
    hashingService: HashingService,
    userDataSource: UserDataSource
){
    post ("signup"){
        val request = call.receiveOrNull<SignUpRequest>() ?: kotlin.run {
            call.respond(HttpStatusCode.BadRequest)
            return@post
        }

        val areFieldsBlank = request.username.isBlank() || request.password.isBlank()
        val isPwTooShort = request.password.length < 8
        //you could also run other validation checks here

        if (areFieldsBlank || isPwTooShort){
            call.respond(HttpStatusCode.Conflict)
            return@post
        }
        val saltedHash = hashingService.generateSaltedHash(request.password)
        val user = UserData(
            userName = request.username,
            userPassword = saltedHash.hash,
            salt = saltedHash.salt
        )

        val wasAcknowledged = userDataSource.insertUser(user)
        if (!wasAcknowledged){
            call.respond(HttpStatusCode.Conflict)
            return@post
        }

        call.respond(HttpStatusCode.OK)
    }
}


/*
* Function to perform sign in
* */
fun Route.signIn(hashingService: HashingService,
                 userDataSource: UserDataSource, tokenService: TokenService, tokenConfig: TokenConfig
){
    post("signin"){
        val request = call.receiveOrNull<SignInRequest>() ?: kotlin.run {
            call.respond(HttpStatusCode.BadRequest)
            return@post
        }

        val user = userDataSource.getUserByUsername(request.username)
        if (user == null) {
            call.respond(HttpStatusCode.Conflict, "user does not exist")
            return@post
        }

        val isValidPassword = hashingService.verify(
            value = request.password,
            saltedHash = SaltedHash(
                hash = user.userPassword,
                salt = user.salt
            )
        )
        if (!isValidPassword){
            println("Entered hash: ${DigestUtils.sha256Hex("${user.salt}${request.password}")}, Hashed PW: ${user.userPassword}")
            call.respond(HttpStatusCode.Conflict, "incorrect username or password")
            return@post
        }

        val token = tokenService.generate(
            config = tokenConfig,
            TokenClaim(
                name = "userId",
                value = user.userId.toString()
            )
        )

        call.respond(
            status = HttpStatusCode.OK,
            message = AuthResponse(
                token = token
            )
        )

    }
}

/*
* Function to know if a user's token is still valid
* */
fun Route.authenticate(){
    //use our default authentication function to carry out the logic
    authenticate {
        get("authenticate"){
            call.respond(HttpStatusCode.OK)
        }
    }
}

/*
* Function to get the user id from the authenticated user
* */
fun Route.getSecretInfo(){
    authenticate{
        get("secret"){
            val userId = getUserIdFromToken(call)
            call.respond(HttpStatusCode.OK, "Your userId is $userId")
        }
    }
}

















