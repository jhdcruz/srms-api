package utils

import java.security.SecureRandom
import java.security.spec.KeySpec
import javax.crypto.SecretKey
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

private const val ALGORITHM = "PBKDF2WithHmacSHA512"
private const val ITERATIONS = 120_000
private const val KEY_LENGTH = 256

/**
 * Utility class for hashing operations
 */

fun generateRandomSalt(): ByteArray {
    val random = SecureRandom.getInstance("PKCS11")
    val salt = ByteArray(16)
    random.nextBytes(salt)

    return salt
}

@OptIn(ExperimentalStdlibApi::class)
fun generateHash(secret: String, password: String, salt: String): String {
    val combinedSalt = "$salt$secret".toByteArray()
    val factory: SecretKeyFactory = SecretKeyFactory.getInstance(ALGORITHM)

    val spec: KeySpec = PBEKeySpec(password.toCharArray(), combinedSalt, ITERATIONS, KEY_LENGTH)
    val key: SecretKey = factory.generateSecret(spec)
    val hash: ByteArray = key.encoded

    return hash.toHexString()
}
