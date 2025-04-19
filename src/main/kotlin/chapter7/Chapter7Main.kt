package org.example.chapter7

import arrow.core.firstOrNone
import arrow.core.getOrElse
import org.example.common.splitAt

class Par<A>(val get: A) {
    companion object {
        fun <A> unit(a: () -> A): Par<A> = Par(a())
        fun <A> get(a: Par<A>): A = a.get

        fun <A, B, C> map2(px: Par<A>, py: Par<B>, f: (A, B) -> C): Par<C> =
            Par(f(px.get, py.get))
    }
}

fun sum(ints: List<Int>): Par<Int> =
    if (ints.size <= 1)
        Par.unit { ints.firstOrNone().getOrElse { 0 } }
    else {
        val (l, r) = ints.splitAt(ints.size / 2)
        Par.map2(sum(l), sum(r)) { lx: Int, rx: Int -> lx + rx }
    }
