package ktor.expos.data.models.account_models

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

data class AccountData(
    @BsonId val accountId: ObjectId = ObjectId(),
    val accountType: String,
    val accountOwnerId: String,
    var accountNumber: String,
    val accountBalance: String,
    val dateCreated: String
)
