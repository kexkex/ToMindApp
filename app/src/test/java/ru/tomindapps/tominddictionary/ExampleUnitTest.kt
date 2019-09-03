package ru.tomindapps.tominddictionary

import org.junit.Test

import org.junit.Assert.*
import org.junit.Before
import ru.tomindapps.tominddictionary.repositories.JsonWikiParse
import ru.tomindapps.tominddictionary.repositories.WikiLoader

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        val a = WikiLoader.searchInWiki("фотон")
        println(a[0].link)
    }
}
