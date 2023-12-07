package com.jaresinunez.booklet.databasestuff

import android.content.ContentResolver
import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.lang.IllegalStateException

object ByteArrayHandling {

    // function to get a Bitmap from an image URI
    fun getByteArrayFromImageUri(context: Context?, imageUri: Uri): ByteArray {
        try {
            val contentResolver: ContentResolver = context?.contentResolver ?: throw  IllegalStateException("Context is null")
            val inputStream: InputStream? = contentResolver.openInputStream(imageUri)

            if (inputStream != null) {
                val bitmap: Bitmap = BitmapFactory.decodeStream(inputStream)

                val byteArrayOutputStream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
                return byteArrayOutputStream.toByteArray()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ByteArray(0)
    }

    // Function to convert a Bitmap to a ByteArray
    fun bitmapToByteArray(bitmap: Bitmap): ByteArray {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        return stream.toByteArray()
    }

    // Function to get a Bitmap from a resource ID (assuming the PNG image is in the resources)
    fun getByteArrayFromResource(context: Resources?, resourceId: Int): ByteArray {
        val options = BitmapFactory.Options()
        options.inPreferredConfig = Bitmap.Config.ARGB_8888  // Adjust based on your needs

        // Decode the resource into a Bitmap
        val bitmap = BitmapFactory.decodeResource(context, resourceId, options)

        // Convert the Bitmap to a ByteArray
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        bitmap.recycle()
        return byteArray
    }
}