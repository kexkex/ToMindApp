package ru.tomindapps.tominddictionary.db

import androidx.room.*
import ru.tomindapps.tominddictionary.models.InterestWord

@Dao
interface WordsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(word: InterestWord)

    @Delete
    fun delete(word: InterestWord)

    @Update
    fun update(word: InterestWord)

    @Query ("Select * from words")
    fun selectAll(): List<InterestWord>

    @Query ("Select * from words order by :sortOrder")
    fun selectByOrder(sortOrder: String): List<InterestWord>

    @Query ("Select * from words where idWord like :id")
    fun selectById(id: Int): InterestWord
}