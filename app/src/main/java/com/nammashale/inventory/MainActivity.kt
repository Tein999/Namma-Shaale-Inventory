package com.nammashale.inventory

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.rememberNavController
import com.nammashale.inventory.presentation.navigation.NavGraph
import com.nammashale.inventory.presentation.theme.NammaShaaleTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * Single-Activity architecture entry point.
 * - Installs the splash screen
 * - Sets up edge-to-edge layout
 * - Provides the NavController to the NavGraph
 * - Manages the "capturedPhotoPath" state between Camera → AddAsset screens
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        // Must be called before super.onCreate() for the splash screen to work
        installSplashScreen()

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            NammaShaaleTheme {
                val navController = rememberNavController()

                // State shared between Camera and AddAsset screens
                // Camera writes the path here, AddAsset reads it
                var capturedPhotoPath by remember { mutableStateOf<String?>(null) }

                // Observe back-stack state for photo from Camera
                val currentBackStack = navController.currentBackStackEntry
                val savedPhoto = currentBackStack?.savedStateHandle?.get<String>("capturedPhotoPath")
                if (savedPhoto != null && capturedPhotoPath != savedPhoto) {
                    capturedPhotoPath = savedPhoto
                }

                Surface(modifier = Modifier.fillMaxSize()) {
                    NavGraph(
                        navController = navController,
                        capturedPhotoPath = capturedPhotoPath,
                        onPhotoPathConsumed = { capturedPhotoPath = null }
                    )
                }
            }
        }
    }
}
