package com.digitalinclined.edugate.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream

class ImageCompressor {

    private val TAG = "ImageCompressor"

    // compress an image
    fun compressImage(
        image: Bitmap,
        compressFormat: Bitmap.CompressFormat, maxSize: Int
    ): ByteArray? {
        val baos = ByteArrayOutputStream()
        image.compress(compressFormat, 100, baos)
        var options = 100
        while (baos.toByteArray().size / 1024 > maxSize) {
            baos.reset()
            image.compress(compressFormat, options, baos)
            options -= if (options <= 10) {
                2
            } else {
                10
            }
            if (options < 1) break
        }
        return baos.toByteArray()
    }

    // convert base64 to bitmap
    fun convertBase64ToImage(stringBlobData: String): Bitmap {
        val decodedString: ByteArray = Base64.decode(stringBlobData, Base64.NO_WRAP)
        val inputStream: InputStream = ByteArrayInputStream(decodedString)
        return BitmapFactory.decodeStream(inputStream)
    }


    fun convertBase64ToScaledBitmap(blob: String, scale: Float, height: Int): Bitmap {
        val decodedString: ByteArray = Base64.decode(blob, Base64.NO_WRAP)
        val inputStream: InputStream = ByteArrayInputStream(decodedString)
        val bmap = BitmapFactory.decodeStream(inputStream)
        Log.d(TAG, "convertBase64ToScaledBitmap: compressed width:${bmap.width}")

        //ratio of original image
        val ratio = bmap.width.toDouble() / bmap.height.toDouble()
        //reduced image width
        val reducedWidth = ratio * (height * scale)

        return Bitmap.createScaledBitmap(
            bmap,
            reducedWidth.toInt(),
            (height * scale).toInt(),
            false
        )
    }


    //scale image according to a given ratio/fixed size images
    fun scaleImageToRatio(blob: String, w: Int, h: Int, scale: Float): Bitmap {
        val decodedString: ByteArray = Base64.decode(blob, Base64.NO_WRAP)
        val inputStream: InputStream = ByteArrayInputStream(decodedString)
        val bmap = BitmapFactory.decodeStream(inputStream)
        return Bitmap.createScaledBitmap(bmap, w, h, false)
    }

}