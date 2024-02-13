package ktor.expos.modules.user.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import ktor.expos.data.models.BankAppResponseData
import ktor.expos.modules.user.models.requests.SignInRequest
import ktor.expos.modules.user.models.requests.SignUpRequest
import ktor.expos.modules.user.models.responses.AuthResponse
import ktor.expos.modules.user.models.responses.UserData
import ktor.expos.modules.user.daos.UserDataSource
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
            call.respond(HttpStatusCode.BadRequest, BankAppResponseData(true, HttpStatusCode.BadRequest.value, HttpStatusCode.BadRequest.description, "request cannot be null"))
            return@post
        }

        val areFieldsBlank = request.username.isBlank() || request.password.isBlank()
        val isPwTooShort = request.password.length < 8
        //you could also run other validation checks here

        //change these errors to enum
        if (areFieldsBlank ){
            call.respond(HttpStatusCode.Conflict, BankAppResponseData(false, HttpStatusCode.Conflict.value, HttpStatusCode.Conflict.description, "fields cannot be blank"))
            return@post
        }
        if (isPwTooShort){
            call.respond(HttpStatusCode.Conflict, BankAppResponseData(false, HttpStatusCode.Conflict.value, HttpStatusCode.Conflict.description, "password is too short. Must be at least 8 characters"))
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
            call.respond(HttpStatusCode.Conflict, BankAppResponseData(false, HttpStatusCode.Conflict.value, HttpStatusCode.Conflict.description, "problem occurred with user creation"))
            return@post
        }

        call.respond(HttpStatusCode.OK, BankAppResponseData(true, HttpStatusCode.OK.value, HttpStatusCode.OK.description, "user created successfully"))
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
            call.respond(HttpStatusCode.BadRequest, BankAppResponseData(false, HttpStatusCode.BadRequest.value, HttpStatusCode.BadRequest.description, "request cannot be null"))
            return@post
        }

        val user = userDataSource.getUserByUsername(request.username)
        if (user == null) {
            call.respond(HttpStatusCode.Conflict, BankAppResponseData(false, HttpStatusCode.Conflict.value, HttpStatusCode.Conflict.description, "incorrect username or password"))
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
            call.respond(HttpStatusCode.Conflict, BankAppResponseData(false, HttpStatusCode.Conflict.value, HttpStatusCode.Conflict.description, "incorrect username or password"))
            return@post
        }

        val token = tokenService.generate(
            config = tokenConfig,
            TokenClaim(
                name = "userId",
                value = user.userId.toString()
            )
        )

        val tokenResponse = AuthResponse(
            token = token
        )
        call.respond(
            HttpStatusCode.OK,
            BankAppResponseData(true, HttpStatusCode.OK.value, HttpStatusCode.OK.description, tokenResponse)

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
            call.respond(HttpStatusCode.OK, BankAppResponseData(true, HttpStatusCode.OK.value, HttpStatusCode.OK.description, "valid"))
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
            call.respond(HttpStatusCode.OK, BankAppResponseData(true, HttpStatusCode.OK.value, HttpStatusCode.OK.description, "Your userId is $userId")
                )
        }
    }
}

















