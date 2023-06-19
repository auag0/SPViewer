package com.anago.spviewer.ui.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.topjohnwu.superuser.Shell


@SuppressLint("CustomSplashScreen")
class SplashActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Shell.getShell {
            val intent = Intent(this, AppListActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    companion object {
        init {
            Shell.setDefaultBuilder(Shell.Builder.create().setTimeout(10))
        }
    }
}