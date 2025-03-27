import io.ktor.server.application.*
import ph.dsi.srms.configureDatabases
import java.security.Security
import ph.dsi.srms.configureRouting
import ph.dsi.srms.configureSecurity
import ph.dsi.srms.configureSerialization
import org.bouncycastle.jce.provider.BouncyCastleProvider

fun main(args: Array<String>) {
    // add bouncy castle as default security provider
    Security.insertProviderAt(
        BouncyCastleProvider(),
        1,
    )
    Security.setProperty("crypto.policy", "unlimited")

    io.ktor.server.netty.EngineMain.main(args)
}


fun Application.module() {
    configureSerialization()
    configureDatabases()
    configureHTTP()
    configureSecurity()
    configureRouting()
}
