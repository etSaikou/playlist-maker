package com.saikou.playlistmaker.global

import android.content.Context
import android.util.TypedValue
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken


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

inline fun<reified T> String.unserialize(clazz: Class<T>): T? {
    return try {
        GsonBuilder().create().fromJson(this, clazz)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}
inline fun<reified T> String.unserializeToList(clazz: Class<T>): List<T> {
    val gson = GsonBuilder().create()

    return gson.fromJson<List<T>>(this, object: TypeToken<List<T>>(){}.type)
}

fun <T> MutableCollection<T>.removeFirst()
        = with(iterator()){ next().also{ remove() }}
fun <T> MutableCollection<T>.reAdd(t:T)
        = with(iterator()){
            next().also{ remove(t) }.also { add(t)}}