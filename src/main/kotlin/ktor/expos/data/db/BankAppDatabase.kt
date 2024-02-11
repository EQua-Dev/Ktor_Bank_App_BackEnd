package ktor.expos.data.db

import ktor.expos.data.models.user_models.MongoUserDataSource
import ktor.expos.utils.Constants.DB_NAME
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo

object BankAppDatabase {

    //create a client variable to interact with teh database
    private val client = KMongo.createClient()
    //use the client to get access to the database
    val database = client.coroutine.getDatabase(DB_NAME)

    val userDataSource = MongoUserDataSource(database)
    //create variable to access the collection where the messages are stored
}