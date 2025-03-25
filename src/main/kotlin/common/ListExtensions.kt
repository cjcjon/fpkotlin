package org.example.common

fun <T> List<T>.head(): T = this.first()

fun <T> List<T>.tail(): List<T> = this.drop(1)
