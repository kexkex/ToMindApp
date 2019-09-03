package ru.tomindapps.tominddictionary.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.tomindapps.tominddictionary.db.AppDb
import ru.tomindapps.tominddictionary.models.InterestWord
import ru.tomindapps.tominddictionary.repositories.WordsRepo

class MainActivityViewModel(app: Application) : AndroidViewModel(app){

    private val wordsRepo: WordsRepo
    val words: MutableLiveData<List<InterestWord>> = MutableLiveData()
    init {
        val dao = AppDb.getInstance(app).wordsDao()
        wordsRepo = WordsRepo(dao)
    }

    fun findWords(query: String){
        words.value?.filter { searchWords(it, query) }

    }

    fun getWordsByOrder(sortOrder: String){
        words.value
    }

    private fun searchWords(interestWord: InterestWord, query: String): Boolean {
        return (interestWord.interestWord.contains(query,true))
    }

    fun getWords(){
        viewModelScope.launch(Dispatchers.IO){
            words.postValue(wordsRepo.getWords().sortedBy { it.interestWord })
            //wordsRepo.words = (words.value as ArrayList<InterestWord>?)!!
        }
    }

    fun saveWord(word: InterestWord){
        viewModelScope.launch(Dispatchers.IO) {
            wordsRepo.saveWordToDb(word)
            words.value = wordsRepo.words
        }
    }

    fun deleteWord(word: InterestWord){
        viewModelScope.launch(Dispatchers.IO){
            wordsRepo.deleteWordFromDb(word)
            words.value = wordsRepo.words
        }
    }
}