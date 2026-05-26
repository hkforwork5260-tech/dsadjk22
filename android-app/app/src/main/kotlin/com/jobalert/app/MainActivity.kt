package com.jobalert.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.systemBars
import androidx.compose.ui.Modifier
import com.jobalert.app.nav.JobAlertNavHost
import com.jobalert.app.ui.theme.HiFiColors
import com.jobalert.app.ui.theme.JobAlertTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            JobAlertTheme {
                androidx.compose.foundation.layout.Box(
                    Modifier
                        .fillMaxSize()
                        .background(HiFiColors.Bg)
                        .windowInsetsPadding(WindowInsets.systemBars),
                ) {
                    JobAlertNavHost()
                }
            }
        }
    }
}
