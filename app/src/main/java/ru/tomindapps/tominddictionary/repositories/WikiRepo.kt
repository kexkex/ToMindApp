package ru.tomindapps.tominddictionary.repositories

import android.content.Context
import androidx.annotation.WorkerThread
import ru.tomindapps.tominddictionary.models.InterestWord
import ru.tomindapps.tominddictionary.models.WikiWords

class WikiRepo {

    @WorkerThread
    suspend fun loadWords(query: String): List<WikiWords>{
        return WikiLoader.searchInWiki(query)
    }
}