package com.kk.libtag

import android.util.Log

/**
 * Created by kevin on 03/04/2018.
 */

fun <T> T.print(msg: String = "") = apply { Log.d("KLG", "--->>$msg: $this") }