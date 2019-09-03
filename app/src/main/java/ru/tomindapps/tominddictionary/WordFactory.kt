package ru.tomindapps.tominddictionary

import ru.tomindapps.tominddictionary.models.InterestWord

object WordFactory {

    private var listOfWords = arrayListOf<InterestWord>()

    fun addWords(word: InterestWord){
        listOfWords.add(word)
    }

    fun getList():ArrayList<InterestWord>{
        return listOfWords
    }

    fun getWord(id:Int): InterestWord {
        return listOfWords[id]
    }

    fun clearList(){
        listOfWords.clear()
    }
}