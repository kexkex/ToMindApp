package ru.tomindapps.tominddictionary.repositories

import androidx.annotation.WorkerThread
import ru.tomindapps.tominddictionary.db.WordsDao
import ru.tomindapps.tominddictionary.models.InterestWord

class WordsRepo (val wordsDao: WordsDao) {


    @WorkerThread
    suspend fun getWords(): List<InterestWord>{
        return wordsDao.selectAll()
    }

    @WorkerThread
    suspend fun findWordsInDb(query: String): List<InterestWord>{
        return wordsDao.selectByQuery(query)
    }

    @WorkerThread
    suspend fun saveWordToDb(word: InterestWord){
        for (w in getWords()){}
        wordsDao.insert(word)
    }

    @WorkerThread
    suspend fun deleteWordFromDb(word: InterestWord){
        wordsDao.delete(word)
    }

    @WorkerThread
    suspend fun getWordFromDb(id: Int): InterestWord {
        return wordsDao.selectById(id)
    }

}