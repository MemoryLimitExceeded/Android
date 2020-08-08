package com.example.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.View

class SingleInstanceActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.e(toString(), "onCreate");
        setContentView(R.layout.activity_singleinstance)
    }

    fun onClick(view: View) {
        when (view.id) {
            (R.id.standard) -> {
                startActivity(Intent(this, StandardActivity::class.java))
            }
            (R.id.singletop) -> {
                startActivity(Intent(this, SingleTopActivity::class.java))
            }
            (R.id.singletask) -> {
                startActivity(Intent(this, SingleTaskActivity::class.java))
            }
            (R.id.singleinstance) -> {
                startActivity(Intent(this, SingleInstanceActivity::class.java))
            }
        }
    }

    override fun onDestroy() {
        Log.e(toString(), "onDestroy");
        super.onDestroy()
    }

    override fun onStart() {
        Log.e(toString(), "onStart");
        super.onStart()
    }

    override fun onStop() {
        Log.e(toString(), "onStop");
        super.onStop()
    }

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        Log.e(toString(), "onSaveInstanceState");
        super.onSaveInstanceState(outState, outPersistentState)
    }

    override fun onResume() {
        Log.e(toString(), "onResume");
        super.onResume()
    }

    override fun onPause() {
        Log.e(toString(), "onPause");
        super.onPause()
    }

    override fun onNewIntent(intent: Intent?) {
        Log.e(toString(), "onNewIntent");
        super.onNewIntent(intent)
    }

}