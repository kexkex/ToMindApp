package ru.tomindapps.tominddictionary.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.tomindapps.tominddictionary.db.AppDb
import ru.tomindapps.tominddictionary.models.InterestWord
import ru.tomindapps.tominddictionary.models.WikiWords
import ru.tomindapps.tominddictionary.repositories.WikiRepo
import ru.tomindapps.tominddictionary.repositories.WordsRepo

class EditActivityViewModel (app: Application) : AndroidViewModel(app){
    private val wordsRepo: WordsRepo
    private val wikiRepo: WikiRepo
    val wikiWords: MutableLiveData<List<WikiWords>> = MutableLiveData()
    val word: MutableLiveData<InterestWord> = MutableLiveData()
    init {
        val dao = AppDb.getInstance(app).wordsDao()
        wordsRepo = WordsRepo(dao)
        wikiRepo = WikiRepo()
    }

    fun findInWiki(query: String){
        viewModelScope.launch(Dispatchers.IO){
            wikiWords.postValue(wikiRepo.loadWords(query))
        }
    }

    fun saveWord(word: InterestWord){
        viewModelScope.launch(Dispatchers.IO) {
            wordsRepo.saveWordToDb(word)
        }
    }

    fun deleteWord(word: InterestWord){
        viewModelScope.launch(Dispatchers.IO){
            wordsRepo.deleteWordFromDb(word)
        }
    }

    fun selectWordById(id: Int) {
        viewModelScope.launch(Dispatchers.IO){
            word.postValue(wordsRepo.getWordFromDb(id))
        }
    }


}