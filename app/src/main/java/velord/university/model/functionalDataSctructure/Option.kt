package velord.university.model.functionalDataSctructure

import velord.university.model.functionalDataSctructure.list.FList
import java.util.*
import kotlin.math.pow

sealed class Option<out A> {

    abstract fun isEmpty(): Boolean

    abstract fun forEach(effect: (A) -> Unit)

    fun getOrElse(default: () -> @UnsafeVariance A): A =
        when (this) {
            None -> default()
            is Some -> value
        }

    fun <B> map(f: (A) -> B): Option<B> =
            when(this){
                None -> None
                is Some -> Some(f(value))
            }

    fun <B> flatMap(f: (A) -> Option<B>): Option<B> =
            when(this){
                None -> None
                is Some ->  map(f).getOrElse { None }
            }

    fun orElse(default: () -> Option<@UnsafeVariance A>): Option<A> =
            map { this }.getOrElse(default)

    fun filter(p: (A) -> Boolean): Option<A> =
            flatMap { x -> if (p(x)) this else None }

    internal object None: Option<Nothing>(){
        override fun isEmpty(): Boolean = true

        override fun toString(): String = "None"

        override fun equals(other: Any?): Boolean = other === None

        override fun hashCode(): Int = 0

        override fun forEach(effect: (Nothing) -> Unit) {}
    }

    internal data class Some<out A>(internal val value: A): Option<A>(){
        override fun isEmpty(): Boolean = false

        override fun forEach(effect: (A) -> Unit) {
            effect(value)
        }
    }

    companion object{
        operator fun <A> invoke(a: A? =  null): Option<A> =
                when(a){
                    null -> None
                    else -> Some(a)
                }
    }
}

fun <A, B> lift(f: (A) -> B): (Option<A>) -> Option<B> = {
    try {
        it.map(f)
    } catch (e: Exception){
        Option()
    }
}

val upperOption: (Option<String>) -> Option<String> =
    lift { it.toUpperCase(Locale.ROOT) }

fun <A, B> hLift(f: (A) -> B): (A) -> Option<B> = {
    try {
        Option(it).map(f)
    } catch (e: Exception) {
        Option()
    }
}

fun <A, B, C> map2(oa: Option<A>,
                   ob: Option<B>,
                   f: (A) -> (B) -> C): Option<C> =
        oa.flatMap { a -> ob.map { b -> f(a)(b) } }

fun <A, B, C, D> map3(oa: Option<A>,
                      ob: Option<B>,
                      oc: Option<C>,
                      f: (A) -> (B) -> (C) -> D): Option<D> =
        oa.flatMap { a ->
            ob.flatMap { b ->
                oc.map { c ->
                    f(a)(b)(c)
                }
            }
        }

fun <A, B, C, D, T, G, H> map6(oa: Option<A>,
                               ob: Option<B>,
                               oc: Option<C>,
                               od: Option<D>,
                               ot: Option<T>,
                               og: Option<G>,
                               f: (A) -> (B) -> (C) -> (D)
                               -> (T) -> (G) -> H): Option<H> =
        oa.flatMap { a ->
            ob.flatMap { b ->
                oc.flatMap { c ->
                    od.flatMap { d ->
                        ot.flatMap { t ->
                            og.map { g ->
                                f(a)(b)(c)(d)(t)(g)
                            }
                        }
                    }
                }
            }
        }

fun <A> sequence(list: FList<Option<A>>): Option<FList<A>> =
        traverse(list) { x -> x }
// very difficult to understanding
fun <A, B> traverse(list: FList<A>, f: (A) -> Option<B>): Option<FList<B>> =
        list.foldRight(Option(FList())) { x ->
            { y: Option<FList<B>> ->
                map2(f(x), y) { a -> { b: FList<B> -> b.cons(a) } }
            }
        }

val parseWithRadix: (Int) -> (String) -> Int = {
    radix -> { string -> Integer.parseInt(string, radix ) }
}

val parseHex: (String) -> Int = parseWithRadix(16)

val mean: (List<Double>) -> Option<Double> = { list ->
    when{
        list.isEmpty() -> Option()
        else -> Option(list.sum() / list.size)
    }
}
//calculates the average
val variance: (List<Double>) -> Option<Double> = { list ->
    mean(list).flatMap { m ->
        mean(list.map { x ->
            (x - m).pow(2.0)
        })
    }
}

fun max(list: List<Int>): Option<Int> = Option(list.max())

fun getDefault(): Int = throw RuntimeException()

fun main() {
    val list = listOf(3, 5, 7, 2, 1)
    val max1 = max(list).getOrElse(::getDefault)
    println(max1)

    val mappedOption = Option(list).getOrElse { listOf() }.map { it + 3 }
    println(mappedOption)

    val flatMappedOption
            = Option(mappedOption)
            .flatMap { x -> Option(x.map { it * 3 }) }
            .getOrElse { listOf() }
    println(flatMappedOption)

    val orELseOption
            = Option(flatMappedOption).orElse { Option(listOf()) }
    println(orELseOption)

    val int = 45
    val filteredOption = Option(int).filter { it < 10 }
    println(filteredOption)

    val varianced = variance(listOf(1.0, 5.0, 6.0))
    println(varianced)

    val upperOption = upperOption(Option("fggs"))
    println(upperOption)
}