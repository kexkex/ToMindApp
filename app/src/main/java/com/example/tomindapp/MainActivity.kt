package com.example.tomindapp

import android.app.SearchManager
import android.content.ContentValues
import android.content.Context
import android.content.DialogInterface
import android.content.Intent

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.DatePicker
import android.widget.SearchView
import android.widget.Toast
import android.widget.Toolbar
import com.javasampleapproach.kotlin.sqlite.DbManager
import kotlinx.android.synthetic.main.activity_edit.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.item_container.view.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity(),WordsAdapter.MyAdapterListener {

    override fun onWikiClicked(position: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onMessageRowClicked(position: Int) {
        editWord(WordFactory.getWord(position))
    }

    override fun onRowLongClicked(position: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var list: ArrayList<InterestWord>
    private lateinit var sortOrder:String
    val ORDER_BY_DATE = "Date"
    val ORDER_BY_TITLE = "Title DESC"




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sortOrder=ORDER_BY_DATE


        loadListOfWords()
        list=WordFactory.getList()


        viewManager = LinearLayoutManager(this)
        viewAdapter = WordsAdapter(WordFactory.getList(),this)
        recyclerView = findViewById<RecyclerView>(R.id.rvWords).apply {
            layoutManager = viewManager
            adapter = viewAdapter
        }

        fillList()


    }

    fun getCurrentDate():String{

        val currentDate = Date()
        // Форматирование времени как "день.месяц.год"
        val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        val dateText = dateFormat.format(currentDate)
        // Форматирование времени как "часы:минуты:секунды"
        val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        val timeText = timeFormat.format(currentDate)


        return "$dateText $timeText"
    }



    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        when (item!!.itemId){
            R.id.menuSortAlphabet -> sortOrder=ORDER_BY_TITLE

            R.id.menuSortDate -> sortOrder=ORDER_BY_DATE
        }
        loadListOfWords()
        viewAdapter.notifyDataSetChanged()

        return super.onOptionsItemSelected(item)
    }



    private fun sortList(arrayList: ArrayList<InterestWord>,sortType:String):ArrayList<InterestWord>{

         var arrSorted=arrayList
         when(sortType){
             "Alphabet"->{

                fun selector(a:InterestWord):String=a.interestWord
                arrayList.sortBy { selector(it) }


                }

            "Date"->{
                fun selector(a:InterestWord):String=a.date
                arrayList.sortBy { selector(it) }

            }
        }
        arrSorted=arrayList
        WordFactory.updateWords(arrayList)
        viewAdapter.notifyDataSetChanged()
        return arrSorted






    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        //Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        val sv=menu.findItem(R.id.app_bar_search).actionView as SearchView
        val sm=getSystemService(Context.SEARCH_SERVICE) as SearchManager
        sv.setSearchableInfo(sm.getSearchableInfo(componentName))
        sv.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query!=null) {
                    searchInListOfWords("%"+query+"%")
                    viewAdapter.notifyDataSetChanged()
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText!=null) {
                    searchInListOfWords("%"+newText!!+"%")
                    viewAdapter.notifyDataSetChanged()
                }
                return false

            }
        })
        return super.onCreateOptionsMenu(menu)
    }

    fun updateDbFromList (arrayList: ArrayList<InterestWord>){

        var dbManager=DbManager(this)
        for (word in arrayList) {
            var selectionArs = arrayOf(word.idWord.toString())
            var values=ContentValues()
            values.put("Title",word.interestWord)
            values.put("Content",word.wordDescription)
            values.put("Date",word.date)
            val mID = dbManager.update(values, "Id=?", selectionArs)

            /*if (mID > 0) {
                Toast.makeText(this, "Add note successfully!", Toast.LENGTH_LONG).show()
                finish()
            } else {
                Toast.makeText(this, "Fail to add note!", Toast.LENGTH_LONG).show()
            }*/
        }
    }

    fun setRecyclerViewItemTouchListener(){
        val itemTouchCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT){
            override fun onMove(p0: RecyclerView, p1: RecyclerView.ViewHolder, p2: RecyclerView.ViewHolder): Boolean {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onSwiped(p0: RecyclerView.ViewHolder, p1: Int) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

        }

        val itemTouchHelper = ItemTouchHelper(itemTouchCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }



    fun fabClick(view: View){
        val intent = Intent(this, EditActivity::class.java)
        startActivity(intent)


    }

    override fun onResume() {
        loadListOfWords()
        viewAdapter.notifyDataSetChanged()
        super.onResume()
    }

    fun reloadListofWords(arrayList: ArrayList<InterestWord>){

    }

    private fun searchInListOfWords(s:String){
        var dbManager=DbManager(this)
        var dbCursor = dbManager.querySearch(s)
        WordFactory.clearList()


        if (dbCursor.moveToLast()) {

            do {
                val id = dbCursor.getInt(dbCursor.getColumnIndex("Id"))
                val title = dbCursor.getString(dbCursor.getColumnIndex("Title"))
                val content = dbCursor.getString(dbCursor.getColumnIndex("Content"))
                val date = dbCursor.getString(dbCursor.getColumnIndex("Date"))


                WordFactory.addWords(InterestWord(id, title, content, date))

            } while (dbCursor.moveToPrevious())
        }

    }

    private fun loadListOfWords(){
        var dbManager=DbManager(this)
        var dbCursor = dbManager.queryAllByOrder(sortOrder)

        WordFactory.clearList()


        if (dbCursor.moveToLast()) {

            do {
                val id = dbCursor.getInt(dbCursor.getColumnIndex("Id"))
                val title = dbCursor.getString(dbCursor.getColumnIndex("Title"))
                val content = dbCursor.getString(dbCursor.getColumnIndex("Content"))
                val date = dbCursor.getString(dbCursor.getColumnIndex("Date"))


                    WordFactory.addWords(InterestWord(id, title, content, date))

            } while (dbCursor.moveToPrevious())
        }



    }



    fun editWord(word: InterestWord) {
        var intent = Intent(this, EditActivity::class.java)
        intent.putExtra("MainActId", word.idWord)
        intent.putExtra("MainActTitle", word.interestWord)
        intent.putExtra("MainActContent", word.wordDescription)
        startActivity(intent)
    }

    fun fillList(){
        var dbManager=DbManager(this)
        var values=ContentValues()

        values.put("Title", "ываыва")
        values.put("Content", "ываыва")
        values.put("Date", getCurrentDate())
        var mID = dbManager.insert(values)

        values.put("Title", "чсмчмва")
        values.put("Content", "мчвмч")
        values.put("Date", getCurrentDate())
        mID = dbManager.insert(values)

        values.put("Title", "цукцук")
        values.put("Content", "alshd")
        values.put("Date", getCurrentDate())
        mID = dbManager.insert(values)

        values.put("Title", "еуцуецу")
        values.put("Content", "alshd")
        values.put("Date", getCurrentDate())
        mID = dbManager.insert(values)

        values.put("Title", "афывфцуф")
        values.put("Content", "alshd")
        values.put("Date", getCurrentDate())
        mID = dbManager.insert(values)










    }



}
