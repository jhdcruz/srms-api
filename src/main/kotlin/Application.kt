package ph.dsi.srms

import io.ktor.server.application.*
import org.bouncycastle.jce.provider.BouncyCastleProvider
import java.security.Security

fun main(args: Array<String>) {
    // add bouncy castle as default security provider
    Security.insertProviderAt(
        BouncyCastleProvider(),
        1
    )
    Security.setProperty("crypto.policy", "unlimited")
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    configureSerialization()
    configureHTTP()
    configureSecurity()
    configureRouting()
}
