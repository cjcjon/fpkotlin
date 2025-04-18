package org.example.chapter5

import org.example.chapter3.List
import org.example.chapter4.Option
import org.example.chapter4.getOrElse
import org.example.chapter4.map

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

    fun <A> hasSubsequence(s: Stream<A>): Boolean =
        this.tails().exists { it.startsWith(s) }

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
//        fun <A> constant(a: A): Stream<A> =
//            cons({ a }, { constant(a) })

        /* 연습문제 5-9 */
//        fun from(n: Int): Stream<Int> =
//            cons({ n }, { from(n + 1) })

        /* 연습문제 5-10 */
//        fun fibs(): Stream<Int> {
//            fun go(x: Int, y: Int): Stream<Int> =
//                cons({ x + y }, { go(y, x + y) })
//
//            return cons({ 0 }, { cons({ 1 }, { go(0, 1) }) })
//        }

        /* 연습문제 5-11 */
        fun <A, S> unfold(z: S, f: (S) -> Option<Pair<A, S>>): Stream<A> =
            f(z).map { (value, state) ->
                cons({ value }, { unfold(state, f) })
            }.getOrElse { empty() }

        /* 연습문제 5-12 */
        fun fibs(): Stream<Int> =
            unfold(0 to 1) { (curr, next) -> Option.Some(curr to (next to (curr + next))) }

        fun from(n: Int): Stream<Int> =
            unfold(n) { value -> Option.Some(value to value + 1) }

        fun constant(n: Int): Stream<Int> =
            unfold(n) { _ -> Option.Some(n to n) }

        fun ones(): Stream<Int> =
            unfold(1) { _ -> Option.Some(1 to 1) }
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
//fun <A> Stream<A>.take(n: Int): Stream<A> = when (this) {
//    is Stream.Cons ->
//        if (n > 0) Stream.cons(this.head) { this.tail().take(n - 1) }
//        else Stream.Empty
//
//    is Stream.Empty -> this
//}

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
//fun <A> Stream<A>.takeWhile(p: (A) -> Boolean): Stream<A> =
//    this.foldRight({ Stream.empty() }, { a, b ->
//        if (p(a)) Stream.cons({ a }, b)
//        else b()
//    })

/* 연습문제 5-6 */
fun <A> Stream<A>.headOption(): Option<A> =
    this.foldRight({ Option.None as Option<A> }, { a, b ->
        Option.Some(a)
    })

/* 연습문제 5-7 */
//fun <A, B> Stream<A>.map(f: (A) -> B): Stream<B> =
//    this.foldRight({ Stream.empty() }, { a, b ->
//        Stream.cons({ f(a) }, b)
//    })

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

/* 연습문제 5-13 */
fun <A, B> Stream<A>.map(f: (A) -> B): Stream<B> =
    Stream.unfold(this) { stream ->
        when (stream) {
            is Stream.Cons -> Option.Some(f(stream.head()) to stream.tail())
            is Stream.Empty -> Option.None
        }
    }

fun <A> Stream<A>.take(n: Int): Stream<A> =
    Stream.unfold(this) { stream ->
        when (stream) {
            is Stream.Cons ->
                if (n > 0) Option.Some(stream.head() to stream.tail().take(n - 1))
                else Option.None

            is Stream.Empty -> Option.None
        }
    }

fun <A> Stream<A>.takeWhile(p: (A) -> Boolean): Stream<A> =
    Stream.unfold(this) { stream ->
        when (stream) {
            is Stream.Cons ->
                if (p(stream.head())) Option.Some(stream.head() to stream.tail())
                else Option.None

            is Stream.Empty -> Option.None
        }
    }

fun <A, B, C> Stream<A>.zipWith(
    that: Stream<B>,
    f: (A, B) -> C
): Stream<C> = Stream.unfold(this to that) { (streamA, streamB) ->
    when (streamA) {
        is Stream.Cons ->
            when (streamB) {
                is Stream.Cons -> Option.Some(
                    f(streamA.head(), streamB.head()) to (streamA.tail() to streamB.tail())
                )

                is Stream.Empty -> Option.None
            }

        is Stream.Empty -> Option.None
    }
}

fun <A, B> Stream<A>.zipAll(
    that: Stream<B>
): Stream<Pair<Option<A>, Option<B>>> =
    Stream.unfold(this to that) { (streamA, streamB) ->
        when (streamA) {
            is Stream.Cons ->
                when (streamB) {
                    is Stream.Cons -> Option.Some(
                        (Option.Some(streamA.head()) to Option.Some(streamB.head())) to (streamA.tail() to streamB.tail())
                    )

                    is Stream.Empty -> Option.Some(
                        (Option.Some(streamA.head()) to Option.None) to (streamA.tail() to Stream.empty())
                    )
                }

            is Stream.Empty ->
                when (streamB) {
                    is Stream.Cons -> Option.Some(
                        (Option.None to Option.Some(streamB.head())) to (Stream.empty<A>() to streamB.tail())
                    )

                    is Stream.Empty ->
                        Option.None
                }
        }
    }

/* 연습문제 5-14 */
fun <A> Stream<A>.startsWith(that: Stream<A>): Boolean =
    this.zipWith(that) { a, b -> a to b }.forAll { (a, b) -> a == b }

/* 연습문제 5-15 */
fun <A> Stream<A>.tails(): Stream<Stream<A>> =
    Stream.unfold(this) { stream ->
        when (stream) {
            is Stream.Cons -> Option.Some(stream to stream.tail())
            is Stream.Empty -> Option.None
        }
    }

/* 연습문제 5-16 */
fun <A, B> Stream<A>.scanRight(z: B, f: (A, () -> B) -> B): Stream<B> =
    this.foldRight(
        { z to Stream.of(z) },
        { a: A, p0: () -> Pair<B, Stream<B>> ->
            val p1: Pair<B, Stream<B>> by lazy { p0() }
            val b2: B = f(a) { p1.first }
            Pair<B, Stream<B>>(b2, Stream.cons({ b2 }, { p1.second }))
        }
    ).second

fun main() {
    val streamA = Stream.of(1, 2, 3, 4, 5)
    println(streamA.take(2).toList())

    val streamB = Stream.of(1, 2, 3, 4, 5)
    println(streamB.drop(3).toList())

    val streamC = Stream.of(1, 2, 3, 4, 5)
    println(streamC.takeWhile { it < 3 }.toList())
}
