package org.example.chapter7

import arrow.core.firstOrNone
import arrow.core.getOrElse
import org.example.common.splitAt

class Par<A>(val get: A) {
    companion object {
        fun <A> unit(a: () -> A): Par<A> = Par(a())
        fun <A> get(a: Par<A>): A = a.get
    }
}

fun sum(ints: List<Int>): Int =
    if (ints.size <= 1)
        ints.firstOrNone().getOrElse { 0 }
    else {
        val (l, r) = ints.splitAt(ints.size / 2)
        val sumL: Par<Int> = Par.unit { sum(l) }
        val sumR: Par<Int> = Par.unit { sum(r) }
        sumL.get + sumR.get
    }
