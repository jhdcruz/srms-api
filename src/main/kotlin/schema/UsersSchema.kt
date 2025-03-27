package schema

import java.time.LocalDateTime
import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.javatime.datetime
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction

@Serializable
data class User(
    val id: Int,
    val name: String,
    val email: String,
    val password: String,
    val role: String,
    val organization: String,
    val createdAt: LocalDateTime,
    val updatedAt: String,
)

@Serializable
data class AuthUser(val email: String, val password: String)

class UserService(database: Database) {
    object Users : Table() {
        val id = integer("id").autoIncrement()
        val name = varchar("name", length = 100)
        val email = varchar("email", length = 60)
        val password = varchar("password", length = 200)
        val role = varchar("role", length = 20)
        val organization = varchar("organization", length = 100)
        val createdAt = datetime("created_at")
        val updatedAt = datetime("updated_at")

        override val primaryKey = PrimaryKey(id)
    }

    init {
        transaction(database) {
            SchemaUtils.create(Users)
        }
    }

    suspend fun create(user: User): Int = dbQuery {
        Users.insert {
            it[name] = user.name
            it[email] = user.email
            it[password] = user.password
            it[role] = user.role
            it[organization] = user.organization
            it[createdAt] = user.createdAt
            it[updatedAt] = user.updatedAt
        }[Users.id]
    }

    suspend fun read(id: Int): User? {
        return dbQuery {
            Users.selectAll()
                .where { Users.id eq id }
                .map {
                    User(
                        id = it[Users.id],
                        name = it[Users.name],
                        email = it[Users.email],
                        role = it[Users.role],
                        organization = it[Users.organization],
                        createdAt = it[Users.createdAt].toString(),
                        updatedAt = it[Users.updatedAt].toString()
                    )
                }
                .singleOrNull()
        }
    }

    suspend fun update(id: Int, user: User) {
        dbQuery {
            Users.update({ Users.id eq id }) {
                it[name] = user.name
                it[age] = user.age
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
