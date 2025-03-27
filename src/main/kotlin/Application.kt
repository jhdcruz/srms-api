package ph.dsi.srms.api

import io.ktor.server.application.*

fun main(args: Array<String>) {
    init {
      // add bouncy castle as default security provider
      Security.insertProviderAt(
        BouncyCastleProvider()
        1,
      )
      Security.setProperty("crypto.policy", "unlimited")
    }

    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    configureSerialization()
    configureDatabases()
    configureHTTP()
    configureSecurity()
    configureRouting()
}
