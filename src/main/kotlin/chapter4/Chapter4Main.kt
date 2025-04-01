package org.example.chapter4

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

fun main() {

}
