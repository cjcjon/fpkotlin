package org.example.common

fun <T> List<T>.head(): T = this.first()

fun <T> List<T>.tail(): List<T> = this.drop(1)

fun <T> List<T>.splitAt(idx: Int): Pair<List<T>, List<T>> =
    this.subList(0, idx) to this.drop(idx)
