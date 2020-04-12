package velord.university.model.functionalDataSctructure

import velord.university.model.functionalDataSctructure.list.FList
import velord.university.model.functionalDataSctructure.list.traverse
import velord.university.model.functionalDataSctructure.result.Result
import velord.university.model.functionalDataSctructure.result.map2

class Lazy<out A>(function: () -> A): () -> A {
    private val value: A by lazy(function)

    override operator fun invoke(): A = value

    fun <B> map(f: (A) -> B): Lazy<B> = Lazy { f(value) }

    fun <B> flatMap(f: (A) -> Lazy<B>): Lazy<B> = Lazy { f(value)() }


    fun forEach(condition: Boolean,
                ifTrue: (A) -> Unit,
                ifFalse: () -> Unit = {}) =
            if (condition)
                ifTrue(value)
            else
                ifFalse()

    fun forEach(condition: Boolean,
                ifTrue: () -> Unit = {},
                ifFalse: (A) -> Unit) =
            if (condition)
                ifTrue()
            else
                ifFalse(value)

    fun forEach(condition: Boolean,
                ifTrue: (A) -> Unit,
                ifFalse: (A) -> Unit) =
            if (condition)
                ifTrue(value)
            else
                ifFalse(value)

    companion object {
        val constructMessageLazy: (Lazy<String>) ->  (Lazy<String>) -> Lazy<String> =
                { a -> { b -> Lazy { "${a()}, ${b()}!" } } }

        val constructMessage: (String) -> (String) -> String =
                { greetings ->
                    { name ->
                        "$greetings, $name!"
                    }
                }

        val lift2: ((String) ->  (String) -> String) ->
        (Lazy<String>) ->  (Lazy<String>) -> Lazy<String> =
                { f -> { a ->  { b -> Lazy { f(a())(b()) } } } }

    }
}

fun <A> sequenceResult(lst: FList<Lazy<A>>): Lazy<Result<FList<A>>> =
    Lazy {
        velord.university.model.functionalDataSctructure.list.sequence(lst.mapViaFoldLeft {
            Result.invoke(
                it()
            )
        })
    }

fun <A> sequenceResult2(lst: FList<Lazy<A>>): Lazy<Result<FList<A>>> =
    Lazy { traverse(lst) { Result.invoke(it()) } }

fun <A> sequenceResult3(list: FList<Lazy<A>>): Lazy<Result<FList<A>>> =
    Lazy {
        val p =
            { r: Result<FList<A>> -> r.map { false }.getOrElse(true) }
        list.foldLeft(Result(FList()), p) { y: Result<FList<A>> ->
            { x: Lazy<A> ->
                map2(Result.invoke(x), y) { a: Lazy<A> ->
                    { b: FList<A> ->
                        b.cons(a.invoke())
                    }
                }
            }
        }
    }

fun <A> sequence(lst: FList<Lazy<A>>): Lazy<FList<A>> =
    Lazy { lst.mapViaFoldLeft { it() } }

fun <A, B, C> lift2(f: (A) -> (B) -> C):
        (Lazy<A>) -> (Lazy<B>) -> Lazy<C> = { a ->  { b -> Lazy { f(a())(b()) } } }

fun first() = true

fun second(): Boolean = throw IllegalStateException()

fun getFirst() = true

fun getSecond(): Boolean = throw IllegalStateException()

fun or(a: () ->  Boolean, b: () -> Boolean) = if (a()) true else b()

fun and(a: Boolean, b: Boolean) = if (a) b else false