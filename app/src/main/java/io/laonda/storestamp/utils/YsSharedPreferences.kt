package io.laonda.storestamp.utils

import android.content.Context
import android.content.SharedPreferences

class YsSharedPreferences(val context: Context) {
    private val PREFS_NAME = "ys_sharedpref"

    private val PREF_KEY_ACCESS_TOKEN = "PREF_KEY_ACCESS_TOKEN"
    private val PREF_KEY_REFRESH_TOKEN = "PREF_KEY_REFRESH_TOKEN"

    private val sharedPref: SharedPreferences = context.getSharedPreferences(PREFS_NAME, 0)

    var accessToken: String
        get() = sharedPref.getString(PREF_KEY_ACCESS_TOKEN, "").toString()
        set(value) = sharedPref.edit().putString(PREF_KEY_ACCESS_TOKEN, value).apply()

    var refreshToken: String
        get() = sharedPref.getString(PREF_KEY_REFRESH_TOKEN, "").toString()
        set(value) = sharedPref.edit().putString(PREF_KEY_REFRESH_TOKEN, value).apply()
}