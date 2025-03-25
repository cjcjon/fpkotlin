package org.example.chapter2

import org.example.chapter2.Chapter2Main.fibTest
import org.example.chapter2.Chapter2Main.isSortedTest
import org.example.common.head
import org.example.common.tail

object Chapter2Main {
    /* 연습문제 2-1 */
    private fun fib(i: Int): Int {
        tailrec fun go(count: Int, prev: Pair<Int, Int>): Int {
            return if (count == 0) prev.second
            else go(count - 1, Pair(prev.second, prev.first + prev.second))
        }

        return go(i - 1, Pair(0, 1))
    }

    fun fibTest() {
        require(fib(1) == 1)
        require(fib(2) == 1)
        require(fib(3) == 2)
        require(fib(4) == 3)
        require(fib(5) == 5)
        require(fib(6) == 8)
        require(fib(7) == 13)
        require(fib(8) == 21)
        require(fib(9) == 34)
        require(fib(10) == 55)
    }

    /* 연습문제 2-2 */
    private fun <A> isSorted(aa: List<A>, order: (A, A) -> Boolean): Boolean {
        tailrec fun go(compareValue: A, list: List<A>): Boolean {
            if (list.isEmpty()) return true
            else {
                val next = list.head()
                return if (order(compareValue, next)) go(next, list.tail())
                else false
            }
        }

        return if (aa.size < 2) true
        else go(aa.head(), aa.tail())
    }

    fun isSortedTest() {
        val compareFunc: (Int, Int) -> Boolean = { a, b -> a < b }
        require(isSorted(emptyList(), compareFunc))
        require(isSorted(listOf(1), compareFunc))
        require(isSorted(listOf(1, 2), compareFunc))
        require(isSorted(listOf(1, 3, 5, 7, 9), compareFunc))
        require(!isSorted(listOf(2, 1), compareFunc))
    }
}

/* 연습문제 2-3 */
fun <A, B, C> curry(f: (A, B) -> C): (A) -> (B) -> C =
    { a -> { b -> f(a, b) } }

fun <A, B, C> uncurry(f: (A) -> (B) -> C): (A, B) -> C =
    { a, b -> f(a)(b) }

fun <A, B, C> compose(f: (B) -> C, g: (A) -> B): (A) -> C =
    { a -> f(g(a)) }

fun main() {
    fibTest()
    isSortedTest()
}
