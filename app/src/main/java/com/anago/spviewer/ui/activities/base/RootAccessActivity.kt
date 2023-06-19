package com.anago.spviewer.ui.activities.base

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.anago.spviewer.MyApp.Companion.toast
import com.anago.spviewer.root.Commands.hasRootAccess

open class RootAccessActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        checkRootAccess()
        super.onCreate(savedInstanceState)
    }

    override fun onStart() {
        checkRootAccess()
        super.onStart()
    }

    override fun onResume() {
        checkRootAccess()
        super.onResume()
    }

    private fun checkRootAccess() {
        if (!hasRootAccess()) {
            toast = Toast.makeText(this, "Root権限を許可してください。", Toast.LENGTH_LONG).also {
                it.show()
            }
            finish()
        }
    }
}