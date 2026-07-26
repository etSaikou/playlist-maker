package com.saikou.playlistmaker.global

import android.content.Context
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.saikou.playlistmaker.R
import java.text.SimpleDateFormat
import java.util.Locale


fun dpToPx(dp: Float, context: Context?): Int {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        dp,
        context?.resources?.displayMetrics
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

fun <T> MutableCollection<T>.removeFirst() {
    val it = iterator()
    if (it.hasNext()) {
        it.next()
        it.remove()
    }
}

fun <T> MutableCollection<T>.reAdd(t: T) {
    remove(t)
    add(t)
}
fun View.vis(visibility: Boolean) {
    this.visibility = if(visibility == true) View.VISIBLE else View.GONE
}

fun Fragment.showCustomToast(message: String) {
    val layout = LayoutInflater.from(requireContext()).inflate(R.layout.layout_toast, null)
    val text: TextView = layout.findViewById(R.id.vToastText)
    text.text = message

    Toast(requireContext()).apply {
        duration = Toast.LENGTH_SHORT
        setGravity(Gravity.BOTTOM or Gravity.FILL_HORIZONTAL, 0, resources.getDimensionPixelSize(R.dimen.margin16))
        view = layout
        show()
    }
}
