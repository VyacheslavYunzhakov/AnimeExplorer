package com.example.ui

import android.content.Context
import android.graphics.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import android.renderscript.*
import androidx.core.graphics.createBitmap

@Suppress("DEPRECATION")
suspend fun blurBitmap(context: Context, bitmap: Bitmap, radius: Float): Bitmap {
    return withContext(Dispatchers.Default) {
        val rs = RenderScript.create(context)
        val input = Allocation.createFromBitmap(rs, bitmap, Allocation.MipmapControl.MIPMAP_NONE, Allocation.USAGE_SCRIPT)
        val output = Allocation.createTyped(rs, input.type)
        val script = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs))
        val r = radius.coerceIn(0f, 25f)
        script.setRadius(r)
        script.setInput(input)
        script.forEach(output)
        val outBitmap = createBitmap(bitmap.width, bitmap.height)
        output.copyTo(outBitmap)
        rs.destroy()
        outBitmap
    }
}