package ktor.expos.data.models.user_models

import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq

class MongoUserDataSource(db: CoroutineDatabase) : UserDataSource {
    //get instance of the database Users collection (table)
    private val users = db.getCollection<UserData>()
    override suspend fun getUserByUsername(username: String): UserData? {
        //get the user whose username matches the passed username
        return users.findOne(UserData::userName eq username)
    }

    override suspend fun insertUser(user: UserData): Boolean {
        //insert a new user into the database
        return users.insertOne(user).wasAcknowledged()
    }
}