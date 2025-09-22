package com.example.ui

import android.app.Application
import android.graphics.Bitmap
import android.util.Log
import android.util.LruCache
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ImagesViewModel(application: Application) : AndroidViewModel(application) {
    private val cache = object : LruCache<String, Bitmap>((Runtime.getRuntime().maxMemory() / 1024 / 8).toInt()) {
        override fun sizeOf(key: String, value: Bitmap): Int = value.byteCount / 1024
    }

    fun getBitmap(key: String): Bitmap? = cache.get(key)
    fun putBitmap(key: String, bitmap: Bitmap) = cache.put(key, bitmap)

    fun loadAndCache(url: String, onComplete: (Bitmap?, Bitmap?) -> Unit) {
        viewModelScope.launch {
            try {
                val loader = ImageLoader(getApplication())
                val request = ImageRequest.Builder(getApplication())
                    .data(url)
                    .allowHardware(false)
                    .build()
                val result = withContext(Dispatchers.IO) { loader.execute(request) }
                if (result is SuccessResult) {
                    val original = result.drawable.toBitmap()
                    val blurred = withContext(Dispatchers.Default) { blurBitmap(getApplication(), original, 20f) }
                    putBitmap("${url}_orig", original)
                    putBitmap("${url}_blur", blurred)
                    onComplete(original, blurred)
                    return@launch
                }
            } catch (e: Exception) {
                Log.w("ImagesViewModel", "load fail: ${e.message}")
            }
            onComplete(null, null)
        }
    }
}