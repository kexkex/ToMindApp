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
import ru.tomindapps.tominddictionary.adapters.WordsAdapter
import ru.tomindapps.tominddictionary.models.InterestWord
import ru.tomindapps.tominddictionary.viewmodels.MainActivityViewModel
import ru.tomindapps.tominddictionary.viewmodels.OrderType


class MainActivity : AppCompatActivity(), WordsAdapter.MyAdapterListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: WordsAdapter
    private lateinit var mainActivityViewModel: MainActivityViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupViews()
        setupViewModels()
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

        mainActivityViewModel.getWordsByOrder()

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
        val orderType = when (item!!.itemId){
            R.id.menuSortAlphabet -> OrderType.TITLE
            R.id.menuSortDate -> OrderType.DATE
            else -> null
        }
        if (orderType !=null ) mainActivityViewModel.getWordsByOrder(orderType)

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
                    mainActivityViewModel.findWords("%$query%")
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText!=null) {
                    mainActivityViewModel.findWords("%$newText%")
                }
                return false

            }
        })

        return super.onCreateOptionsMenu(menu)
    }


    private fun showBottomSheetFragment(word: InterestWord){

        val bsf = BottomSheetFragment()

        var bundle = Bundle()
        bundle.putString("title",word.interestWord)
        bundle.putString("descr",word.wordDescription)
        bundle.putString("link",word.link)

        bsf.arguments = bundle
        bsf.show(supportFragmentManager, bsf.tag)
    }

    private fun openWiki(link:String){

        val intent = Intent(this, WebViewActivity::class.java)
        intent.putExtra("Link",link)
        startActivity(intent)
    }

    fun fabClick(view: View){
        val intent = Intent(this, EditActivity::class.java)
        startActivity(intent)
    }

    private fun clipBoardIntercept():String?{
        return try {
            val clipBoard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clipData = clipBoard.primaryClip

            if (clipData != null) {
                var text = clipData.getItemAt(0).text
                if (text.length<=20&&text.length>=4) {
                    text.toString()
                } else null
            } else null
        } catch (ex: Throwable){
            null
        }
    }

    private fun clearClipBoard(){

        val clipBoard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clipData = ClipData.newPlainText("","")
        clipBoard.primaryClip = clipData
    }

    private fun addClipDataToEditAct(s:String){

        val builder = AlertDialog.Builder(this)
        builder.apply {
            setTitle("${s.capitalize()}")
            setMessage("У вас есть слово - ${s.capitalize()} в буфере обмена! Хотите его добавить?")
            setCancelable(true)
            setPositiveButton(resources.getString(R.string.yes)) { dialog, _ ->
                dialog!!.cancel()
                clearClipBoard()
                startEditActWithClip(s.capitalize())
            }
            setNegativeButton(resources.getString(R.string.no)
            ) { dialog, _ ->
                dialog!!.cancel()
                clearClipBoard()
            }
        }
        val alert = builder.create()
        alert.show()
    }

    private fun startEditActWithClip(s:String){

        var intent = Intent(this, EditActivity::class.java)
        intent.putExtra("MainActTitle", s)
        startActivity(intent)
    }



    private fun editWord(word: InterestWord) {

        var intent = Intent(this, EditActivity::class.java)

        intent.putExtra("MainActId", word.idWord)
        intent.putExtra("MainActTitle", word.interestWord)
        intent.putExtra("MainActContent", word.wordDescription)
        intent.putExtra("MainActLink", word.link)

        startActivity(intent)
    }

}
