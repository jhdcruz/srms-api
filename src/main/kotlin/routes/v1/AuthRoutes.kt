fun Route.authRoute() {
    val jwtSecret = environment.config.property("jwt.secret").getString()
    val jwtAudience = environment.config.property("jwt.audience").getString()
    val jwtDomain = environment.config.property("jwt.domain").getString()
    val jwtRealm = environment.config.property("jwt.realm").getString()

  route("/auth") {
    post("/login") {
        val user = call.receive<ExposedUser>()
        val token = JWT.create()
            .withAudience(jwtAudience)
            .withIssuer(jwtDomain)
            .withClaim("uid", user.id)
            .sign(Algorithm.HMAC256("secret"))
        call.respond(HttpStatusCode.OK, mapOf("token" to token))
    }

    // refresh token
    post("/refresh") {
        val token = call.request.headers["Authorization"]?.removePrefix("Bearer ")
        if (token != null) {
            val newToken = JWT.create()
                .withAudience("audience")
                .withIssuer("issuer")
                .withClaim("name", "user.name")
                .sign(Algorithm.HMAC256("secret"))
            call.respond(HttpStatusCode.OK, mapOf("token" to newToken))
        } else {
            call.respond(HttpStatusCode.Unauthorized, "Token is missing or invalid")
        }
    }

  }
}
