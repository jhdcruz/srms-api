package utils

import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.jetbrains.annotations.Nullable
import java.security.Provider
import java.security.Security
import java.security.MessageDigest
import java.math.BigInteger

private const val ALGORITHM = "PBKDF2WithHmacSHA512"
private const val ITERATIONS = 120_000
private const val KEY_LENGTH = 256
private const val SECRET = environment.config.property("jwt.secret").getString()

/**
 * Utility class for hashing operations
 */

fun generateRandomSalt(): ByteArray {
  val random = SecureRandom.getInstance("PKCS11")
  val salt = ByteArray(16)
  random.nextBytes(salt)

  return salt
}

fun generateHash(password: String, salt: String): String {
  val combinedSalt = "$salt$SECRET".toByteArray()
  val factory: SecretKeyFactory = SecretKeyFactory.getInstance(ALGORITHM)

  val spec: KeySpec = PBEKeySpec(password.toCharArray(), combinedSalt, ITERATIONS, KEY_LENGTH)
  val key: SecretKey = factory.generateSecret(spec)
  val hash: ByteArray = key.encoded

  return hash.toHexString()
}