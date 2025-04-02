package org.example.chapter4

import kotlin.math.pow
import org.example.chapter3.List as MyList

sealed class Option<out A> {
    data class Some<out A>(val get: A) : Option<A>()
    data object None : Option<Nothing>()

    companion object {
        fun <A, B> lift(f: (A) -> B): (Option<A>) -> Option<B> =
            { oa -> oa.map(f) }

        fun <A> catches(a: () -> A): Option<A> = try {
            Some(a())
        } catch (e: Throwable) {
            None
        }

        /* 연습문제 4-4 */
        fun <A> sequence(xs: MyList<Option<A>>): Option<MyList<A>> {
            return MyList.foldRight(
                xs,
                Some(MyList.Nil)
            ) { element: Option<A>, acc: Option<MyList<A>> ->
                map2(element, acc) { a1: A, a2: MyList<A> ->
                    MyList.Cons(a1, a2)
                }
            }
        }
    }
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

/* 연습문제 4-3 */
fun <A, B, C> map2(a: Option<A>, b: Option<B>, f: (A, B) -> C): Option<C> =
    a.flatMap { aValue ->
        b.map { bValue -> f(aValue, bValue) }
    }

fun main() {
    val xs = MyList.of(Option.Some(1), Option.Some(2), Option.Some(3))
    println(Option.sequence(xs))

    val ys = MyList.of(Option.Some(1), Option.None)
    println(Option.sequence(ys))

    val ks = MyList.of<Option<Int>>()
    println(Option.sequence(ks))
}
