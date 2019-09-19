package com.gmail.volkovskiyda.totpapp

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val totpManager = TOTPManager()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val key = totpManager.generateKey()
        println("Key: $key")

        totp.setOnClickListener {
            val code = totpManager.getTotpPassword(key)
            val message = "Code: $code"
            println(message)
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
    }
}
