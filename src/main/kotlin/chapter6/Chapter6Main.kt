package org.example.chapter6

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
