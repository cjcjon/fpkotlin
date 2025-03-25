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
            is Cons -> Cons(
                list.head,
                if (n > 1) drop(list.tail, n - 1) else Nil
            )
        }
    }
}

fun main() {

}
