package org.example.chapter3

sealed class List<out A> {
    data object Nil: List<Nothing>()
    data class Cons<out A>(val head: A, val tail: List<A>): List<A>()

    companion object {
        fun <A> empty(): List<A> = Nil

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

        fun <A, B> foldRight(xs: List<A>, z: B, f: (A, B) -> B): B =
            when (xs) {
                is Nil -> z
                is Cons -> f(xs.head, foldRight(xs.tail, z, f))
            }

        fun sum2(ints: List<Int>): Int =
            foldRight(ints, 0) { a, b -> a + b }

        fun product2(dbs: List<Double>): Double =
            foldRight(dbs, 1.0) { a, b -> a * b }

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

        /* 연습문제 3-8 */
//        fun <A> length(xs: List<A>): Int =
//            foldRight(xs, 0, { _, acc -> acc + 1 })

        /* 연습문제 3-9 */
        tailrec fun <A, B> foldLeft(xs: List<A>, z: B, f: (B, A) -> B): B = when (xs) {
            is Nil -> z
            is Cons -> foldLeft(xs.tail, f(z, xs.head), f)
        }

        /* 연습문제 3-10 */
        fun sum3(ints: List<Int>): Int =
            foldLeft(ints, 0) { b, a -> a + b }

        fun product3(dbs: List<Double>): Double =
            foldLeft(dbs, 1.0) { b, a -> a * b }

        fun <A> length(xs: List<A>): Int =
            foldLeft(xs, 0) { acc, _ -> acc + 1 }
    }
}

fun main() {

}
