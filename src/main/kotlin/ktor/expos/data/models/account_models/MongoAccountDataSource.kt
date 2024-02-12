package ktor.expos.data.models.account_models

import ktor.expos.data.models.user_models.UserData
import ktor.expos.utils.HelperFunctions.generateAccountNumber
import org.bson.types.ObjectId
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq

class MongoAccountDataSource(db: CoroutineDatabase): AccountDataSource {
    private val accounts = db.getCollection<AccountData>()
    private val users = db.getCollection<UserData>()

    override suspend fun getAccountByAccountNumber(accountNumber: String): AccountData? {
        return accounts.findOne(AccountData::accountNumber eq accountNumber)
    }

    override suspend fun insertAccount(accountData: AccountData): Boolean {
        //val userAccount = accounts.findOneById(accountData.accountId)
        //val accountExists = userAccount != null

        val userExists = users.findOneById(ObjectId(accountData.accountOwnerId)) !=null


        return if (!userExists) {
            false
        }else{
            accounts.insertOne(accountData).wasAcknowledged()
        }
    }
}