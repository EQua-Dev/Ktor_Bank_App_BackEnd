package ktor.expos.modules.transaction.daos

import com.mongodb.client.model.Filters
import ktor.expos.modules.account.models.responses.AccountData
import ktor.expos.modules.user.models.responses.UserData
import ktor.expos.modules.bank.models.requests.BankCommission
import ktor.expos.modules.bank.models.responses.BankCommissionResponse
import ktor.expos.modules.transaction.models.requests.Transaction
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq

class MongoTransactionDataSource(db: CoroutineDatabase): TransactionDataSource {
    private val transactions = db.getCollection<Transaction>()
    override suspend fun createTransaction(transaction: Transaction): Boolean {
        return transactions.insertOne(transaction).wasAcknowledged()
    }

    override suspend fun getAllTransactionsOfUser(userId: String): List<Transaction> {
        /*val transactionsFilter = Filters.or(
            Filters.eq(Transaction::transactionFrom /AccountData::accountOwnerId / UserData::userId.name, userId),
            Filters.eq(Transaction::transactionTo / AccountData::accountOwnerId / UserData::userId.name, userId),
        )*/

        val transactionsFilter = Filters.or(
            Filters.eq("transactionFrom.accountOwnerId.userId", userId),
            Filters.eq("transactionTo.accountOwnerId.userId", userId),
        )
        val transactions = transactions.find(transactionsFilter).toList()

        return transactions
    }

}