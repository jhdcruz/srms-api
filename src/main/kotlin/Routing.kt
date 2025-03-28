package ph.dsi.srms

import io.ktor.server.application.*
import io.ktor.server.plugins.requestvalidation.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import ph.dsi.srms.routes.v1.authRoute
import ph.dsi.srms.routes.v1.usersRoute

fun Application.configureRouting() {
    install(RequestValidation) {
        validate<String> { bodyText ->
            if (!bodyText.startsWith("Hello")) {
                ValidationResult.Invalid("Body text should start with 'Hello'")
            } else {
                ValidationResult.Valid
            }
        }
    }
    routing {
        get("/") {
            call.respondText("Hello World!")
        }

        route("/v1") {
            val db = configureDatabase()

            authRoute()
            usersRoute(db)
        }
    }
}
