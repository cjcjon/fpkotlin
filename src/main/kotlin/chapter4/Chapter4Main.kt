package org.example.chapter4

import kotlin.math.pow

sealed class Option<out A> {
    data class Some<out A>(val get: A): Option<A>()
    data object None : Option<Nothing>()
}

/* 연습문제 4-1 */
fun <A, B> Option<A>.map(f: (A) -> B): Option<B> = when (this) {
    is Option.None -> this
    is Option.Some -> Option.Some(f(this.get))
}

fun <A, B> Option<A>.flatMap(f: (A) -> Option<B>): Option<B> = when (this) {
    is Option.None -> Option.None
    is Option.Some -> f(this.get)
}

fun <A> Option<A>.getOrElse(default: () -> A): A = when (this) {
    is Option.None -> default()
    is Option.Some -> this.get
}

fun <A> Option<A>.orElse(ob: () -> Option<A>) = when (this) {
    is Option.None -> ob()
    is Option.Some -> this
}

fun <A> Option<A>.filter(f: (A) -> Boolean): Option<A> = when (this) {
    is Option.None -> this
    is Option.Some ->
        if (f(this.get)) this
        else Option.None
}

/* 연습문제 4-2 */
fun mean(xs: List<Double>): Option<Double> =
    if (xs.isEmpty()) Option.None
    else Option.Some(xs.sum() / xs.size)

fun variance(xs: List<Double>): Option<Double> =
    mean(xs).flatMap { m ->
        mean(xs.map { x ->
            (x - m).pow(2)
        })
    }

fun main() {
    val xs = listOf(2.0, 4.0, 6.0, 8.0, 10.0)
    println(variance(xs))
}
