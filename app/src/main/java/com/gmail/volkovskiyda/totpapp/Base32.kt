package com.gmail.volkovskiyda.totpapp

interface Base32 {
    fun encode(data: ByteArray): String
    fun decode(encoded: String): ByteArray
}