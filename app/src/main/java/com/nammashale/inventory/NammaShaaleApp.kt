package com.nammashale.inventory

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Application class annotated with @HiltAndroidApp.
 * This triggers Hilt's code generation and sets up the application-level dependency graph.
 * Must be declared in AndroidManifest.xml via android:name attribute.
 */
@HiltAndroidApp
class NammaShaaleApp : Application()
