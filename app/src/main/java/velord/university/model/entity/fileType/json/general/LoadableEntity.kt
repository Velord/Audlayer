package velord.university.model.entity.fileType.json.general

data class Loadable <T> (
    private val loadF: suspend () -> T
) {

    private var entity: T? = null

    fun isInitialized(): Boolean = (entity == null).not()

    suspend fun load(): T {
        entity = loadF()
        return entity!!
    }

    suspend fun get(): T =
        if (isInitialized().not()) load()
        else entity!!

    suspend fun rearwardThenGet (
        newLoadF: suspend () -> T
    ): Loadable<T> = Loadable { newLoadF() }.also { get() }

    fun rearward (
        newLoadF: suspend () -> T
    ): Loadable<T> = Loadable { newLoadF() }

    fun getUnsafe(): T = entity!!
}