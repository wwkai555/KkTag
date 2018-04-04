package com.example.libtag

import android.util.Log
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject

/**
 * Created by kevin on 03/04/2018.
 */

fun <T> T.print(msg: String = "") = apply { Log.d("KLG", "--->>$msg: $this") }