package org.example.chapter5

import org.example.chapter3.List
import org.example.chapter4.Option

sealed class Stream<out A> {
    data class Cons<out A>(
        val head: () -> A,
        val tail: () -> Stream<A>,
    ) : Stream<A>()

    data object Empty : Stream<Nothing>()

    fun exists(p: (A) -> Boolean): Boolean = when (this) {
        is Cons -> p(this.head()) || this.tail().exists(p)
        else -> false
    }

    fun <B> foldRight(
        z: () -> B,
        f: (A, () -> B) -> B
    ): B = when (this) {
        is Cons -> f(this.head()) { tail().foldRight(z, f) }
        is Empty -> z()
    }

    fun find(p: (A) -> Boolean): Option<A> =
        filter(p).headOption()

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

        /* 연습문제 5-8 */
        fun <A> constant(a: A): Stream<A> =
            cons({ a }, { constant(a) })

        /* 연습문제 5-9 */
        fun from(n: Int): Stream<Int> =
            cons({ n }, { from(n + 1) })
    }
}

/* 연습문제 5-1 */
fun <A> Stream<A>.toList(): List<A> {
    tailrec fun go(xs: Stream<A>, acc: List<A>): List<A> = when (xs) {
        is Stream.Cons -> go(xs.tail(), List.Cons(xs.head(), acc))
        is Stream.Empty -> acc
    }

    return List.reverse(go(this, List.Nil))
}

/* 연습문제 5-2 */
fun <A> Stream<A>.take(n: Int): Stream<A> = when (this) {
    is Stream.Cons ->
        if (n > 0) Stream.cons(this.head) { this.tail().take(n - 1) }
        else Stream.Empty

    is Stream.Empty -> this
}

fun <A> Stream<A>.drop(n: Int): Stream<A> = when (this) {
    is Stream.Cons ->
        if (n > 0) this.tail().drop(n - 1)
        else this

    is Stream.Empty -> this
}

/* 연습문제 5-4 */
fun <A> Stream<A>.forAll(p: (A) -> Boolean): Boolean =
    this.foldRight({ true }, { a, b -> p(a) && b() })

/* 연습문제 5-5 */
fun <A> Stream<A>.takeWhile(p: (A) -> Boolean): Stream<A> =
    this.foldRight({ Stream.empty() }, { a, b ->
        if (p(a)) Stream.cons({ a }, b)
        else b()
    })

/* 연습문제 5-6 */
fun <A> Stream<A>.headOption(): Option<A> =
    this.foldRight({ Option.None as Option<A> }, { a, b ->
        Option.Some(a)
    })

/* 연습문제 5-7 */
fun <A, B> Stream<A>.map(f: (A) -> B): Stream<B> =
    this.foldRight({ Stream.empty() }, { a, b ->
        Stream.cons({ f(a) }, b)
    })

fun <A> Stream<A>.filter(p: (A) -> Boolean): Stream<A> = when (this) {
    is Stream.Cons ->
        if (p(this.head())) Stream.cons(this.head, { this.tail().filter(p) })
        else this.tail().filter(p)

    is Stream.Empty -> Stream.Empty
}

fun <A> Stream<A>.append(a: () -> Stream<A>): Stream<A> = when (this) {
    is Stream.Cons -> Stream.cons(this.head, { this.tail().append(a) })
    is Stream.Empty -> a()
}

fun main() {
    val streamA = Stream.of(1, 2, 3, 4, 5)
    println(streamA.take(2).toList())

    val streamB = Stream.of(1, 2, 3, 4, 5)
    println(streamB.drop(3).toList())

    val streamC = Stream.of(1, 2, 3, 4, 5)
    println(streamC.takeWhile { it < 3 }.toList())
}
