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
        viewModelScope.launch(Dispatchers.IO){
            words.postValue(wordsRepo.findWordsInDb(query))
        }
    }

    fun getWordsByOrder(orderType: OrderType = OrderType.TITLE){
        when (orderType){
            OrderType.DATE -> getWordsByDate()
            OrderType.TITLE -> getWordsByTitle()
        }
    }

    private fun getWordsByTitle(){
        viewModelScope.launch(Dispatchers.IO){
            words.postValue(wordsRepo.getWords().sortedBy { it.interestWord })
        }
    }

    private fun getWordsByDate(){
        viewModelScope.launch(Dispatchers.IO){
            words.postValue(wordsRepo.getWords().sortedBy { it.date })
        }
    }

}

enum class OrderType {
    DATE,
    TITLE
}