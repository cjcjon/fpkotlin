package org.example.chapter5

import org.example.chapter3.List
import org.example.chapter4.Option

sealed class Stream<out A> {
    data class Cons<out A>(
        val head: () -> A,
        val tail: () -> Stream<A>,
    ): Stream<A>()

    data object Empty: Stream<Nothing>()

    companion object {
        fun <A> cons(hd: () -> A, tl: () -> Stream<A>): Stream<A> {
            val head: A by lazy(hd)
            val tail: Stream<A> by lazy(tl)

            return Cons({ head }, { tail })
        }

        fun <A> empty(): Stream<A> = Empty

        fun <A> of(vararg xs: A): Stream<A> =
            if (xs.isEmpty()) empty()
            else cons({ xs[0] }, { of(*xs.sliceArray(1 until xs.size)) })
    }
}

fun <A> Stream<A>.headOption(): Option<A> =
    when (this) {
        is Stream.Empty -> Option.None
        is Stream.Cons -> Option.Some(head())
    }

/* 연습문제 5-1 */
fun <A> Stream<A>.toList(): List<A> {
    tailrec fun go(xs: Stream<A>, acc: List<A>): List<A> = when (xs) {
        is Stream.Cons -> go(xs.tail(), List.Cons(xs.head(), acc))
        is Stream.Empty -> acc
    }

    return List.reverse(go(this, List.Nil))
}

fun main() {

}
