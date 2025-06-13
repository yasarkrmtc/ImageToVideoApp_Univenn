package com.imagetovideoapp.utils

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import com.apollographql.apollo.api.DefaultUpload
import com.apollographql.apollo.api.Upload
import okio.source
import java.io.File
import java.io.FileOutputStream

object FileUtils {
    fun getFileFromUri(context: Context, uri: Uri): File {
        val inputStream = context.contentResolver.openInputStream(uri)
        val tempFile = File.createTempFile(Constants.TEMP_FILE_PREFIX, Constants.TEMP_FILE_SUFFIX, context.cacheDir)
        inputStream?.use { input ->
            FileOutputStream(tempFile).use { output ->
                input.copyTo(output)
            }
        }
        return tempFile
    }

    fun createUploadFromFile(file: File): Upload {
        return DefaultUpload.Builder()
            .fileName(file.name)
            .contentLength(file.length())
            .content { sink ->
                file.source().use { source ->
                    sink.buffer.writeAll(source)
                    sink.flush()
                }
            }
            .build()
    }

    fun bitmapToFile(bitmap: Bitmap, fileName: String, context: Context): File {
        val file = File(context.cacheDir, fileName)
        val outputStream = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        outputStream.flush()
        outputStream.close()
        return file
    }

}
