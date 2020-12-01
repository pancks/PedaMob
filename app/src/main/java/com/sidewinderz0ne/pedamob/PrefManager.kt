package com.sidewinderz0ne.pedamob

import android.content.Context
import android.content.SharedPreferences

class PrefManager(_context: Context) {
    var pref: SharedPreferences
    var editor: SharedPreferences.Editor
    var context: Context? = null
    // shared pref mode
    var privateMode = 0
    // shared pref mode
    var PRIVATE_MODE = 0

    var isFirstTimeLaunch: Boolean
        get() = pref.getBoolean(IS_FIRST_TIME_LAUNCH, true)
        set(isFirstTime) {
            editor.putBoolean(IS_FIRST_TIME_LAUNCH, isFirstTime)
            editor.commit()
        }

    var id: Int
        get() = pref.getInt(PEDAID, 0)
        set(isLogged) {
            editor.putInt(PEDAID, isLogged)
            editor.commit()
        }

    companion object {
        // Shared preferences file name
        private const val PREF_NAME = "PedaMobileApp"
        private const val IS_FIRST_TIME_LAUNCH = "IsFirstTimeLaunch"
        const val PEDAID = "peda_id"
    }

    init {
        pref = _context.getSharedPreferences(PREF_NAME, privateMode)
        editor = pref.edit()
    }

    fun prefManag(context: Context) {
        this. context = context
        pref = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE)
        editor = pref.edit()
    }

    fun timeLaunch(): Boolean {
        return pref.getBoolean(IS_FIRST_TIME_LAUNCH, true)
    }
}