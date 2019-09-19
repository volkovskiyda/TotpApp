package com.gmail.volkovskiyda.totpapp

import java.security.SecureRandom
import java.util.*
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import kotlin.math.pow

class TOTPManager(
    private val base32: Base32 = Base32String(),
    private val secureRandom: SecureRandom = SecureRandom(),
    private val numberOfDigits: Int = 6,
    private val tokenPeriodSec: Int = 30
) {
    private val algorithm = "HmacSHA1"

    fun generateKey(): String {
        val buffer = ByteArray(10)
        secureRandom.nextBytes(buffer)
        val secretKey = buffer.copyOf(10)
        return base32.encode(secretKey)
    }

    fun getTotpPassword(key: String, date: Date = Date()): String =
        getTotpPasswordInt(key, date).toString().padStart(numberOfDigits, '0')

    private fun getTotpPasswordInt(key: String, date: Date = Date()): Int =
        calculateCode(base32.decode(key.toUpperCase(Locale.US)), date.time / tokenPeriodSec / 1000)

    private fun calculateCode(key: ByteArray, tm: Long): Int {
        val data = ByteArray(8)
        var value = tm

        run {
            var i = 8
            while (i-- > 0) {
                data[i] = value.toByte()
                value = value ushr 8
            }
        }

        val signKey = SecretKeySpec(key, algorithm)

        val mac = Mac.getInstance(algorithm)
        mac.init(signKey)

        val hash = mac.doFinal(data)

        val offset = hash[hash.size - 1].toInt() and 0xF

        var truncatedHash: Long = 0

        for (i in 0..3) {
            truncatedHash = (truncatedHash shl 8) or (hash[offset + i].toInt() and 0xFF).toLong()
        }

        truncatedHash = (truncatedHash and 0x7FFFFFFF) % 10.0.pow(numberOfDigits).toLong()

        return truncatedHash.toInt()
    }
}