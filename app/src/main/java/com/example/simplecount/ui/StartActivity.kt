package com.example.simplecount.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.simplecount.MainActivity
import com.example.simplecount.R

class StartActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

        Thread(Runnable {
            Thread.sleep(3000)
            val intent = Intent(this@StartActivity, MainActivity::class.java)
            startActivity((intent))
            finish()
        }).start()
    }
}
