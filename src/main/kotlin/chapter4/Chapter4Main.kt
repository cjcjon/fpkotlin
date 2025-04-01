package org.example.chapter4

sealed class Option<out A> {
    data class Some<out A>(val get: A): Option<A>()
    data object None : Option<Nothing>()
}

fun main() {

}
