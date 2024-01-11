package utilities


import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.graphics.Matrix
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.FileNotFoundException
import java.io.IOException

object ImageInBitmap {
    var bitmap: Bitmap? = null


 suspend fun onImageInBitmap(uri: Uri,context: Context):Bitmap =
     withContext(Dispatchers.IO){
            try {
                    val tempBitmap = getCapturedImage(uri, context)
                    bitmap = onResizeBitmap(tempBitmap)

            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            }


         return@withContext bitmap!!
    }

    private fun getCapturedImage(selectedPhotoUri: Uri,context:Context): Bitmap {
        val bitmap = when {
            Build.VERSION.SDK_INT < 28 -> {
                MediaStore.Images.Media.getBitmap(
                    context.contentResolver,
                    selectedPhotoUri
                )
            }

            else -> {
                val source = ImageDecoder.createSource(context.contentResolver, selectedPhotoUri)
                ImageDecoder.decodeBitmap(source)
            }
        }
        return bitmap
    }

     private fun  onResizeBitmap(bitmap: Bitmap): Bitmap  {
        val width = bitmap.width
        val height = bitmap.height
        var resizeBitmap = bitmap
        val matrix = Matrix()
        val ratio = (width.toFloat() / height.toFloat())

        if (ratio > 1) {
            if (width > 1000) {
                val newWidth = 1000
                val newHeight = (1000 / ratio).toInt()
                val scaleWidth = (newWidth.toFloat() / width.toFloat())
                val scaleHeight = (newHeight.toFloat() / height.toFloat())
                matrix.postScale(scaleWidth, scaleHeight)
                resizeBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, false)
                bitmap.recycle()
            }
        } else if (height > 1000) {
            val newHeight = 1000
            val newWidth = (1000 * ratio).toInt()
            val scaleWidth = (newWidth.toFloat() / width.toFloat())
            val scaleHeight = (newHeight.toFloat() / height.toFloat())
            matrix.postScale(scaleWidth, scaleHeight)
            resizeBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, false)
            bitmap.recycle()

        }
        return resizeBitmap
    }
}