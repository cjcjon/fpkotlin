package org.example.chapter4

import org.example.chapter3.List.Companion.sum
import org.example.chapter3.sum
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
//            return MyList.foldRight(
//                xs,
//                Some(MyList.Nil)
//            ) { element: Option<A>, acc: Option<MyList<A>> ->
//                map2(element, acc) { a1: A, a2: MyList<A> ->
//                    MyList.Cons(a1, a2)
//                }
//            }

            return traverse(xs) { it }
        }

        /* 연습문제 4-5 */
        fun <A, B> traverse(xa: MyList<A>, f: (A) -> Option<B>): Option<MyList<B>> {
            return MyList.foldRight(
                xa,
                Some<MyList<B>>(MyList.Nil)
            ) { element: A, acc: Option<MyList<B>> ->
                map2(f(element), acc) { a1: B, a2: MyList<B> ->
                    MyList.Cons(a1, a2)
                }
            }
        }
    }
}

sealed class Either<out E, out A> {
    data class Left<out E>(val value: E) : Either<E, Nothing>()
    data class Right<out A>(val value: A) : Either<Nothing, A>()

    companion object {
        fun <A> catches(a: () -> A): Either<Exception, A> =
            try {
                Right(a())
            } catch (e: Exception) {
                Left(e)
            }

        fun mean(xs: MyList<Double>): Either<String, Double> =
            if (xs.isEmpty()) Left("mean of empty list!")
            else Right(xs.sum() / xs.size())

        fun saveDiv(x: Int, y: Int): Either<Exception, Int> =
            try {
                Right(x / y)
            } catch (e: Exception) {
                Left(e)
            }

        /* 연습문제 4-7 */
        fun <E, A> sequence(xs: MyList<Either<E, A>>): Either<E, MyList<A>> =
//            MyList.foldRight(xs, Right(MyList.Nil)) { element: Either<E, A>, acc: Either<E, MyList<A>> ->
//                map2(element, acc) { a1: A, a2: MyList<A> ->
//                    MyList.Cons(a1, a2)
//                }
//            }
            traverse(xs) { it }

        fun <E, A, B> traverse(xs: MyList<A>, f: (A) -> Either<E, B>): Either<E, MyList<B>> =
//            MyList.foldRight(xs, Right<MyList<B>>(MyList.Nil)) { element: A, acc: Either<E, MyList<B>> ->
//                map2(f(element), acc) { a1: B, a2: MyList<B> ->
//                    MyList.Cons(a1, a2)
//                }
//            }
            when (xs) {
                is MyList.Nil -> Right(MyList.Nil)
                is MyList.Cons -> map2(f(xs.head), traverse(xs.tail, f)) { b, xb -> MyList.Cons(b, xb) }
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

/* 연습문제 4-6 */
fun <E, A, B> Either<E, A>.map(f: (A) -> B): Either<E, B> = when (this) {
    is Either.Left -> this
    is Either.Right -> Either.Right(f(this.value))
}

fun <E, A, B> Either<E, A>.flatMap(f: (A) -> Either<E, B>): Either<E, B> = when (this) {
    is Either.Left -> this
    is Either.Right -> f(this.value)
}

fun <E, A> Either<E, A>.orElse(f: () -> Either<E, A>): Either<E, A> = when (this) {
    is Either.Left -> f()
    is Either.Right -> this
}

fun <E, A, B, C> map2(
    ae: Either<E, A>,
    be: Either<E, B>,
    f: (A, B) -> C,
): Either<E, C> = ae.flatMap { a -> be.map { b -> f(a, b) } }

fun main() {
    val xs = MyList.of(Option.Some(1), Option.Some(2), Option.Some(3))
    println(Option.sequence(xs))

    val ys = MyList.of(Option.Some(1), Option.None)
    println(Option.sequence(ys))

    val ks = MyList.of<Option<Int>>()
    println(Option.sequence(ks))
}
