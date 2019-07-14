package ru.tomindapps.tominddictionary

import android.content.ContentValues
import android.content.Context
import android.content.SearchRecentSuggestionsProvider
import android.database.Cursor
import com.javasampleapproach.kotlin.sqlite.DbManager

class DB {

    lateinit var dbManager:DbManager
    lateinit var context:Context



    companion object {
        val instance = DB()
    }

    fun create(context: Context){
        this.context = context
        dbManager = DbManager(this.context)
    }

    fun addWordInDB(values: ContentValues): Long {
        return dbManager.insert(values)
    }

    fun updateWordInDb(values: ContentValues, id:Int): Int {
        var selectionArs = arrayOf(id.toString())
        return dbManager.update(values, "Id=?", selectionArs)
    }

    fun searchInDB(string: String):ArrayList<InterestWord>{
        return loadlist(dbManager.querySearch(string))
    }

    fun loadAllFromDB(order:String):ArrayList<InterestWord>{
        return loadlist(dbManager.queryAllByOrder(order))
    }

    private fun loadlist(dbCursor:Cursor):ArrayList<InterestWord>{
        var wordList = arrayListOf<InterestWord>()
        wordList.clear()
        if (dbCursor.moveToLast()) {

            do {
                val id = dbCursor.getInt(dbCursor.getColumnIndex("Id"))
                val title = dbCursor.getString(dbCursor.getColumnIndex("Title"))
                val content = dbCursor.getString(dbCursor.getColumnIndex("Content"))
                val date = dbCursor.getString(dbCursor.getColumnIndex("Date"))
                val link = dbCursor.getString(dbCursor.getColumnIndex("Link"))

                wordList.add(
                    InterestWord(
                        id,
                        title,
                        content,
                        date,
                        link
                    )
                )

            } while (dbCursor.moveToPrevious())
        }

        return wordList
    }

}