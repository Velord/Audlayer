package util.functionalFrogrammingType

import util.functionalFrogrammingType.result.Result
import util.list.FList

sealed class LeftistHeap<out A> {

    internal abstract val comparator: Result<Comparator<@UnsafeVariance A>>

    abstract val left: Result<LeftistHeap<A>>
    abstract val right: Result<LeftistHeap<A>>
    abstract val head: Result<A>

    abstract val rank: Int
    abstract val size: Int
    abstract val isEmpty: Boolean

    operator fun plus(element: @UnsafeVariance A): LeftistHeap<A>
            = merge(this, LeftistHeap(element, comparator))

    abstract fun tail(): Result<LeftistHeap<A>>
    abstract fun get(index: Int): Result<A>
    abstract fun pop(): Option<Pair<A, LeftistHeap<A>>>

    fun toList(): FList<A>
            = foldLeft(FList<A>()) { lst -> { a -> lst.cons(a) } }.reverse()

    fun <A, S, B> unfold(z: S,
                         getNext: (S) -> Option<Pair<A, S>>,
                         identity: B,
                         f: (B) -> (A) -> B): B {
        tailrec fun unfold(acc: B, z: S): B {
            val next = getNext(z)
            return when (next) {
                is Option.None -> acc
                is Option.Some ->
                    unfold(f(acc)(next.value.first), next.value.second)
            }
        }
        return unfold(identity, z)
    }

    fun <B> foldLeft(identity: B, f: (B) -> (A) -> B): B
            = unfold(this, { it.pop() }, identity, f)


    internal class Empty<out A>(
            override val comparator: Result<Comparator<@UnsafeVariance A>>
            = Result.Empty): LeftistHeap<A>() {

        override val isEmpty: Boolean = true

        override val left: Result<LeftistHeap<A>> = Result(this)

        override val right: Result<LeftistHeap<A>> = Result(this)

        override val head: Result<A>
                = Result.failure("head() called on empty heap")

        override val rank: Int = 0

        override val size: Int = 0

        override fun tail(): Result<LeftistHeap<A>>
                = Result.failure("tail() called on empty heap")

        override fun get(index: Int): Result<A>
                = Result.failure(NoSuchElementException("Index out of bounds"))

        override fun pop(): Option<Pair<A, LeftistHeap<A>>> = Option()

    }

    internal class H<out A>(
        override val rank: Int,
        private val lft: LeftistHeap<A>,
        private val hd: A,
        private val rght: LeftistHeap<A>,
        override val comparator: Result<Comparator<@UnsafeVariance A>>
            = lft.comparator.orElse { rght.comparator }): LeftistHeap<A>() {

        override val isEmpty: Boolean = false

        override val size: Int = lft.size + rght.size + 1

        override val left: Result<LeftistHeap<A>> = Result(lft)

        override val head: Result<A> = Result(hd)

        override val right: Result<LeftistHeap<A>> = Result(rght)

        override fun tail(): Result<LeftistHeap<A>> = Result(merge(lft, rght))


        override fun get(index: Int): Result<A> = when(index) {
            0 -> Result(hd)
            else -> tail().flatMap { it.get(index - 1) }
        }

        override fun pop(): Option<Pair<A, LeftistHeap<A>>>
                = Option(Pair(hd, merge(lft, rght)))

    }

    companion object {

        operator fun <A: Comparable<A>> invoke(): LeftistHeap<A> = Empty()

        operator fun <A> invoke(comparator: Comparator<A>): LeftistHeap<A>
                = Empty(Result(comparator))

        operator fun <A> invoke(comparator: Result<Comparator<A>>): LeftistHeap<A> =
            Empty(comparator)

        operator fun <A> invoke(element: A, comparator: Result<Comparator<A>>):
                LeftistHeap<A> = H(
            1, Empty(comparator), element,
            Empty(comparator), comparator
        )

        operator fun <A: Comparable<A>> invoke(element: A): LeftistHeap<A>
                = invoke(element, Comparator { o1: A, o2: A -> o1.compareTo(o2) })

        operator fun <A> invoke(element: A, comparator: Comparator<A>): LeftistHeap<A>
                = H(
            1, Empty(Result(comparator)), element,
            Empty(Result(comparator)), Result(comparator)
        )

        protected fun <A> merge(head: A,
                                first: LeftistHeap<A>,
                                second: LeftistHeap<A>
        ): LeftistHeap<A> =
                first.comparator.orElse { second.comparator }.let {
                    when {
                        first.rank >= second.rank -> H(second.rank + 1, first, head, second, it)
                        else -> H(first.rank + 1, second, head, first, it)
                    }
                }

        fun <A> merge(first: LeftistHeap<A>,
                      second: LeftistHeap<A>,
                      comparator: Result<Comparator<A>> =
                              first.comparator.orElse { second.comparator }): LeftistHeap<A> =
                first.head.flatMap { fh ->
                    second.head.flatMap { sh ->
                        when {
                            compare(fh, sh, comparator) <= 0 -> first.left.flatMap { fl ->
                                first.right.map { fr ->
                                    merge(
                                        fh,
                                        fl,
                                        merge(fr, second, comparator)
                                    )
                                }
                            }
                            else -> second.left.flatMap { sl ->
                                second.right.map { sr ->
                                    merge(
                                        sh,
                                        sl,
                                        merge(first, sr, comparator)
                                    )
                                }
                            }
                        }
                    }
                }.getOrElse(when (first) {
                    is Empty -> second
                    else -> first
                })

        private fun <A> compare(first: A, second: A, comparator: Result<Comparator<A>>): Int =
                comparator.map { comp ->
                    comp.compare(first, second)
                }.getOrElse((first as Comparable<A>).compareTo(second))

    }
}