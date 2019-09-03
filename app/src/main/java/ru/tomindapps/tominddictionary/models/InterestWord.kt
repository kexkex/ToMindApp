package ru.tomindapps.tominddictionary.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity (tableName = "words")
data class InterestWord (
    var interestWord:String,
    var wordDescription:String,
    var date: String,
    var link:String = "",
    @PrimaryKey
    val idWord:Int){

    companion object{
        var lastId = 0
        fun createWord(title: String, descr: String, date: String, link: String): InterestWord{
            lastId++
            return InterestWord(title, descr, date, link, lastId)
        }
    }
}

