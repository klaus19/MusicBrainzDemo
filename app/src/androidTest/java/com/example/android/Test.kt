package com.example.android

import android.content.Context
import java.io.IOException
import java.nio.charset.StandardCharsets


fun loadJSONFromAsset(context: Context, fileName: String): String? {
    try {
        val file = context.assets.open(fileName)
        val size = file.available()
        val buffer = ByteArray(size)
        file.read(buffer)
        file.close()
        return String(buffer, StandardCharsets.UTF_8)
    } catch (ex: IOException) {
        ex.printStackTrace()
    }
    return null
}
