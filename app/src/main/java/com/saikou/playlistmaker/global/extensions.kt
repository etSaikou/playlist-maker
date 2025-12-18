package com.saikou.playlistmaker.global

import android.content.Context
import android.util.TypedValue
import android.view.View
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.text.SimpleDateFormat
import java.util.Locale


fun dpToPx(dp: Float, context: Context): Int {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        dp,
        context.resources.displayMetrics
    ).toInt()
}


fun Any?.serialize(): String? {
    return GsonBuilder().create().toJson(this)
}

inline fun<reified T> String.deserialize(clazz: Class<T>): T? {
    return try {
        GsonBuilder().create().fromJson(this, clazz)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}
inline fun<reified T> String.deserializeToList(clazz: Class<T>): List<T> {
    val gson = GsonBuilder().create()

    return gson.fromJson<List<T>>(this, object: TypeToken<List<T>>(){}.type)
}

fun String.replaceDimensionArtwork(): String{
    return this.replaceAfterLast('/', "512x512bb.jpg")
}

fun Long.millisFormat(): String? {
   return SimpleDateFormat("mm:ss", Locale.getDefault()).format(this)
}

fun <T> MutableCollection<T>.removeFirst()
        = with(iterator()){ next().also{ remove() }}
fun <T> MutableCollection<T>.reAdd(t:T)
        = with(iterator()){
            next().also{ remove(t) }.also { add(t)}}
fun View.vis(visibility: Boolean) {
    this.visibility = if(visibility == true) View.VISIBLE else View.GONE
}