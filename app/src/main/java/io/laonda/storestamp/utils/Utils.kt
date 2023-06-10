package io.laonda.storestamp.utils

import android.content.Context
import android.content.Intent
import android.widget.Toast
import io.laonda.storestamp.BuildConfig
import io.laonda.storestamp.ui.activity.CrashLogActivity
import okhttp3.ResponseBody
import org.json.JSONObject
import java.io.PrintWriter
import java.io.StringWriter
import java.text.SimpleDateFormat

fun ResponseBody?.displayError(context: Context) {
    this?.let {
        val jsonObject = JSONObject(string())
        Toast.makeText(context, "${jsonObject.getString("message")}", Toast.LENGTH_SHORT).show()
    }
}

fun String.toDate(context: Context): String {
    val locale = context.getResources().getConfiguration().getLocales().get(0)

    val simpleDateFormatReader = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", locale)
    val date = simpleDateFormatReader.parse(this)

    val simpleDateFormatWriter = SimpleDateFormat("yyyy.MM.dd, HH:mm", locale)
    return simpleDateFormatWriter.format(date)
}