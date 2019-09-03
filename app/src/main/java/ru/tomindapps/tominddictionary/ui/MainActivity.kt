package ru.tomindapps.tominddictionary.ui

import android.app.SearchManager
import android.content.*

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.SearchView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.javasampleapproach.kotlin.sqlite.DbManager
import ru.tomindapps.tominddictionary.R
import ru.tomindapps.tominddictionary.WordFactory
import ru.tomindapps.tominddictionary.adapters.WordsAdapter
import ru.tomindapps.tominddictionary.models.InterestWord
import ru.tomindapps.tominddictionary.viewmodels.MainActivityViewModel
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity(), WordsAdapter.MyAdapterListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: WordsAdapter
    private lateinit var mainActivityViewModel: MainActivityViewModel
    private lateinit var sortOrder:String
    val ORDER_BY_DATE = "Date"
    val ORDER_BY_TITLE = "Title DESC"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        sortOrder = ORDER_BY_DATE

        setupViews()
        setupViewModels()

        /*db = DB.instance
        db.create(this)

        loadListOfWords()
        list = WordFactory.getList()*/



    }



    private fun setupViewModels() {
        mainActivityViewModel = ViewModelProviders.of(this).get(MainActivityViewModel(application)::class.java)
        mainActivityViewModel.words.observe(this, androidx.lifecycle.Observer { viewAdapter.updateList(it) })
    }

    private fun setupViews() {
        val viewManager = LinearLayoutManager(this)
        viewAdapter = WordsAdapter(this)

        recyclerView = findViewById<RecyclerView>(R.id.rvWords).apply {
            layoutManager = viewManager
            addItemDecoration(object: DividerItemDecoration(this@MainActivity,LinearLayoutManager.VERTICAL){})
            adapter = viewAdapter
        }
    }

    override fun onResume() {

        mainActivityViewModel.getWords()

        val clipText = clipBoardIntercept()
        if (clipText!=null) addClipDataToEditAct(clipText)

        super.onResume()
    }

    override fun onEditClicked(position: Int) {
        editWord(mainActivityViewModel.words.value!![position])
    }

    override fun onWikiClicked(position: Int) {
        openWiki(mainActivityViewModel.words.value!![position].link)
    }

    override fun onMessageRowClicked(position: Int) {
        showBottomSheetFragment(mainActivityViewModel.words.value!![position])

    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        when (item!!.itemId){
            R.id.menuSortAlphabet -> sortOrder=ORDER_BY_TITLE
            R.id.menuSortDate -> sortOrder=ORDER_BY_DATE
        }

        mainActivityViewModel.getWords()

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
                    mainActivityViewModel.findWords("%"+query+"%")
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText!=null) {
                    mainActivityViewModel.findWords("%"+ newText +"%")
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
        return try {
            val clipBoard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clipData = clipBoard.primaryClip

            if (clipData!=null) {
                var text = clipData.getItemAt(0).text
                if (text.length<=20&&text.length>=4) {
                    text.toString()
                } else null
            } else null
        } catch (ex:Throwable){
            null
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
            setMessage("У вас есть слово - ${s.capitalize()} в буфере обмена! Хотите его добавить?")
            setCancelable(true)
            setPositiveButton(resources.getString(R.string.yes)) { dialog, which ->
                dialog!!.cancel()
                clearClipBoard()
                startEditActWithClip(s.capitalize())
            }
            setNegativeButton(resources.getString(R.string.no)
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

   /* private fun searchInListOfWords(s:String){
        WordFactory.clearList()
        for (w in db.searchInDB(s)) WordFactory.addWords(w)
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

                WordFactory.addWords(
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

    }

    private fun loadListOfWords(){
        WordFactory.clearList()
        for (w in db.loadAllFromDB(sortOrder)) WordFactory.addWords(w)


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

                WordFactory.addWords(
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
    }*/

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
