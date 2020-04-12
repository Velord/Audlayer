package velord.university.model.functionalDataSctructure

import velord.university.model.functionalDataSctructure.list.FList


sealed class Either<E, out A> {

    abstract fun <B> map(f: (A) -> B): Either<E, B>

    abstract fun <B> flatMap(f: (A) -> Either<@UnsafeVariance E, B>): Either<E, B>
    
    fun getOrElse(default: () -> @UnsafeVariance A): A =
            when(this) {
                is Left -> default()
                is Right -> this.value
            }

    fun orElse(default: () -> Either<E, @UnsafeVariance A>): Either<E, A> =
            when(this){
                is Left -> default()
                is Right -> this
            }

    fun orElse2(defaultValue: () -> Either<E, @UnsafeVariance A>): Either<E, A> =
            map { this }.getOrElse(defaultValue)

    internal class Left<E , out A>(internal val value: E): Either<E, A>(){
        override fun toString(): String = "Left($value)"

        override fun <B> map(f: (A) -> B): Either<E, B> = Left(value)

        override fun <B> flatMap(f: (A) -> Either<@UnsafeVariance E, B>): Either<E, B>
                = Left(value)
    }

    internal class Right<E, out A>(internal val value: A): Either<E, A>(){
        override fun toString(): String  = "Right($value)"

        override fun <B> map(f: (A) -> B): Either<E, B> = Right(f(value))

        override fun <B> flatMap(f: (A) -> Either<@UnsafeVariance E, B>): Either<E, B>
                = f(value)
    }

    companion object{
        fun <E, B> left(value: E): Either<E, B> = Left(value)

        fun <E, B> right(value: B): Either<E, B> = Right(value)
    }
}

fun <A: Comparable<A>> max(list: FList<A>): Either<String, A> =
        when(list) {
            is FList.Nil -> Either.left("max called on empty list")
            is FList.Cons -> Either.right(list.foldLeft(list.head) { x ->
                { y ->
                    if (x.compareTo(y) == 1) x else y
                }
            })
            else -> Either.left("max called on undefined list")
        }

fun main() {
    val list = FList(21, 9, 13, 1, 3, 4, 7)
    val maxElement = max(list)
    println(maxElement)

    val mapped = maxElement.map { it + 3 }
    println(mapped)

    val flatMapped
            = mapped.flatMap { Either.right<String, Int>(it * 3) }
    println(flatMapped)

}