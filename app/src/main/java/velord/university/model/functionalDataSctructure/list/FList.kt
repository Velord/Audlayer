package velord.university.model.functionalDataSctructure.list

import android.os.Build
import velord.university.model.functionalDataSctructure.Option
import velord.university.model.functionalDataSctructure.list.FList.Companion.doubleToString
import velord.university.model.functionalDataSctructure.list.FList.Companion.flatten
import velord.university.model.functionalDataSctructure.list.FList.Companion.foldRight
import velord.university.model.functionalDataSctructure.list.FList.Companion.product
import velord.university.model.functionalDataSctructure.list.FList.Companion.triple
import velord.university.model.functionalDataSctructure.result.Result
import velord.university.model.functionalDataSctructure.result.map2
import java.util.concurrent.ExecutionException
import java.util.concurrent.ExecutorService

sealed class FList<out A>{

    abstract val length: Int

    abstract fun isEmpty(): Boolean

    abstract fun lengthMemoized(): Int

    abstract fun setHeadSafe(): Result<A>

    abstract fun tailSafe(): Result<FList<A>>

    abstract fun <B> foldLeft(identity: B, zero: B,
                              f: (B) -> (A) -> B): Pair<B, FList<A>>

    abstract fun <B> foldLeft(identity: B,
                              p: (B) -> Boolean,
                              f: (B) -> (A) -> B): B

    abstract fun forEach(ef: (A) -> Unit)

    abstract fun forEachIndexed(ef: (index: Int, A) -> Unit)

    fun lastSafe(): Result<A> =
            foldLeft(Result()) { _: Result<A> ->  { y: A -> Result(y) } }

    fun lastSafeViaCoFoldRight(): Result<A> =
        this.reverse().coFoldRight(Result()) { x: A ->  { _: Result<A> ->
                Result(x)
            }
        }

    fun lastSafeOld(): Result<A> = lastSafe(this)

    internal tailrec fun lastSafe(list: FList<@UnsafeVariance A>): Result<A> =
            when(list){
                is Cons -> when(list.tail){
                    is Cons -> lastSafe(list.tail)
                    Nil -> Result(list.head)
                    else -> Result(list.head)
                }
                is Nil -> Result()
                else -> Result()
            }

    fun setHeadSafeViaCoFoldRight(): Result<A> =
            coFoldRight(Result()) { x: A -> { _: Result<A> ->
                Result(x)
            } }

    fun setHead(a: @UnsafeVariance A): FList<A> =
            when (this) {
                is Cons -> Cons(a, this.tail)
                Nil -> throw IllegalStateException("setHead called on an empty list")
                else -> throw IllegalStateException("setHead called on an undefined list")
            }

    fun cons(elem: @UnsafeVariance A): FList<A> = Cons(elem, this)

    fun drop(n: Int): FList<A> {
        tailrec fun drop(n: Int, list: FList<A>): FList<A> =
                if (n <= 0 ) list else when(list){
                    is Nil -> list
                    is Cons -> drop(n - 1, list.tail)
                    else -> list
                }
        return drop(n, this)
    }

    fun dropWhile(p: (A) -> Boolean): FList<A> = dropWhile(p, this)

    fun concat(list: FList<@UnsafeVariance A>): FList<A> =
        concatOld(this, list)

    fun reverseOld(): FList<A> {
        tailrec fun <A> reverse(acc: FList<A>, list: FList<A>): FList<A> =
                when(list){
                    Nil -> acc
                    is Cons -> reverse(acc.cons(list.head), list.tail)
                    else -> acc
                }
        return reverse(invoke(), this)
    }

    fun reverse(): FList<A> =
            foldLeft(invoke()){ acc -> { acc.cons(it)}}

    fun init(): FList<A> = reverse().drop(1).reverse()

    fun <B> foldRight(identity: B, f: (A) -> (B) -> B): B =
        foldRight(this, identity, f)

    fun lengthFoldRight(): Int = foldRight(0){ _ -> { it + 1 }}

    fun <B> foldLeft(identity: B, f: (B) -> (A) -> B): B =
        foldLeft(identity, this, f)

    fun length(): Int = foldLeft(0) { { _ -> it + 1} }

    fun <B> foldRightViaFoldLeft(identity: B,
                                 f: (A) -> (B) -> B): B =
            this.reverse().foldLeft(identity){ x -> { y -> f(y)(x)}}

    fun <B> coFoldRight(identity: B, f: (A) -> (B) -> B): B =
        cofoldRight(identity, this.reverse(), identity, f)

    fun <B> mapViaFoldLeft(f: (A) -> B): FList<B> =
            foldLeft(invoke()){ x: FList<B> -> { y: A ->
                Cons(
                    f(y),
                    x
                )
            }
            }.reverse()

    fun <B> mapViaFoldRight(f: (A) -> B): FList<B> =
            coFoldRight(invoke()) { h -> { t: FList<B> ->
                Cons(
                    f(
                        h
                    ), t
                )
            } }

    fun filter(p: (A) -> Boolean): FList<A> =
            coFoldRight(invoke()) { h -> { t: FList<A> -> if (p(h)) Cons(
                h,
                t
            ) else t } }

    fun filterViaFlatMap(p: (A) -> Boolean): FList<A> =
            flatMap{h -> if (p(h)) FList(h) else invoke() }

    fun <B> flatMap(f: (A) -> FList<B>): FList<B> =
        flatten(mapViaFoldLeft(f))

    fun <A1, A2> unzip(f: (A) -> Pair<A1, A2>): Pair<FList<A1>, FList<A2>> =
            this.coFoldRight(Pair(Nil, Nil)) { a ->
                { pairList: Pair<FList<A1>, FList<A2>> ->
                    f(a).let {
                        Pair(pairList.first.cons(it.first),
                                pairList.second.cons(it.second))
                    }
                }
            }


    fun getAtNotOptimizedViaCorecursive(idx: Int): Result<A> {
        tailrec fun <A> getAt(list: Cons<A>, index: Int): Result<A> =
                if (index == 0)
                    Result(list.head)
                else
                    getAt(list.tail as Cons, index - 1)
        return if ((idx < 0) or (idx >= lengthMemoized()))
            Result.failure("Index out of bound")
        else
            getAt(this as Cons, idx)
    }

    fun getAt(idx: Int): Result<A> {
        val p: (Pair<Result<A>, Int>) -> Boolean = { it.second < 0 }
        return Pair<Result<A>, Int>(Result.failure("Index out of bound"), idx)
                .let { identity ->
                    if ((idx < 0) or (idx >= lengthMemoized()))
                        identity
                    else
                        foldLeft(identity, p) { ta:  Pair<Result<A>, Int> ->
                            { a ->
                                if (p(ta))
                                    ta
                                else
                                    Pair(Result(a), ta.second - 1)
                            }
                        }
                }.first
    }

    fun splitPairAt(index: Int): Pair<FList<A>, FList<A>> {
        tailrec fun splitAt(acc: FList<A>,
                            list: FList<A>,
                            i: Int): Pair<FList<A>, FList<A>> =
                when(list){
                    Nil -> Pair(acc.reverse(), list)
                    is Cons ->
                        if (i == 0)
                            Pair(acc.reverse(), list)
                        else
                            splitAt(acc.cons(list.head), list.tail,  i - 1)
                    else -> Pair(acc.reverse(), list)
                }
        return when {
            index < 0 -> splitPairAt(0)
            index > lengthMemoized() -> splitPairAt(lengthMemoized())
            else -> splitAt(Nil, this, this.lengthMemoized() - index)
        }
    }

    fun splitAtViaFoldLeft(index: Int): Pair<FList<A>, FList<A>> {
        val idxValid = when {
            index < 0 -> 0
            index >= lengthMemoized() -> lengthMemoized()
            else -> index
        }
        val identity = Triple(Nil, Nil, idxValid)
        val result =
                foldLeft(identity) { ta: Triple<FList<A>, FList<A>, Int> ->
                    {a: A ->
                        if (ta.third == 0)
                            Triple(ta.first, ta.second.cons(a), ta.third)
                        else
                            Triple(ta.first.cons(a), ta.second, ta.third - 1)
                    }
                }
        return Pair(result.first.reverse(), result.second.reverse())
    }

    fun splitAtViaHelpClass(index: Int): Pair<FList<A>, FList<A>> {
        data class Pair<out A>(val first: FList<A>, val second: Int) {
            override fun equals(other: Any?): Boolean = when {
                other == null -> false
                other.javaClass == this.javaClass ->
                    (other as Pair<A>).second == second
                else -> false
            }
            override fun hashCode(): Int =
                    first.hashCode() + second.hashCode()
        }
        return when {
            index <= 0 -> Pair(Nil, this)
            index >= length -> Pair(this, Nil)
            else -> {
                val identity = Pair(Nil as FList<A>, -1)
                val zero = Pair(this, index)
                val (pair, list) = this.foldLeft(identity, zero) { acc ->
                    { e -> Pair(acc.first.cons(e), acc.second + 1) }
                }
                Pair(pair.first.reverse(), list)
            }
        }
    }

    fun startsWith(sub: FList<@UnsafeVariance A>): Boolean {
        tailrec fun startsWith(list: FList<A>, sub: FList<A>): Boolean =
                when(sub) {
                    Nil -> true
                    is Cons -> when(list) {
                        Nil -> false
                        is Cons ->
                            if (list.head == sub.head)
                                startsWith(list.tail, sub.tail)
                            else
                                false
                        else -> false
                    }
                    else -> true
                }
        return startsWith(this, sub)
    }

    fun hasSubList(sub: FList<@UnsafeVariance A>): Boolean {
        tailrec  fun hasSubList(list: FList<A>, sub: FList<A>): Boolean =
                when(list) {
                    Nil -> sub.isEmpty()
                    is Cons ->
                        if (list.startsWith(sub)) true
                        else hasSubList(list.tail, sub)
                    else -> sub.isEmpty()
                }
        return hasSubList(this, sub)
    }

    fun <B> groupBy(f: (A) -> B): Map<B, FList<A>> =
            coFoldRight(mapOf()) { t ->
                { mt: Map<B, FList<A>> ->
                    f(t).let {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            mt + (it to (mt.getOrDefault(it, Nil)).cons(t))
                        } else {
                            TODO("VERSION.SDK_INT < N")
                        }
                    }
                }
            }

    fun exists(p: (A) -> Boolean): Boolean =
            foldLeft(identity = false, zero = true) { x -> { y: A -> x || p(y) } }.first

    fun forAll(p: (A) -> Boolean): Boolean =
            !exists { !p(it) }

    fun splitListAt(index: Int): FList<FList<A>> {
        tailrec fun splitAt(acc: FList<A>,
                            list: FList<A>,
                            i: Int): FList<FList<A>> =
                when(list){
                    Nil -> FList(acc.reverse(), list)
                    is Cons ->
                        if (i == 0)
                            FList(acc.reverse(), list)
                        else
                            splitAt(acc.cons(list.head), list.tail,  i - 1)
                    else -> FList(acc.reverse(), list)
                }
        return when {
            index < 0 -> splitListAt(0)
            index > lengthMemoized() -> splitListAt(lengthMemoized())
            else -> splitAt(Nil, this, this.lengthMemoized() - index)
        }
    }

    fun divide(depth: Int): FList<FList<A>> {
        tailrec fun divide(list: FList<FList<A>>, depth: Int): FList<FList<A>> =
                when(list) {
                    Nil -> list
                    is Cons ->
                        if (list.head.length() < 2 || depth < 1)
                            list
                        else
                            divide(list.flatMap { x ->
                                x.splitListAt(x.length() / 2) }, depth - 1)
                    else -> list
                }
        return if (this.isEmpty())
            FList(this)
        else
            divide(FList(this), depth)
    }

    fun <B> parFoldLeft(es: ExecutorService,
                        identity: B,
                        f: (B) -> (A) -> B,
                        m: (B) -> (B) -> B): Result<B> =
            try {
                val result: FList<B> = divide(1024)
                        .mapViaFoldLeft { list: FList<A> ->
                            es.submit<B> { list.foldLeft(identity, f) }
                        }.mapViaFoldLeft<B> { fb ->
                            try {
                                fb.get()
                            } catch (e: InterruptedException){
                                throw RuntimeException(e)
                            } catch (e: ExecutionException){
                                throw RuntimeException(e)
                            }
                        }
                Result(result.foldLeft(identity, m))
            } catch (e: Exception){
                Result.failure(e)
            }

    fun <B> parMap(es: ExecutorService, g: (A) -> B): Result<FList<B>> =
            try {
                val divided = this.mapViaFoldLeft { x ->
                    es.submit<B>{ g(x) }
                }.mapViaFoldLeft { fb ->
                    try {
                        fb.get()
                    } catch (e: InterruptedException) {
                        throw RuntimeException(e)
                    } catch (e: ExecutionException) {
                        throw RuntimeException(e)
                    }
                }
                Result(divided)
            } catch (e: Exception) {
                Result.failure(e)
            }

    fun zipWithPosition(): FList<Pair<A, Int>> =
        zipWith(this, range(0, this.lengthMemoized())) { a ->
            { i: Int ->
                Pair(
                    a,
                    i
                )
            }
        }

    abstract class Empty<A>: FList<A>()

    internal object Nil: Empty<Nothing>() {

        override val length: Int = 0

        override fun isEmpty(): Boolean = true

        override fun toString(): String = "[NIL]"

        override fun lengthMemoized(): Int = 0

        override fun setHeadSafe(): Result<Nothing> = Result()

        override fun tailSafe(): Result<FList<Nothing>> = Result.Empty

        override fun <B> foldLeft(
                identity: B, zero: B,
                f: (B) -> (Nothing) -> B): Pair<B, FList<Nothing>> = Pair(identity, Nil)


        override fun <B> foldLeft(identity: B,
                                  p: (B) -> Boolean,
                                  f: (B) -> (Nothing) -> B): B = identity

        override fun forEach(ef: (Nothing) -> Unit) { }

        override fun forEachIndexed(ef: (index: Int, Nothing) -> Unit) { }
    }

    internal class Cons<A>(internal val head: A,
                           internal val tail: FList<A>
    ): FList<A>() {

        override val length: Int = tail.length + 1

        override fun isEmpty(): Boolean = false

        override fun toString(): String = "[${toString("", this)}NIL]"

        private tailrec fun toString(acc: String, list: FList<A>): String =
                when (list) {
                    is Nil -> acc
                    is Cons -> toString("$acc${list.head}, ", list.tail)
                    else -> acc
                }

        override fun lengthMemoized(): Int = length

        override fun setHeadSafe(): Result<A> = Result(head)

        override fun tailSafe(): Result<FList<A>> = Result(tail)

        override fun <B> foldLeft(identity: B,
                                  zero: B,
                                  f: (B) -> (A) -> B): Pair<B, FList<A>> {
            fun <B> foldLeft(acc: B, zero: B, list: FList<A>,
                             f: (B) -> (A) -> B): Pair<B, FList<A>> =
                    when (list) {
                        Nil -> Pair(acc, list)
                        is Cons ->
                            if (acc == zero)
                                Pair(acc, list)
                            else
                                foldLeft(f(acc)(list.head), zero, list.tail, f)
                        else -> Pair(acc, list)
                    }
            return foldLeft(identity, zero, this, f)
        }

        override fun <B> foldLeft(identity: B,
                                  p: (B) -> Boolean,
                                  f: (B) -> (A) -> B): B {
            fun foldLeft(acc: B, list: FList<A>): B =
                    when (list) {
                        Nil -> acc
                        is Cons ->
                            if (p(acc))
                                acc
                            else
                                foldLeft(f(acc)(list.head), list.tail)
                        else -> acc
                    }
            return foldLeft(identity, this)
        }

        override fun forEach(ef: (A) -> Unit) {
            tailrec fun forEach(list: FList<A>) {
                when (list) {
                    is Nil -> {}
                    is Cons -> {
                        ef(list.head)
                        forEach(list.tail)
                    }
                }
            }
            forEach(this)
        }

        override fun forEachIndexed(ef: (index: Int, A) -> Unit) {
            tailrec fun forEachIndexed(index: Int, list: FList<A>) {
                when (list) {
                    is Nil -> {}
                    is Cons -> {
                        ef(index, list.head)
                        forEachIndexed(index + 1, list.tail)
                    }
                }
            }
            forEachIndexed(0, this)
        }
    }

    companion object{
        operator fun <A> invoke(vararg az: A): FList<A> =
                az.foldRight(Nil as FList<A>){ a: A, list: FList<A> ->
                    Cons(a, list)
                }

        fun sumFoldRight(ints: FList<Int>): Int =
            foldRight(ints, 0) { x -> { y -> x + y } }

        fun productFoldRight(ints: FList<Int>): Int =
            foldRight(ints, 1) { x -> { y -> x * y } }

        fun sum(ints: FList<Int>): Int =
                ints.foldLeft(0){ x -> { y -> x + y}}

        fun <A> drop(aList: FList<A>, n: Int): FList<A> {
            tailrec fun drop_(list: FList<A>, n: Int): FList<A> =
                    when(list){
                        is Nil -> list
                        is Cons -> if (n <= 0) list else drop_(list.tail, n - 1)
                        else -> list
                    }
            return drop_(aList, n)
        }

        fun product(ints: FList<Int>): Int =
                ints.foldLeft(1){ x -> { y -> x * y}}

        tailrec fun <A> dropWhile(p: (A) -> Boolean,
                                          list: FList<A>
        ): FList<A> =
                when(list){
                    Nil -> list
                    is Cons -> if (p(list.head)) dropWhile(
                        p,
                        list.tail
                    ) else list
                    else -> list
                }

        fun <A> concatOld(list1: FList<A>, list2: FList<A>): FList<A> =
                when (list1) {
                    Nil -> list2
                    is Cons -> Cons(
                        list1.head,
                        concatOld(list1.tail, list2)
                    )
                    else -> list2
                }

        fun <A> concatViaFoldRight(list1: FList<A>, list2: FList<A>): FList<A> =
            foldRight(list1, list2) { x -> { y -> Cons(x, y) } }

        fun <A> concatViaFoldLeft(list1: FList<A>, list2: FList<A>): FList<A> =
                list1.reverse().foldLeft(list2) { x -> x::cons}

        fun <A, B> foldRight(list: FList<@UnsafeVariance A>,
                             identity: B,
                             f: (A) -> (B) -> B): B =
                when(list){
                    Nil -> identity
                    is Cons -> f(list.head)(
                        foldRight(
                            list.tail,
                            identity,
                            f
                        )
                    )
                    else -> identity
                }

        private tailrec fun <A, B> foldLeft(acc: B,
                                            list: FList<A>,
                                            f: (B) -> (A) -> B): B =
                when (list) {
                    Nil -> acc
                    is Cons -> foldLeft(f(acc)(list.head), list.tail, f)
                    else -> acc
                }

        private tailrec fun <A, B> cofoldRight(acc: B,
                                               list: FList<A>,
                                               identity: B,
                                               f: (A) -> (B) -> B): B =
                when(list){
                    Nil -> acc
                    is Cons -> cofoldRight(
                        f(list.head)(acc),
                        list.tail,
                        identity,
                        f
                    )
                    else -> acc
                }

        fun <A> flatten(list: FList<FList<A>>): FList<A> =
                list.coFoldRight(invoke()){ x -> x::concat}


        fun triple(ints: FList<Int>): FList<Int> =
                ints.reverse().foldLeft(invoke()) { x: FList<Int> ->
                    { y ->
                        x.cons(y * 3)
                    }
                }

        fun doubleToString(list: FList<Double>): FList<String> =
            foldRight(list, invoke()) { x ->
                { y: FList<String> -> y.cons(x.toString()) }
            }

        fun range(start: Int, end: Int): FList<Int> =
            unfold(start) { i ->
                if (i < end)
                    Option(Pair(i, i + 1))
                else
                    Option()
            }

        fun fromSeparated(string: String, separator: String): FList<String>
                = FList(*string.split(separator).toTypedArray())
    }
}

fun <A> flattenResult(list: FList<Result<A>>): FList<A> =
        list.flatMap { ra -> ra.map { FList(it) }.getOrElse(FList()) }

fun <A>  sequence(list: FList<Result<A>>): Result<FList<A>> =
    traverse(list.filter { !it.isEmpty() }) { x -> x }

// very difficult to understanding
fun <A, B> traverse(list: FList<A>, f: (A) -> Result<B>): Result<FList<B>> =
    list.coFoldRight(Result(FList())) { x ->
            { y: Result<FList<B>> ->
                map2(f(x), y) { a -> { b: FList<B> -> b.cons(a) } }
            }
        }

fun <A, B, C> zipWith(list1: FList<A>,
                      list2: FList<B>,
                      f: (A) -> (B) -> C): FList<C> {
    tailrec fun zipWith(acc: FList<C>,
                        list1: FList<A>,
                        list2: FList<B>
    ): FList<C> =
            when(list1){
                FList.Nil -> acc
                is FList.Cons -> when(list2) {
                    FList.Nil -> acc
                    is FList.Cons -> zipWith(acc.cons(f(list1.head)(list2.head)),
                            list1.tail, list2.tail)
                    else -> acc
                }
                else -> acc
            }
    return zipWith(FList(), list1, list2).reverse()
}

fun <A, B, C> product(list1: FList<A>,
                      list2: FList<B>,
                      f: (A) -> (B) -> C): FList<C> =
        list1.flatMap { a -> list2.mapViaFoldLeft { b -> f(a)(b) } }

fun <A, B> unzip(list: FList<Pair<A, B>>): Pair<FList<A>, FList<B>> =
        list.unzip { it }

fun <A, S> unfold(z: S, getNext: (S) -> Option<Pair<A, S>>): FList<A> {
    tailrec fun unfold(acc: FList<A>, z: S): FList<A> {
        return when(val next = getNext(z)) {
            Option.None -> acc
            is Option.Some ->
                unfold(acc.cons(next.value.first), next.value.second)
        }
    }
    return unfold(FList.Nil, z).reverse()
}

fun <A, S> unfoldSafe(z: S,
                      getNext: (S) -> Result<Pair<A, S>>
): Result<FList<A>> {
    tailrec fun unfold(acc: FList<A>, z: S): Result<FList<A>> {
        return when (val next = getNext(z)) {
            Result.Empty -> Result(acc)
            is Result.Failure -> Result.failure(next.exception)
            is Result.Success ->
                unfold(acc.cons(next.value.first), next.value.second)
        }
    }
    return unfold(FList.Nil, z).map(FList<A>::reverse)
}

fun <T> FList<T>.toStandartLibraryMutableList(): MutableList<T> {
    val list = mutableListOf<T>()
    this.forEach { list.add(it) }
    return list
}

private val random = kotlin.random.Random

fun main(){
    val list = FList(1, 2, 3, 4, 5)

    val newList = list.drop(2).setHead(0)
    println(newList)

    val dropList = list.dropWhile{it < 4}
    println(dropList)

    val concatenateList = list.concat(dropList)
    println(concatenateList)

    val reverseList = concatenateList.reverse()
    println(reverseList)

    val initList = reverseList.init()
    println(initList)

    println(foldRight(FList(1, 2, 3), FList()) { x: Int ->
        { y: FList<Int> ->
            y.cons(x)
        }
    })

    val listOflIst = FList(list, newList, dropList)
    val flattenList = flatten(listOflIst)
    println(flattenList)

    val tripleList = triple(dropList)
    println(tripleList)

    val mapList = reverseList.mapViaFoldLeft { it * 10 }
    println(mapList)

    val filteredList = initList.filter { it < 5 }
    println(filteredList)

    val flatMapList = filteredList.flatMap { FList(it, it * 4) }
    println(flatMapList)

    val filteredList2 = initList.filterViaFlatMap { it > 3 }
    println(filteredList2)

    val listForProduct = listOf(-1674383352, -769845693, -926682101, 1103657321, 239272174,
            823877173, -1904631627, -1934839314, 343255155, 2128411895, 86637914, -2089158031, -1493319498,
            255861806, 1091127403, 458268127, 226023601, -2063963997, 1037587748, 484446298, 1383424614, 1059923565,
            1648068317, -1079154188, -680456104, -1550220465, -1103778400, 1459636665, -1990519236, 1042789214,
            2124126202, -1769184014, 2116894960, 327902183, 391015958, 1234060968, -974513748, 2097770639,
            -1403508217, 1836450663, -746747785, -50248006, 1474740580, 1369630019, -1478448677, -383870160,
            -170404839, -1000385505, 1656277979, -1640081922, 374338684, 310148652, -1028340187, 1834735770,
            -319431522, 383878320, -1536482941, 780796489, 1662723299, 844771592, -1817263465, -1454532923,
            -1784590379, 1848473072, -529069113, -286987495, -989642956, 1975795968, 1758702197, -1520239408,
             869819985, 1898799225, -332168333, -935176370, 1336429979, 332628289, 1959359158, -562664580, 1312439162,
            -1534686932, 1498468936, 1296580011, -1074391461, 508154236, -417399677, -1665823311, 1672008715, 1349322155,
            -1181860485, -203956904, 1246759496, -1601328537, -1549670468, 743110905)
    val myListProduct = FList(
        -1674383352, -769845693, -926682101, 1103657321, 239272174,
        823877173, -1904631627, -1934839314, 343255155, 2128411895, 86637914, -2089158031, -1493319498,
        255861806, 1091127403, 458268127, 226023601, -2063963997, 1037587748, 484446298, 1383424614, 1059923565,
        1648068317, -1079154188, -680456104, -1550220465, -1103778400, 1459636665, -1990519236, 1042789214,
        2124126202, -1769184014, 2116894960, 327902183, 391015958, 1234060968, -974513748, 2097770639,
        -1403508217, 1836450663, -746747785, -50248006, 1474740580, 1369630019, -1478448677, -383870160,
        -170404839, -1000385505, 1656277979, -1640081922, 374338684, 310148652, -1028340187, 1834735770,
        -319431522, 383878320, -1536482941, 780796489, 1662723299, 844771592, -1817263465, -1454532923,
        -1784590379, 1848473072, -529069113, -286987495, -989642956, 1975795968, 1758702197, -1520239408,
        869819985, 1898799225, -332168333, -935176370, 1336429979, 332628289, 1959359158, -562664580, 1312439162,
        -1534686932, 1498468936, 1296580011, -1074391461, 508154236, -417399677, -1665823311, 1672008715, 1349322155,
        -1181860485, -203956904, 1246759496, -1601328537, -1549670468, 743110905
    )
    val product = product(myListProduct)
    val productList = listForProduct.fold(1) { x , y -> x * y }
    println(product)
    println(productList)
    val equal = (product - productList) < 0.001
    println(equal)


    val reversedArray = arrayOf(1, 3, 65, 23).toList().reversed().joinToString(", ", "[", ", NIL]")
    val reversedMyList = FList(1, 3, 65, 23).reverse().toString()
    println(reversedArray)
    println(reversedMyList)
    val equalReversed = reversedArray == reversedMyList
    println(equalReversed)

    val doubleArray = arrayOf(1.0, 2.0, 3.0)
    val myListOfDouble = FList(1.0, 2.0, 3.0)
    println(doubleArray)
    println(myListOfDouble)
    val myListOfString = doubleToString(myListOfDouble).toString()
    val doubleArrayString = doubleArray.toList().joinToString(", ", "[", ", NIL]")
    println(myListOfString)
    println(doubleArrayString)
    val equals = myListOfString == doubleArrayString
    println(equals)

    val lastSafeViaFoldRight = flatMapList.lastSafeViaCoFoldRight()
    val lastSafe = flatMapList.lastSafe()
    println(lastSafe)
    println(lastSafeViaFoldRight)
    println(lastSafe == lastSafeViaFoldRight)

    val listForSplit = FList(1, 34, 234, 23, 12, 545, 876, 34, 23)
    val index =
            if (listForSplit.isEmpty()) 0
            else random.nextInt(0, listForSplit.lengthMemoized() - 1)
    val result = listForSplit.splitPairAt(index)
    println(listForSplit.toString())
    println(result)
    println(result.first.concat(result.second).toString())

    val f: (Int) -> Option<Pair<Int, Int>> =
            { it ->
                if (it < 100) Option(Pair(it, it + 1)) else Option()
            }
    val unfolded = unfold(0, f)
    println(unfolded)

    val number = random.nextInt(1, 100)
    val result1 = unfoldSafe(0) {
        if (it < number) Result(Pair(it, it + 1)) else Result()
    }
    val result2 = unfoldSafe(number) {
        if (it > 0) Result(Pair(it - 1, it - 1)) else Result()
    }
    val unfoldSafeEqual = (result1.getOrElse(FList(-1)).toString() ==
            result2.getOrElse(FList(-1)).reverse().toString()) and
            (result1.getOrElse(FList(-1)).foldLeft(0) { a -> { b -> a + b } } ==
                    (0 until number).sum())
    println(number)
    println(result1.getOrElse(FList(-1)))
    println(result2.getOrElse(FList(-1)))
    println(unfoldSafeEqual)
}
