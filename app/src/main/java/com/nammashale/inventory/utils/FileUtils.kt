package com.nammashale.inventory.utils

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Utility object for file operations — primarily creating URIs for CameraX capture.
 */
object FileUtils {

    /**
     * Creates a temporary image file and returns a content URI via FileProvider.
     * This URI is used as the output for CameraX ImageCapture.
     */
    fun createImageUri(context: Context): Uri {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
            .format(Date())
        val fileName = "IMG_${timeStamp}.jpg"

        val photoFile = File(
            context.cacheDir,
            fileName
        )

        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            photoFile
        )
    }

    /**
     * Saves a captured image from cache to permanent app-specific storage.
     * Returns the permanent file path.
     */
    fun moveCapturedPhotoToPermanentStorage(context: Context, tempUri: Uri): String? {
        return try {
            val photoDir = File(context.filesDir, Constants.PHOTO_DIR_NAME).also {
                if (!it.exists()) it.mkdirs()
            }
            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
                .format(Date())
            val destFile = File(photoDir, "ASSET_${timeStamp}.jpg")

            context.contentResolver.openInputStream(tempUri)?.use { input ->
                destFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }

            destFile.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Deletes a photo file given its path, used when an asset is deleted.
     */
    fun deletePhoto(filePath: String) {
        try {
            File(filePath).delete()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
