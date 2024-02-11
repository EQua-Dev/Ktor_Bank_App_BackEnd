package ktor.expos.data.models.user_models

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

data class UserData(
    @BsonId val userId: ObjectId = ObjectId(),
    val userName: String,
    val userPassword: String,
    val salt: String,
    //val userAccounts: UserBankAccounts
)
