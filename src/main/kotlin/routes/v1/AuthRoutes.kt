package ph.dsi.srms.routes.v1

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import ph.dsi.srms.schema.User

fun Route.authRoute() {
    val jwtSecret = environment.config.property("jwt.secret").getString()
    val jwtAudience = environment.config.property("jwt.audience").getString()
    val jwtDomain = environment.config.property("jwt.domain").getString()
    val jwtRealm = environment.config.property("jwt.realm").getString()

    route("/auth") {
        post("/login") {
            val user = call.receive<User>()
            val token = JWT.create()
                .withAudience(jwtAudience)
                .withIssuer(jwtDomain)
                .withClaim("uid", user.id)
                .withClaim("org", user.org)
                .withClaim("rol", user.roles)
                .sign(Algorithm.HMAC256(jwtSecret))
            call.respond(HttpStatusCode.OK, mapOf("token" to token))
        }

        // refresh token
        authenticate {
            post("/refresh") {
                val token = call.request.headers["Authorization"]?.removePrefix("Bearer ")
                val user = call.receive<User>()
                if (token != null) {
                    val newToken = JWT.create()
                        .withAudience(jwtAudience)
                        .withIssuer(jwtDomain)
                        .withClaim("uid", user.id)
                        .withClaim("org", user.org)
                        .withClaim("rol", user.roles)
                        .sign(Algorithm.HMAC256(jwtSecret))
                    call.respond(HttpStatusCode.OK, mapOf("token" to newToken))
                } else {
                    call.respond(HttpStatusCode.Unauthorized, "Token is missing or invalid")
                }
            }
        }
    }
}
