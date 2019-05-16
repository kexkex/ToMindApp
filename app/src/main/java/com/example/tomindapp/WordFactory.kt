package com.example.tomindapp

object WordFactory {

    private var listOfWords = arrayListOf<InterestWord>()

    fun addWords(word: InterestWord){
        listOfWords.add(word)
    }

    fun getList():ArrayList<InterestWord>{
        return listOfWords
    }

    fun getWord(id:Int):InterestWord{
        return listOfWords[id]
    }

    fun generateWordId ():Int{
        return listOfWords.size
    }

    fun removeWord (id:Int) {
        if (!listOfWords.isEmpty()&&id<(listOfWords.size)) {
                listOfWords.removeAt(id)
            for (i in 0..(listOfWords.size-1)){
                listOfWords[i].idWord=i}
            }
    }

    fun updateWord (id: Int, word: InterestWord){
        listOfWords[id]=word
    }

    fun clearList(){
        listOfWords.clear()
    }

    fun updateWords(arr:ArrayList<InterestWord>){
        listOfWords=arr

    }


}