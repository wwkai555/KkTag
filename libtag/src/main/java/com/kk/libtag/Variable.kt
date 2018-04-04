package com.kk.libtag

import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject

/**
 * Created by kevin on 04/04/2018.
 */
class Variable<V>(var v: V, ignoreDefaultValue: Boolean = false) {
    private val subject: BehaviorSubject<V> = if (ignoreDefaultValue) BehaviorSubject.create() else BehaviorSubject.createDefault(v)

    fun set(v: V) {
        synchronized(this) {
            this.v = v
            subject.onNext(v)
        }
    }

    fun asObservable(): Observable<V> = subject

    fun observer(action: (v: V) -> Unit) {
        subject.subscribe { action(it) }
    }
}
