package com.shouman.apps.reseller.admin.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.widget.Toast
import androidx.exifinterface.media.ExifInterface
import com.shouman.apps.reseller.admin.R
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.*

class BitmapUtils {
    companion object {
        fun resamplePic(targetW: Int, targetH: Int, imagePath: String): Bitmap? {

            // Get the dimensions of the original bitmap
            val bmOptions = BitmapFactory.Options()
            bmOptions.inJustDecodeBounds = true
            BitmapFactory.decodeFile(imagePath, bmOptions)
            val photoW = bmOptions.outWidth
            val photoH = bmOptions.outHeight

            // Determine how much to scale down the image
            val scaleFactor = minOf(photoW / targetW, photoH / targetH)

            // Decode the image file into a Bitmap sized to fill the View
            bmOptions.inJustDecodeBounds = false
            bmOptions.inSampleSize = scaleFactor
            val bitmap = BitmapFactory.decodeFile(imagePath)
            return try {
                rotateImage(imagePath, bitmap)
            } catch (e: IOException) {
                e.printStackTrace()
                null
            }
        }

        @Throws(IOException::class)
        fun rotateImage(path: String, bitmap: Bitmap): Bitmap? {
            var rotate = 0
            val exif = ExifInterface(path)
            val orientation = exif.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL
            )
            when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_270 -> rotate = 270
                ExifInterface.ORIENTATION_ROTATE_180 -> rotate = 180
                ExifInterface.ORIENTATION_ROTATE_90 -> rotate = 90
            }
            val matrix = Matrix()
            matrix.postRotate(rotate.toFloat())
            return Bitmap.createBitmap(
                bitmap, 0, 0, bitmap.width,
                bitmap.height, matrix, true
            )
        }

        /**
         * Creates the temporary image file in the cache directory.
         *
         * @return The temporary image file.
         * @throws IOException Thrown if there is an error creating the file
         */
        @Throws(IOException::class)
        fun createTempImageFile(context: Context): File? {
            val timeStamp = SimpleDateFormat(
                "yyyyMMdd_HHmmss",
                Locale.getDefault()
            ).format(Date())
            val imageFileName = "JPEG_" + timeStamp + "_"
            val storageDir = context.externalCacheDir
            return File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",  /* suffix */
                storageDir /* directory */
            )
        }

        fun deleteImageFile(context: Context, imagePath: String?): Boolean {
            // Get the file
            imagePath?.let {
                val imageFile = File(imagePath)

                // Delete the image
                val deleted = imageFile.delete()

                // If there is an error deleting the file, show a Toast
                if (!deleted) {
                    val errorMessage = context.getString(R.string.error)
                    Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                }
                return deleted
            }
            return false
        }
    }
}