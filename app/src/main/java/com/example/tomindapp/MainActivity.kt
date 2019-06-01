package com.example.tomindapp

import android.app.SearchManager
import android.content.*

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.SearchView
import com.javasampleapproach.kotlin.sqlite.DbManager
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity(),WordsAdapter.MyAdapterListener {

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

        sortOrder = ORDER_BY_DATE

        loadListOfWords()
        list = WordFactory.getList()

        viewManager = LinearLayoutManager(this)
        viewAdapter = WordsAdapter(WordFactory.getList(),this)

        recyclerView = findViewById<RecyclerView>(R.id.rvWords).apply {
            layoutManager = viewManager
            addItemDecoration(object:DividerItemDecoration(this@MainActivity,LinearLayoutManager.VERTICAL){})
            adapter = viewAdapter
        }
    }

    override fun onResume() {
        loadListOfWords()
        viewAdapter.notifyDataSetChanged()

        val clipText = clipBoardIntercept()
        if (clipText!=null) addClipDataToEditAct(clipText)

        super.onResume()
    }

    override fun onEditClicked(position: Int) {
        editWord(WordFactory.getWord(position))
    }

    override fun onWikiClicked(position: Int) {
        openWiki(WordFactory.getList().get(position).link)
    }

    override fun onMessageRowClicked(position: Int) {
        showBottomSheetFragment(WordFactory.getWord(position))

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

    override fun onCreateOptionsMenu(menu: Menu): Boolean {

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


    fun showBottomSheetFragment(word: InterestWord){

        val bsf = BottomSheetFragment()

        var bundle = Bundle()
        bundle.putString("title",word.interestWord)
        bundle.putString("descr",word.wordDescription)
        bundle.putString("link",word.link)

        bsf.arguments=bundle
        bsf.show(supportFragmentManager,bsf.tag)
    }

    fun openWiki(link:String){

        val intent = Intent(this, WebViewActivity::class.java)
        intent.putExtra("Link",link)
        startActivity(intent)
    }

    fun getCurrentDate():String{

        val currentDate = Date()
        val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        val dateText = dateFormat.format(currentDate)
        val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        val timeText = timeFormat.format(currentDate)

        return "$dateText $timeText"
    }

    fun fabClick(view: View){
        val intent = Intent(this, EditActivity::class.java)
        startActivity(intent)
    }

    fun clipBoardIntercept():String?{
        try {

            val clipBoard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clipData = clipBoard.primaryClip

            if (clipData!=null) {
                var text = clipData.getItemAt(0).text
                if (text.length<=20&&text.length>=4) {
                    return text.toString()
                } else return null
            } else return null
        }
        catch (ex:Throwable){
            return null
        }
    }

    fun clearClipBoard(){

        val clipBoard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clipData = ClipData.newPlainText("","")
        clipBoard.primaryClip=clipData
    }

    fun addClipDataToEditAct(s:String){

        val builder = AlertDialog.Builder(this)
        builder.apply {
            setTitle("${s.capitalize()}")
            setMessage("There is word - ${s.capitalize()} in clipboard! Wish you to add it?")
            setCancelable(true)
            setPositiveButton("YES") { dialog, which ->
                dialog!!.cancel()
                clearClipBoard()
                startEditActWithClip(s.capitalize())
            }
            setNegativeButton("NO"
            ) { dialog, which ->
                dialog!!.cancel()
                clearClipBoard()
            }
        }
        val alert = builder.create()
        alert.show()
    }

    fun startEditActWithClip(s:String){

        var intent = Intent(this, EditActivity::class.java)
        intent.putExtra("MainActTitle", s)
        startActivity(intent)
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
                val link = dbCursor.getString(dbCursor.getColumnIndex("Link"))

                WordFactory.addWords(InterestWord(id, title, content, date, link))

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
                val link = dbCursor.getString(dbCursor.getColumnIndex("Link"))

                WordFactory.addWords(InterestWord(id, title, content, date, link))

            } while (dbCursor.moveToPrevious())
        }
    }

    fun editWord(word: InterestWord) {

        var intent = Intent(this, EditActivity::class.java)

        intent.putExtra("MainActId", word.idWord)
        intent.putExtra("MainActTitle", word.interestWord)
        intent.putExtra("MainActContent", word.wordDescription)
        intent.putExtra("MainActLink", word.link)

        startActivity(intent)
    }

    fun fillList(){

        var dbManager=DbManager(this)
        var values=ContentValues()

        values.put("Title", "ываыва")
        values.put("Content", "ываыва")
        values.put("Date", getCurrentDate())
        dbManager.insert(values)

        values.put("Title", "чсмчмва")
        values.put("Content", "мчвмч")
        values.put("Date", getCurrentDate())
        dbManager.insert(values)

        values.put("Title", "цукцук")
        values.put("Content", "alshd")
        values.put("Date", getCurrentDate())
        dbManager.insert(values)

        values.put("Title", "еуцуецу")
        values.put("Content", "alshd")
        values.put("Date", getCurrentDate())
        dbManager.insert(values)

        values.put("Title", "афывфцуф")
        values.put("Content", "alshd")
        values.put("Date", getCurrentDate())
        dbManager.insert(values)
    }



}
