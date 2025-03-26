package org.example.chapter3

sealed class List<out A> {
    data object Nil: List<Nothing>()
    data class Cons<out A>(val head: A, val tail: List<A>): List<A>()

    companion object {
        fun <A> of(vararg args: A): List<A> {
            val tail = args.sliceArray(1 until args.size)
            return if (args.isEmpty()) Nil else Cons(args[0], of(*tail))
        }

        fun sum(ints: List<Int>): Int =
            when (ints) {
                is Nil -> 0
                is Cons -> ints.head + sum(ints.tail)
            }

        fun product(doubles: List<Double>): Double =
            when (doubles) {
                is Nil -> 1.0
                is Cons ->
                    if (doubles.head == 0.0) 0.0
                    else doubles.head * product(doubles.tail)
            }

        fun <A> append(a1: List<A>, a2: List<A>): List<A> =
            when (a1) {
                is Nil -> a2
                is Cons -> Cons(a1.head, append(a1.tail, a2))
            }

        /* 연습문제 3-1 */
        fun <A> tail(xs: List<A>): List<A> = when (xs) {
            is Nil -> Nil
            is Cons -> xs.tail
        }

        /* 연습문제 3-2 */
        fun <A> setHead(xs: List<A>, x: A): List<A> = when (xs) {
            is Nil -> Cons(x, Nil)
            is Cons -> Cons(x, xs.tail)
        }

        /* 연습문제 3-3 */
        fun <A> drop(list: List<A>, n: Int): List<A> = when (list) {
            is Nil -> Nil
            is Cons ->
                if (n > 0) drop(list.tail, n - 1)
                else list
        }

        /* 연습문제 3-4 */
        fun <A> dropWhile(list: List<A>, f: (A) -> Boolean): List<A> = when (list) {
            is Nil -> Nil
            is Cons ->
                if (f(list.head)) dropWhile(list.tail, f)
                else list
        }

        /* 연습문제 3-5 */
        fun <A> init(list: List<A>): List<A> = when (list) {
            is Nil -> Nil
            is Cons -> when (list.tail) {
                is Cons -> Cons(list.head, init(list.tail))
                is Nil -> Nil
            }
        }
    }
}

fun main() {

}
