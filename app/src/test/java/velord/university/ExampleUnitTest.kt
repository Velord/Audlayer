package velord.university

import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {

    @Test
    fun addition_isCorrect() {
        var s = "!!!Hello world!!!"

        s = s.substring(3).replace('!', '*')

        println(s)
    }
}
