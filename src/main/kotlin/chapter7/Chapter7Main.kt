package org.example.chapter7

import org.example.common.head
import org.example.common.splitAt
import java.util.concurrent.TimeUnit

fun interface Callable<A> {
    fun call(): A
}

interface Future<A> {
    fun get(): A
    fun get(timeout: Long, timeUnit: TimeUnit): A
    fun cancel(evenIfRunning: Boolean): Boolean
    fun isDone(): Boolean
    fun isCancelled(): Boolean
}

interface ExecutorService {
    fun <A> submit(c: Callable<A>): Future<A>
}

typealias Par<A> = (ExecutorService) -> Future<A>

object Pars {
    fun <A> unit(a: A): Par<A> =
        { es: ExecutorService -> UnitFuture(a) }

    fun <A> lazyUnit(a: () -> A): Par<A> =
        { es: ExecutorService -> UnitFuture(a()) }

    /* 연습문제 7-4 */
    fun <A, B> asyncF(f: (A) -> B): (A) -> Par<B> =
        { a -> lazyUnit { f(a) } }

    data class UnitFuture<A>(val a: A): Future<A> {
        override fun get(): A = a
        override fun get(timeout: Long, timeUnit: TimeUnit): A = a
        override fun cancel(evenIfRunning: Boolean): Boolean = false
        override fun isDone(): Boolean = true
        override fun isCancelled(): Boolean = false
    }

    data class TimedMap2Future<A, B, C>(
        val pa: Future<A>,
        val pb: Future<B>,
        val f: (A, B) -> C
    ) : Future<C> {
        override fun isDone(): Boolean = TODO("Unused")
        override fun get(): C = TODO("Unused")
        override fun get(timeout: Long, timeUnit: TimeUnit): C {
            val timeoutMillis = TimeUnit.MILLISECONDS.convert(timeout, timeUnit)

            val start = System.currentTimeMillis()
            val a = pa.get(timeout, timeUnit)
            val duration = System.currentTimeMillis() - start

            val remainder = timeoutMillis - duration
            val b = pb.get(remainder, TimeUnit.MILLISECONDS)

            return f(a, b)
        }

        override fun cancel(evenIfRunning: Boolean): Boolean = TODO("Unused")

        override fun isCancelled(): Boolean = TODO("Unused")
    }

    fun <A, B, C> map2(
        a: Par<A>,
        b: Par<B>,
        f: (A, B) -> C
    ): Par<C> = { es: ExecutorService ->
        val af: Future<A> = a(es)
        val bf: Future<B> = b(es)

        TimedMap2Future(af, bf, f)
    }

    fun <A> fork(
        a: () -> Par<A>
    ): Par<A> = { es: ExecutorService ->
        es.submit { a()(es).get() }
    }

    fun <A> delay(pa: () -> Par<A>): Par<A> =
        { es -> pa()(es) }

    /* 연습문제 7-5 */
    fun <A> sequence(ps: List<Par<A>>): Par<List<A>> = when {
        ps.isEmpty() -> unit(listOf<Nothing>())
        ps.size == 1 -> map(ps.head()) { listOf(it) }
        else -> {
            val (l, r) = ps.splitAt(ps.size / 2)
            map2(sequence(l), sequence(r)) { la, lb -> la + lb }
        }
    }

    fun <A, B> map(pa: Par<A>, f: (A) -> B): Par<B> =
        map2(pa, unit(Unit), { a, _ -> f(a) })

    fun <A, B> parMap(ps: List<A>, f: (A) -> B): Par<List<B>> = fork {
        val fbs: List<Par<B>> = ps.map(asyncF(f))
        sequence(fbs)
    }

    /* 연습문제 7-6 */
    fun <A> parFilter(sa: List<A>, f: (A) -> Boolean): Par<List<A>> {
        val pars: List<Par<A>> = sa.map { lazyUnit { it } }

        return map(sequence(pars)) { la: List<A> ->
            la.flatMap { a ->
                if (f(a)) listOf(a) else emptyList()
            }
        }
    }

    /* 연습문제 7-10 */
//    fun <A> choiceN(n: Par<Int>, choices: List<Par<A>>): Par<A> = { es: ExecutorService ->
//        choices[n(es).get()].invoke(es)
//    }

//    fun <A> choice(cond: Par<Boolean>, t: Par<A>, f: Par<A>): Par<A> = { es: ExecutorService ->
//        choiceN(map(cond, { if (it) 1 else 0 }), listOf(f, t))(es)
//    }

    /* 연습문제 7-11 */
//    fun <K, V> choiceMap(
//        key: Par<K>,
//        choices: Map<K, Par<V>>
//    ): Par<V> = { es: ExecutorService ->
//        choices[key(es).get()]!!.invoke(es)
//    }

    /* 연습문제 7-12 */
    fun <A, B> flatMap(pa: Par<A>, choices: (A) -> Par<B>): Par<B> = { es: ExecutorService ->
        choices(pa(es).get()).invoke(es)
    }

    fun <A> choiceN(n: Par<Int>, choices: List<Par<A>>): Par<A> =
        flatMap(n) { idx -> choices[idx] }

    fun <A> choice(cond: Par<Boolean>, t: Par<A>, f: Par<A>): Par<A> =
        choiceN(map(cond) { if (it) 1 else 0 }, listOf(f, t))

    fun <K, V> choiceMap(
        key: Par<K>,
        choices: Map<K, Par<V>>
    ): Par<V> = flatMap(key) { k -> choices[k]!! }

    /* 연습문제 7-13 */
    fun <A> join(a: Par<Par<A>>): Par<A> =
        { es: ExecutorService -> a(es).get()(es) }

    fun <A, B> flatMapViaJoin(pa: Par<A>, f: (A) -> Par<B>): Par<B> =
        join(map(pa, f))

    fun <A> joinViaFlatMap(a: Par<Par<A>>): Par<A> =
        flatMap(a) { it }

    fun sortPar(parList: Par<List<Int>>): Par<List<Int>> =
        map(parList) { it.sorted() }
}
