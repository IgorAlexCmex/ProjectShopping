package utilities

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.text.Html
import android.text.Spanned
import android.util.Base64
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.preference.Preference
import androidx.preference.PreferenceManager
import constants.Constants
import tables.ShoppingList
import java.io.ByteArrayOutputStream
import java.lang.StringBuilder

import java.text.SimpleDateFormat
import java.util.Calendar


fun Fragment.myToast(string: String) {
    Toast.makeText(activity, string, Toast.LENGTH_SHORT).show()
}

fun AppCompatActivity.myToast(string: String) {
    Toast.makeText(this, string, Toast.LENGTH_LONG).show()
}

fun myToastLong(context: Context, string: String) {
    Toast.makeText(context, string, Toast.LENGTH_LONG).show()
}
fun myToastShort(context: Context, string: String) {
    Toast.makeText(context, string, Toast.LENGTH_SHORT).show()
}

fun myLog(text:String){
 Log.d("CMEX",text)
}

@SuppressLint("SimpleDateFormat")
fun utilGetTime(timeInMileSec: Long): String {
    val timeFormat = SimpleDateFormat(Constants.TIME)
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = timeInMileSec
    // timeFormat.timeZone= TimeZone.getTimeZone("")
    myLog(timeFormat.format(calendar.time))
    return timeFormat.format(calendar.time)
}
fun utilSetFormatTime(time:String,pref:SharedPreferences):String{
    val formatTime=SimpleDateFormat(Constants.TIME)
    val date= formatTime.parse(time)
    val newFormat=pref.getString(Constants.STYLE_TIME,Constants.TIME)
    val newFormatTime=SimpleDateFormat(newFormat)
      return  if(date!=null) newFormatTime.format(date) else time
}
fun utilUriPermission(uriImage:Uri,context: Context){
    context.contentResolver.takePersistableUriPermission(
        uriImage,
        Intent.FLAG_GRANT_READ_URI_PERMISSION)
}

 fun utilBitmapInByteArray(bitmap: Bitmap): ByteArray {
    val byteArrayOutputStream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.JPEG, 20, byteArrayOutputStream)
    bitmap.recycle()
    return byteArrayOutputStream.toByteArray()
}
 fun utilByteArrayInBitmap(byteArray:ByteArray): Bitmap {
    return BitmapFactory.decodeByteArray(byteArray, 0,byteArray.size)
}
fun utilConvertBitmapToString(bitmap: Bitmap):String{
    val baos=ByteArrayOutputStream()
     bitmap.compress(Bitmap.CompressFormat.JPEG,50,baos)
    val byteArray=baos.toByteArray()
    return Base64.encodeToString(byteArray,Base64.DEFAULT)

}
fun utilConvertStringToBitmap(stringByteArray:String):Bitmap?{
    return try{
       val encodeByte=Base64.decode(stringByteArray,Base64.DEFAULT)
        BitmapFactory.decodeByteArray(encodeByte,0,encodeByte.size)
    }catch (e:Exception) {
       e.printStackTrace()
        null
    }
}
fun utilGetHtml(text:String):Spanned{
  return if(Build.VERSION.SDK_INT<=Build.VERSION_CODES.N){
      Html.fromHtml(text)
  } else {
      Html.fromHtml(text,Html.FROM_HTML_MODE_COMPACT)
  }
}
fun utilHtmlToString(spanned: Spanned):String{
    return if(Build.VERSION.SDK_INT<=Build.VERSION_CODES.N){
        Html.toHtml(spanned)
    } else {
        Html.toHtml(spanned,Html.FROM_HTML_MODE_COMPACT)
    }
}

fun utilShareShoppingList(listShop:ArrayList<ShoppingList>,titleList:String):Intent{
    val builder=StringBuilder()
    builder.append("⭐$titleList\uD83D\uDC5C \n ")  //👜
    builder.append("\n")
    var count=0
    listShop.forEach {
        count++
        builder.append("$count. ${it.nameItem} ${it.infoItem} \n")
    }
    val intent=Intent(Intent.ACTION_SEND)
    intent.type="text/plane"
    intent.putExtra(Intent.EXTRA_TEXT,builder.toString())
    return intent
}
