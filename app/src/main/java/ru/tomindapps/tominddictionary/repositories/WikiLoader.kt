package ru.tomindapps.tominddictionary.repositories

import com.google.gson.JsonParser
import io.github.rybalkinsd.kohttp.ext.asString
import io.github.rybalkinsd.kohttp.ext.httpGet
import ru.tomindapps.tominddictionary.models.WikiWords

object WikiLoader {
    const val WORDS_LIMIT = 5
    const val BASE_URL = "https://ru.wikipedia.org/w/api.php?action=opensearch&search="
    const val OPTIONS = "&prop=info&inprop=url&limit=$WORDS_LIMIT"

    fun searchInWiki(query: String): List<WikiWords>{
        val url = BASE_URL + query + OPTIONS
        val response = url.httpGet()
        return parseJson(response.asString())
    }

    private fun parseJson(string: String?): List<WikiWords> {
        val tempArray = arrayListOf<WikiWords>()

        val data = JsonParser()
            .parse(string)
            .asJsonArray
        val size = data.get(1).asJsonArray.size()
        for (i in 0 until size) {
            val title = data.get(1).asJsonArray.get(i).asString
            val descr = data.get(2).asJsonArray.get(i).asString
            val link = data.get(3).asJsonArray.get(i).asString
            tempArray.add(WikiWords(title, descr, link))
        }
        return tempArray
    }
}