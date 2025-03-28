package ph.dsi.srms.schema

import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction

@Serializable
data class User(
    val id: Int,
    val lname: String,
    val fname: String,
    val mname: String,
    val contactNo: String,
    val email: String,
    val username: String,
    val password: String,
    val org: String,
    val dept: String,
    val roles: Int,
    val designation: String,
    val kbPass: String
)

@Serializable
data class UserDisplay(
    val id: Int,
    val lname: String,
    val fname: String,
    val mname: String,
    val contactNo: String,
    val email: String,
    val username: String,
    val org: String,
    val dept: String,
    val roles: Int,
    val designation: String
)

@Serializable
data class AuthUser(val email: String, val password: String)

class UserService(database: Database) {
    object Users : Table() {
        val id = integer("user_id").autoIncrement()
        val lname = varchar("lname", length = 50)
        val fname = varchar("fname", length = 50)
        val mname = varchar("mname", length = 50)
        val contactNo = varchar("contact_no", length = 11)
        val email = varchar("email_no", length = 50)
        val username = varchar("username", length = 20)
        val password = varchar("password", length = 20)
        val org = varchar("org", length = 50)
        val dept = varchar("dept", length = 10)
        val roles = integer("roles")
        val designation = varchar("designation", length = 25)
        val kbPass = varchar("kb_password", length = 50)

        override val primaryKey = PrimaryKey(id)
    }

    init {
        transaction(database) {
            SchemaUtils.create(Users)
        }
    }

    suspend fun create(user: User): Int = dbQuery {
        Users.insert {
            it[id] = user.id
            it[lname] = user.lname
            it[fname] = user.fname
            it[mname] = user.mname
            it[contactNo] = user.contactNo
            it[email] = user.email
            it[username] = user.username
            it[password] = user.password
            it[org] = user.org
            it[dept] = user.dept
            it[roles] = user.roles
            it[designation] = user.designation
            it[kbPass] = user.kbPass
        }[Users.id]
    }

    suspend fun readAll(): List<UserDisplay> {
        return dbQuery {
            Users.select(
                Users.id,
                Users.lname,
                Users.fname,
                Users.mname,
                Users.contactNo,
                Users.email,
                Users.username,
                Users.org,
                Users.dept,
                Users.roles,
                Users.designation
            )
                .map {
                    UserDisplay(
                        id = it[Users.id],
                        lname = it[Users.lname],
                        fname = it[Users.fname],
                        mname = it[Users.mname],
                        contactNo = it[Users.contactNo],
                        email = it[Users.email],
                        username = it[Users.username],
                        org = it[Users.org],
                        dept = it[Users.dept],
                        roles = it[Users.roles],
                        designation = it[Users.designation]
                    )
                }
        }
    }

    suspend fun read(id: Int): UserDisplay? {
        return dbQuery {
            Users.select(
                Users.id,
                Users.lname,
                Users.fname,
                Users.mname,
                Users.contactNo,
                Users.email,
                Users.username,
                Users.org,
                Users.dept,
                Users.roles,
                Users.designation
            )
                .where { Users.id eq id }
                .map {
                    UserDisplay(
                        id = it[Users.id],
                        lname = it[Users.lname],
                        fname = it[Users.fname],
                        mname = it[Users.mname],
                        contactNo = it[Users.contactNo],
                        email = it[Users.email],
                        username = it[Users.username],
                        org = it[Users.org],
                        dept = it[Users.dept],
                        roles = it[Users.roles],
                        designation = it[Users.designation]
                    )
                }
                .singleOrNull()
        }
    }

    suspend fun update(id: Int, user: User) {
        dbQuery {
            Users.update({ Users.id eq id }) {
                it[lname] = user.lname
                it[fname] = user.fname
                it[mname] = user.mname
                it[contactNo] = user.contactNo
                it[email] = user.email
                it[username] = user.username
                it[org] = user.org
                it[dept] = user.dept
                it[roles] = user.roles
                it[designation] = user.designation
                it[kbPass] = user.kbPass
            }
        }
    }

    suspend fun delete(id: Int) {
        dbQuery {
            Users.deleteWhere { Users.id.eq(id) }
        }
    }

    private suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }
}
