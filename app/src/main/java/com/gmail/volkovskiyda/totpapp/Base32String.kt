package com.gmail.volkovskiyda.totpapp

import java.util.*
import kotlin.collections.HashMap


class Base32String(alphabet: String = "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567") : Base32 {

    private val chars: CharArray = alphabet.toCharArray()
    private val mask: Int = chars.size - 1
    private val shift: Int = Integer.numberOfTrailingZeros(chars.size)
    private val charMap: HashMap<Char, Int> = HashMap<Char, Int>().apply {
        for (i in chars.indices) this[chars[i]] = i
    }
    private val separator = "-"

    override fun encode(data: ByteArray): String {
        if (data.isEmpty()) return ""

        require(data.size < 1 shl 28)

        val outputLength = (data.size * 8 + shift - 1) / shift
        val result = StringBuilder(outputLength)

        var buffer = data[0].toInt()
        var next = 1
        var bitsLeft = 8
        while (bitsLeft > 0 || next < data.size) {
            if (bitsLeft < shift) {
                if (next < data.size) {
                    buffer = buffer shl 8
                    buffer = buffer or (data[next++].toInt() and 0xff)
                    bitsLeft += 8
                } else {
                    val pad = shift - bitsLeft
                    buffer = buffer shl pad
                    bitsLeft += pad
                }
            }
            val index = mask and (buffer shr bitsLeft - shift)
            bitsLeft -= shift
            result.append(chars[index])
        }
        return result.toString()
    }

    override fun decode(encoded: String): ByteArray {
        val encodedStr = encoded
            .trim { it <= ' ' }
            .replace(separator.toRegex(), "")
            .replace(" ".toRegex(), "")
            .replaceFirst("[=]*$".toRegex(), "")
            .toUpperCase(Locale.US)

        if (encodedStr.isEmpty()) return ByteArray(0)
        val encodedLength = encodedStr.length
        val outLength = encodedLength * shift / 8
        val result = ByteArray(outLength)
        var buffer = 0
        var next = 0
        var bitsLeft = 0
        for (c in encodedStr.toCharArray()) {
            check(charMap.containsKey(c)) { "Illegal character: $c" }

            buffer = buffer shl shift
            buffer = buffer or (charMap[c]!!.toInt() and mask)
            bitsLeft += shift
            if (bitsLeft >= 8) {
                result[next++] = (buffer shr bitsLeft - 8).toByte()
                bitsLeft -= 8
            }
        }
        return result
    }
}