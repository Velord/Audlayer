package util.functionalFrogrammingType

import util.functionalFrogrammingType.Stream.Companion.drop
import util.functionalFrogrammingType.result.Result
import util.list.FList

sealed class Stream<out A> {

    abstract fun isEmpty(): Boolean

    abstract fun head(): Result<A>

    abstract fun  tail(): Result<Stream<A>>

    abstract fun takeAtMost(n: Int): Stream<A>

    abstract fun takeWhile(p: (A) -> Boolean): Stream<A>

    abstract fun <B> foldRight(z: Lazy<B>,
                               f: (A) -> (Lazy<B>) -> B): B

    fun dropAtMost(n: Int) = dropAtMost(n, this)

    fun toList(): FList<A> = toList(this)

    fun dropWhile(p: (A) -> Boolean) = dropWhile(this, p)

    fun exists(p: (A) -> Boolean) = exists(this, p)

    fun takeWhileViaFoldRight(p: (A) -> Boolean): Stream<A> =
            foldRight(Lazy { Empty }, { a ->
                { b: Lazy<Stream<A>> ->
                    if (p(a))
                        cons(Lazy { a }, b)
                    else
                        Empty
                 }
            } )

    fun headSafeViaFoldRight(): Result<A> =
            foldRight(Lazy { Result<A>() }, { a -> { Result(a) } })

    fun <B> map(f: (A) -> B): Stream<B> =
            foldRight(Lazy { Empty }, { a ->
                { b: Lazy<Stream<B>> ->
                    cons(Lazy { f(a) }, b)
                }
            } )

    fun filter(p: (A) -> Boolean): Stream<A> =
            dropWhile { x -> !p(x)}.let { stream ->
                when(stream) {
                    is Empty -> stream
                    is Cons -> cons(
                        stream.hd,
                        Lazy { stream.tl().filter(p) })
                }
            }

    fun filter2(p: (A) -> Boolean): Stream<A> =
            dropWhile { x -> !p(x) }.let { stream ->
                when (stream) {
                    is Empty -> stream
                    is Cons -> stream.head().map({ a ->
                        cons(Lazy { a }, Lazy { stream.tl().filter(p) })
                    }).getOrElse(Empty)
                }
            }

    fun append(stream2: Lazy<Stream<@UnsafeVariance A>>): Stream<A> =
            this.foldRight(stream2) { a: A ->
                { b: Lazy<Stream<@UnsafeVariance A>> ->
                    cons(Lazy { a }, b)
                }
            }

    fun <B> flatMap(f: (A) -> Stream<B>): Stream<B> =
            foldRight(Lazy { Empty as Stream<B> }, { a ->
                { b: Lazy<Stream<B>> ->
                    f(a).append(b)
                }
            } )

    fun find(p: (A) -> Boolean): Result<A> = filter(p).head()

    private object Empty: Stream<Nothing>() {

        override fun head(): Result<Nothing> = Result()

        override fun tail(): Result<Stream<Nothing>> = Result()

        override fun isEmpty(): Boolean = true

        override fun takeAtMost(n: Int): Stream<Nothing> = this

        override fun takeWhile(p: (Nothing) -> Boolean): Stream<Nothing> = this

        override fun <B> foldRight(z: Lazy<B>, f: (Nothing) -> (Lazy<B>) -> B): B = z()
    }

    private class Cons<out A> (internal val hd: Lazy<A>,
                               internal val tl: Lazy<Stream<A>>
    ): Stream<A>() {

        override fun head(): Result<A> = Result.invoke(hd())

        override fun tail(): Result<Stream<A>> = Result.invoke(tl())

        override fun isEmpty(): Boolean = false

        override fun takeAtMost(n: Int): Stream<A> =
                when {
                    n > 0 -> cons(hd, Lazy { tl().takeAtMost(n - 1) })
                    else -> Empty
                }

        override fun takeWhile(p: (A) -> Boolean): Stream<A> =
                when {
                    p(hd()) -> cons(hd, Lazy { tl().takeWhile(p) })
                    else -> Empty
                }

        override fun <B> foldRight(z: Lazy<B>, f: (A) -> (Lazy<B>) -> B): B  =
                f(hd())(Lazy { tl().foldRight(z, f) })
    }

    companion object {
        fun <A> cons(hd: Lazy<A>,
                     tl: Lazy<Stream<A>>
        ): Stream<A> = Cons(hd, tl)

        operator fun  <A> invoke(): Stream<A> = Empty

        fun from(i: Int): Stream<Int> = iterate(i) { it + 1 }

        fun fromViaUnfold(n: Int): Stream<Int> =
            unfold(n) { x ->
                Result(Pair(x, x + 1))
            }

        fun <A> repeat(f: () -> @UnsafeVariance A): Stream<A> =
            cons(Lazy { f() }, Lazy { repeat { f() } })

        tailrec fun drop(a: Int, s: Stream<Int>): Stream<Int> = when(a) {
            0 -> s
            else -> drop( a - 1, s.tail().getOrElse(from(-1)))
        }


        tailrec fun <A> dropAtMost(n: Int, s: Stream<A>): Stream<A> = when {
            n > 0 -> when(s) {
                is Empty -> s
                is Cons -> dropAtMost(n - 1, s.tl())
            }
            else -> s
        }

        tailrec fun <A> dropWhile(stream: Stream<A>,
                                  p: (A) -> Boolean): Stream<A> =
                when(stream) {
                    is Empty -> stream
                    is Cons -> when {
                        p(stream.hd()) -> dropWhile(stream.tl(), p)
                        else -> stream
                    }
        }

        fun <A> toList(stream: Stream<A>): FList<A> {
            tailrec fun <A> toList(list: FList<A>, stream: Stream<A>): FList<A> =
                    when(stream) {
                        Empty -> list
                        is Cons -> toList(list.cons(stream.hd()), stream.tl())
                    }
            return toList(FList(), stream).reverse()
        }

        fun <A> iterate(seed: A, f: (A) -> A): Stream<A> =
            cons(Lazy { seed }, Lazy { iterate(f(seed), f) })

        fun <A> iterate(seed: Lazy<A>, f: (A) -> A): Stream<A> =
            cons(seed, Lazy { iterate(f(seed()), f) })

        tailrec fun <A> exists(stream: Stream<A>, p: (A) ->  Boolean): Boolean =
                when(stream) {
                    is Empty -> false
                    is Cons -> when {
                        p(stream.hd()) -> true
                        else -> exists(stream.tl(), p)
                    }
                }

        fun fibs(): Stream<Int> = unfold(Pair(1, 1)) { x ->
            Result(Pair(x.first, Pair(x.second, x.first + x.second)))
        }

        fun <A, S> unfold(z: S, f: (S) -> Result<Pair<A, S>>): Stream<A> =
                f(z).map { x ->
                    cons(
                        Lazy { x.first },
                        Lazy { unfold(x.second, f) })
                }.getOrElse(Empty)

        fun <A> fill(n: Int, elem: Lazy<A>): Stream<A> {
            tailrec fun <A> fill(acc: Stream<A>, n: Int, elem: Lazy<A>): Stream<A> =
                    when {
                        n <= 0 -> acc
                        else -> fill(Cons(elem, Lazy { acc }), n - 1, elem)
                    }
            return fill(Empty, n, elem)
        }
    }
}

fun main() {
    val stream = Stream.from(1)
    stream.head().forEach( { println(it) } )
    stream.tail().flatMap { it.head() }.forEach( { println(it) } )
    stream.tail().flatMap {
        it.tail().flatMap { it.head() }
    }.forEach( { println(it) } )
    stream.tail().flatMap {
        it.tail().flatMap {
        it.tail().flatMap {
            it.head() } }
    }.forEach( { println(it) } )


    var n = 0
    val a = 20030
    fun next() = ++n

    val x = a % 10000
    println(x)
    val stream1 = drop(x, Stream.repeat { next() })
    println(stream1.head())
    val result = stream1.head().getOrElse(-1)
    println(result)
    n = 0
    println(result == 1)

    val a1 = 342
    val offset = 46
    val stream2 = Stream.from(a1)
    val stream2Taked = stream2.takeAtMost(offset)
    val stream2Dropped = stream.dropAtMost(offset)
    val stream2DroppedTaked = stream2Dropped.takeAtMost(offset)
    val s = Stream.from(a1).dropAtMost(offset).takeAtMost(offset)
    println(s.head())
    println(stream2.head())
    println(stream2Dropped.head())
    println(stream2Taked.head())
    println(stream2DroppedTaked.head())
    println(stream2DroppedTaked.head().map { it == a + offset }.getOrElse( false))
}