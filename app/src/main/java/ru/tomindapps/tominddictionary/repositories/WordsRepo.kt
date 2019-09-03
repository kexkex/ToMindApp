package ru.tomindapps.tominddictionary.repositories

import android.content.Context
import androidx.annotation.WorkerThread
import ru.tomindapps.tominddictionary.db.WordsDao
import ru.tomindapps.tominddictionary.models.InterestWord

class WordsRepo (val wordsDao: WordsDao) {

    var words = arrayListOf<InterestWord>()

    @WorkerThread
    suspend fun getWords(): List<InterestWord>{
        return wordsDao.selectAll()
    }

    @WorkerThread
    suspend fun getWordsFromDb(sortOrder: String): List<InterestWord>{
        return wordsDao.selectByOrder(sortOrder)
    }

    @WorkerThread
    suspend fun saveWordToDb(word: InterestWord){
        wordsDao.insert(word)
        if (!words.contains(word)) {
            words.add(word)
        } else {
            val pos = words.indexOf(word)
            words[pos] = word
        }
    }

    @WorkerThread
    suspend fun deleteWordFromDb(word: InterestWord){
        wordsDao.delete(word)
        words.remove(word)
    }

    fun getWordFromDb(id: Int): InterestWord {
        return wordsDao.selectById(id)
    }

}