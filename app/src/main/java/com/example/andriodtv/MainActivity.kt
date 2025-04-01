package com.example.andriodtv
import App
import AppAdapter
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {
    private lateinit var exitButton: Button
    private lateinit var apps: List<App>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = GridLayoutManager(this, 4) // 3-column grid


        val apps = loadApprovedApps()
        val adapter = AppAdapter(apps) { launchApp(it.packageName) }
        recyclerView.adapter = adapter

        exitButton = findViewById(R.id.exitButton)
        exitButton.setOnClickListener {
            showPinDialog()
        }

    }
    private fun showPinDialog() {
        val pinEditText = EditText(this).apply {
            inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_PASSWORD
            hint = "Enter PIN"
        }

        AlertDialog.Builder(this)
            .setTitle("Exit Confirmation")
            .setMessage("Enter PIN to exit")
            .setView(pinEditText)
            .setPositiveButton("OK") { _, _ ->
                val enteredPin = pinEditText.text.toString()
                if (enteredPin == "1234") {  

                    
                    finishAffinity()  
                } else {
                    Toast.makeText(this, "Incorrect PIN!", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
    private fun loadApprovedApps(): List<App> {
        val packageManager = packageManager
        val apps = mutableListOf<App>()

        // Fetch apps using intent filters
        val intentLauncher = Intent(Intent.ACTION_MAIN, null).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }
        val intentLeanback = Intent(Intent.ACTION_MAIN, null).apply {
            addCategory(Intent.CATEGORY_LEANBACK_LAUNCHER)
        }

        val launchableApps = packageManager.queryIntentActivities(intentLauncher, 0)
        val leanbackApps = packageManager.queryIntentActivities(intentLeanback, 0)

        val combinedApps = (launchableApps + leanbackApps).distinctBy { it.activityInfo.packageName }

        for (resolveInfo in combinedApps) {
            val packageName = resolveInfo.activityInfo.packageName
            val appName = resolveInfo.loadLabel(packageManager).toString()
            val appIcon = resolveInfo.loadIcon(packageManager)

            apps.add(App(appName, appIcon, packageName))
        }

        // 
        val forceAddApps = listOf(
            "com.google.android.youtube.tv", // YouTube for TV
            "com.google.android.apps.tv.launcherx", // Google TV Launcher
            "com.android.vending", // Google Play Store
            "com.android.tv.settings", // TV Settings
            "com.netflix.ninja", // Netflix
            "com.amazon.firebat", // Amazon Prime Video (for some devices)
            "com.disney.disneyplus", // Disney+
            "com.spotify.tv.android", // Spotify for TV
            "com.google.android.katniss" // Google Assistant
        )

        for (packageName in forceAddApps) {
            try {
                val appInfo = packageManager.getApplicationInfo(packageName, 0)
                val appName = packageManager.getApplicationLabel(appInfo).toString()
                val appIcon = packageManager.getApplicationIcon(appInfo)

                if (apps.none { it.packageName == packageName }) {
                    apps.add(App(appName, appIcon, packageName))
                }
            } catch (e: PackageManager.NameNotFoundException) {
                // Ignore if not installed
            }
        }

        return apps
    }


    private val restrictedPackages = listOf(
        "com.android.tv.settings", // Settings app
        "com.android.vending"   // Google Play Store
    )
    private fun launchApp(packageName: String) {
        if (packageName in restrictedPackages) {
            Toast.makeText(this, "Access Denied!", Toast.LENGTH_SHORT).show()
            return
        }
        val intent = packageManager.getLaunchIntentForPackage(packageName)
        if (intent != null) {
            startActivity(intent)
        } else {
            Toast.makeText(this, "Unable to launch app", Toast.LENGTH_SHORT).show()
        }
    }
}
