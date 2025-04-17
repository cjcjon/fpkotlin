package org.example.chapter6

import org.example.chapter3.List

interface RNG {
    fun nextInt(): Pair<Int, RNG>
}

data class SimpleRNG(val seed: Long): RNG {
    override fun nextInt(): Pair<Int, RNG> {
        val newSeed = (seed * 0x5DEECE66DL + 0xBL) and 0xFFFFFFFFFFFFL
        val nextRNG = SimpleRNG(newSeed)
        val n = (newSeed ushr 16).toInt()

        return n to nextRNG
    }
}

/* 연습문제 6-1 */
fun nonNegativeInt(rng: RNG): Pair<Int, RNG> {
    val (rndNum, nextRng) = rng.nextInt()

    return if (rndNum == Int.MIN_VALUE) Int.MAX_VALUE to nextRng
    else if (rndNum < 0) -rndNum to nextRng
    else rndNum to nextRng
}

/* 연습문제 6-2 */
fun double(rng: RNG): Pair<Double, RNG> {
    val (rndNum, nextRng) = nonNegativeInt(rng)

    return (rndNum / (Int.MAX_VALUE.toDouble() + 1)) to nextRng
}

/* 연습문제 6-3 */
fun intDouble(rng: RNG): Pair<Pair<Int, Double>, RNG> {
    val (intNum, intRng) = rng.nextInt()
    val (doubleNum, doubleRng) = double(intRng)

    return (intNum to doubleNum) to doubleRng
}

fun doubleInt(rng: RNG): Pair<Pair<Double, Int>, RNG> {
    val (doubleNum, doubleRng) = double(rng)
    val (intNum, intRng) = doubleRng.nextInt()

    return (doubleNum to intNum) to intRng
}

fun double3(rng: RNG): Pair<Triple<Double, Double, Double>, RNG> {
    val (double1, rng1) = double(rng)
    val (double2, rng2) = double(rng1)
    val (double3, rng3) = double(rng2)

    return Triple(double1, double2, double3) to rng3
}

/* 연습문제 6-4 */
fun ints(count: Int, rng: RNG): Pair<List<Int>, RNG> {
    tailrec fun go(count: Int, acc: Pair<List<Int>, RNG>): Pair<List<Int>, RNG> =
        if (count > 0) {
            val (accInts, currRng) = acc
            val (nextInt, nextRng) = currRng.nextInt()

            go(count - 1, List.Cons(nextInt, accInts) to nextRng)
        } else acc

    return go(count, List.empty<Int>() to rng)
}

typealias Rand<A> = (RNG) -> Pair<A, RNG>

fun <A> unit(a: A): Rand<A> = { rng -> a to rng }

fun <A, B> map(s: Rand<A>, f: (A) -> B): Rand<B> = { rng ->
    val (a, rng2) = s(rng)
    f(a) to rng2
}

fun nonNegativeEven(): Rand<Int> =
    map(::nonNegativeInt) { it - (it % 2) }

/* 연습문제 6-5 */
fun doubleR(): Rand<Double> =
    map(::nonNegativeInt) { it / (Int.MAX_VALUE.toDouble() + 1) }

/* 연습문제 6-6 */
fun <A, B, C> map2(
    ra: Rand<A>,
    rb: Rand<B>,
    f: (A, B) -> C,
): Rand<C> = { rng ->
    val (a, rng1) = ra(rng)
    val (b, rng2) = rb(rng1)

    f(a, b) to rng2
}

fun <A, B> both(ra: Rand<A>, rb: Rand<B>): Rand<Pair<A, B>> =
    map2(ra, rb) { a, b -> a to b }

val intR: Rand<Int> = { rng -> rng.nextInt() }
val doubleR: Rand<Double> =
    map(::nonNegativeInt) { i ->
        i / (Int.MAX_VALUE.toDouble() + 1)
    }
val intDoubleR: Rand<Pair<Int, Double>> = both(intR, doubleR)
val doubleIntR: Rand<Pair<Double, Int>> = both(doubleR, intR)

/* 연습문제 6-7 */
fun <A> sequence(fs: List<Rand<A>>): Rand<List<A>> =
    List.foldRight(fs, unit(List.empty())) { f, acc ->
        map2(f, acc) { h, t -> List.Cons(h, t) }
    }

fun ints2(count: Int, rng: RNG): Pair<List<Int>, RNG> {
    fun go(c: Int): List<Rand<Int>> =
        if (c == 0) List.Nil
        else List.Cons({ r -> 1 to r }, go(c - 1))

    return sequence(go(count))(rng)
}
