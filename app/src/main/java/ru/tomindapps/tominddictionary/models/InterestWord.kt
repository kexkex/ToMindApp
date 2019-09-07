package ru.tomindapps.tominddictionary.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity (tableName = "words")
data class InterestWord (
    var interestWord:String,
    var wordDescription:String,
    var date: String,
    var link:String = "",
    @PrimaryKey(autoGenerate = true)
    val idWord:Int = 0){
}

