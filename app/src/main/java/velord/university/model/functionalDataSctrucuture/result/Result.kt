package util.functionalFrogrammingType.result

import util.functionalFrogrammingType.Option
import java.io.Serializable

fun <A, B> lift(f: (A) -> B): (Result<A>) -> Result<B> = { it.map(f) }

fun <A, B, C> lift2(f: (A) -> (B) -> C
): (Result<A>) -> (Result<B>) -> Result<C> =
        { a -> { b -> a.map(f).flatMap { b.map(it) } } }


fun <A, B, C, D> lift3(f: (A) -> (B) -> (C) -> D
): (Result<A>) -> (Result<B>) -> (Result<C>) -> Result<D> =
        { a -> { b -> { c ->
            a.map(f).flatMap { b.map(it)}.flatMap { c.map(it) } } }
        }

fun <A, B, C, D, F> lift4(f: (A) -> (B) -> (C) -> (D) -> F
): (Result<A>) -> (Result<B>) -> (Result<C>) -> (Result<D>) -> Result<F> =
        { a -> { b -> { c -> { d ->
            a.map(f).flatMap { b.map(it)}.flatMap { c.map(it) }.flatMap { d.map(it) } } } }
        }

fun <A, B, C> map2(oa: Result<A>,
                   ob: Result<B>,
                   f: (A) -> (B) -> C): Result<C> =
        oa.flatMap { a ->  ob.map { b -> f(a)(b) } }

fun <A, B, C, D> map3(oa: Result<A>,
                      ob: Result<B>,
                      oc: Result<C>,
                      f: (A) -> (B) -> (C) -> D): Result<D> =
        oa.flatMap { a ->  ob.flatMap { b -> oc.map { c ->  f(a)(b)(c) } } }

fun <A, B, C, D, F> map3(oa: Result<A>,
                         ob: Result<B>,
                         oc: Result<C>,
                         od: Result<D>,
                         f: (A) -> (B) -> (C) -> (D) -> F): Result<F> =
        oa.flatMap { a ->  ob.flatMap { b -> oc.flatMap { c ->
            od.map { d -> f(a)(b)(c)(d) } } } }

sealed class Result<out A>: Serializable {

    abstract fun mapEmpty(): Result<Any>

    abstract  fun <B> map(f: (A) -> B): Result<B>

    abstract fun <B> flatMap(f: (A) -> Result<B>): Result<B>

    abstract fun toOption(): Option<A>

    abstract fun isEmpty(): Boolean

    abstract fun mapFailure(message: String): Result<A>

    abstract fun forEach(onSuccess: (A) -> Unit = {},
                         onFailure: (RuntimeException) -> Unit = {},
                         onEmpty: () -> Unit = {})

    fun getOrElse(defaultValue: @UnsafeVariance A): A =
            when(this) {
                is Success -> this.value
                else -> defaultValue
            }

    fun orElse(defaultValue: () -> Result<@UnsafeVariance A>): Result<A> =
            when(this){
                is Success -> this
                else -> try {
                    defaultValue()
                } catch (e: RuntimeException) {
                    failure<A>(e)
                } catch (e: Exception) {
                    failure<A>(RuntimeException(e))
                }
            }

    fun filter(p: (A) -> Boolean): Result<A> =
            flatMap {
                if (p(it))
                    this
                else
                    failure("Condition not matched")
            }

    fun filter(message: String, p: (A) -> Boolean): Result<A> =
            flatMap {
                if (p(it))
                    this
                else
                    failure(message)
            }

    fun exists(p: (A) -> Boolean): Boolean =
            map(p).getOrElse(false)

    internal class Failure<out A>(
            internal val exception: RuntimeException): Result<A>() {

        override fun toString(): String = "Failure: ${exception.message}"

        override fun <B> map(f: (A) -> B): Result<B> = Failure(exception)

        override fun <B> flatMap(f: (A) -> Result<B>): Result<B> =
            Failure(exception)

        override fun toOption(): Option<A> = Option()

        override fun mapFailure(message: String): Result<A> =
            Failure(RuntimeException(message, exception))

        override fun forEach(onSuccess: (A) -> Unit,
                             onFailure: (RuntimeException) -> Unit,
                             onEmpty: () -> Unit) = onFailure(exception)

        override fun isEmpty(): Boolean = false

        override fun mapEmpty(): Result<Any> =
            failure(this.exception)
    }

    internal class Success<out A>(internal val value: A): Result<A>() {

        override fun toString(): String = "Success: ($value)"

        override fun <B> map(f: (A) -> B): Result<B> =
                try {
                    Success(f(value))
                } catch (e: RuntimeException){
                    Failure(e)
                } catch (e: Exception){
                    Failure(RuntimeException(e))
                }

        override fun <B> flatMap(f: (A) -> Result<B>): Result<B> =
                try {
                    f(value)
                } catch (e: RuntimeException){
                    Failure(e)
                } catch (e: Exception){
                    Failure(IllegalStateException(e))
                }

        override fun toOption(): Option<A> = Option(value)

        override fun mapFailure(message: String): Result<A> = this

        override fun forEach(onSuccess: (A) -> Unit,
                             onFailure: (RuntimeException) -> Unit,
                             onEmpty: () -> Unit) = onSuccess(value)

        override fun isEmpty(): Boolean = false

        override fun mapEmpty(): Result<Any> = failure("not empty")
    }

    internal object Empty: Result<Nothing>(){
        override fun <B> map(f: (Nothing) -> B): Result<B> = Empty

        override fun <B> flatMap(f: (Nothing) -> Result<B>): Result<B> =
            Empty

        override fun toString(): String = "Empty"

        override fun toOption(): Option<Nothing> = Option()

        override fun mapFailure(message: String): Result<Nothing> = this

        override fun forEach(onSuccess: (Nothing) -> Unit,
                             onFailure: (RuntimeException) -> Unit,
                             onEmpty: () -> Unit) = onEmpty()

        override fun isEmpty(): Boolean = true

        override fun mapEmpty(): Result<Any> = Result(Any())
    }

    companion object{
        operator fun <A> invoke(a: A? = null): Result<A> =
                when(a){
                    null -> Failure(NullPointerException())
                    else -> Success(a)
                }

        operator fun <A> invoke(): Result<A> = Empty

        operator fun <A> invoke(a: A? = null, message: String): Result<A> =
                when(a){
                    null -> Failure(NullPointerException(message))
                    else -> Success(a)
                }

        operator fun <A> invoke(a: A? = null,
                                p: (A) -> Boolean): Result<A> =
                when(a){
                    null -> Failure(NullPointerException())
                    else -> when{
                        p(a) -> Success(a)
                        else -> Empty
                    }
                }

        operator fun <A> invoke(a: A? = null,
                                message: String,
                                p: (A) -> Boolean): Result<A> =
                when(a){
                    null -> Failure(NullPointerException(message))
                    else -> when{
                        p(a) -> Success(a)
                        else -> Failure(
                            IllegalArgumentException(
                                "Argument: ($a) does not match condition: $message"
                            )
                        )
                    }
                }


        suspend fun <A> ofAsync(f: suspend () -> A): Result<A> =
            try {
                Result(f())
            } catch (e: RuntimeException) {
                failure(e)
            } catch (e: Exception) {
                failure(e)
            }

        fun <A> of(f: () -> A): Result<A> =
                try {
                    Result(f())
                } catch (e: RuntimeException) {
                    failure(e)
                } catch (e: Exception) {
                    failure(e)
                }

        fun <A> of(predicate: (A) -> Boolean,
                   value: A,
                   message: String): Result<A> =
                try {
                    if (predicate(value))
                        Result(value)
                    else
                        failure("Assertion failed for value $value with message: $message")
                } catch (e: Exception) {
                    failure(
                        IllegalStateException(
                            "Exception while validating $value",
                            e
                        )
                    )
                }

        fun <A> failure(message: String): Result<A> =
            Failure(IllegalStateException(message))

        fun <A> failure(exception: RuntimeException): Result<A> =
            Failure(exception)

        fun <A> failure(exception: Exception): Result<A> =
            Failure(IllegalStateException(exception))

    }
}