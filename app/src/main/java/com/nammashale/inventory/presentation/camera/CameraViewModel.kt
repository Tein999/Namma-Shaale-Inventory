package com.nammashale.inventory.presentation.camera

import android.content.Context
import android.net.Uri
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nammashale.inventory.utils.FileUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import javax.inject.Inject

data class CameraUiState(
    val isCapturing: Boolean = false,
    val capturedPhotoPath: String? = null,
    val errorMessage: String? = null,
    val hasPermission: Boolean = false
)

@HiltViewModel
class CameraViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(CameraUiState())
    val uiState: StateFlow<CameraUiState> = _uiState.asStateFlow()

    private var imageCapture: ImageCapture? = null
    private val cameraExecutor: ExecutorService = Executors.newSingleThreadExecutor()

    fun onPermissionGranted() {
        _uiState.update { it.copy(hasPermission = true) }
    }

    /**
     * Sets up the camera preview and image capture use case.
     * Called from the Composable's DisposableEffect when the screen becomes visible.
     */
    fun startCamera(
        previewView: PreviewView,
        lifecycleOwner: LifecycleOwner
    ) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }

            imageCapture = ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .build()

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    cameraSelector,
                    preview,
                    imageCapture
                )
            } catch (exc: Exception) {
                _uiState.update { it.copy(errorMessage = "Failed to start camera: ${exc.message}") }
            }
        }, ContextCompat.getMainExecutor(context))
    }

    fun capturePhoto() {
        val imageCapture = imageCapture ?: return
        val outputUri = FileUtils.createImageUri(context)

        _uiState.update { it.copy(isCapturing = true) }

        val outputOptions = ImageCapture.OutputFileOptions.Builder(
            context.contentResolver,
            outputUri,
            android.content.ContentValues()
        ).build()

        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(context),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    val savedUri = output.savedUri ?: outputUri
                    // Move from temp to permanent storage
                    val permanentPath = FileUtils.moveCapturedPhotoToPermanentStorage(context, savedUri)
                    _uiState.update {
                        it.copy(
                            isCapturing = false,
                            capturedPhotoPath = permanentPath
                        )
                    }
                }

                override fun onError(exc: ImageCaptureException) {
                    _uiState.update {
                        it.copy(
                            isCapturing = false,
                            errorMessage = "Photo capture failed: ${exc.message}"
                        )
                    }
                }
            }
        )
    }

    fun dismissError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    override fun onCleared() {
        super.onCleared()
        cameraExecutor.shutdown()
    }
}
