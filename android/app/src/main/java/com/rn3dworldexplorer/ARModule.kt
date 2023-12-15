package com.rn3dworldexplorer

import android.content.Intent
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod

class ARModule(private val reactContext: ReactApplicationContext) : ReactContextBaseJavaModule(reactContext) {
    override fun getName(): String {
        return "ARModule"
    }

    @ReactMethod
    fun showAR(fileName: String) {
        val assetPath = "file:///android_asset/$fileName"
        val intent = Intent(reactContext, ModelDisplayActivity::class.java)
        intent.putExtra("MODEL_PATH", assetPath)

        // If starting the activity from a non-activity context, set this flag
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

        reactContext.startActivity(intent)
    }
}