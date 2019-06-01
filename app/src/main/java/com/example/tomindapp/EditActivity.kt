package com.example.tomindapp

import android.content.ContentValues
import android.content.Context
import android.content.DialogInterface
import android.net.ConnectivityManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.view.View
import android.widget.*
import com.javasampleapproach.kotlin.sqlite.DbManager
import kotlinx.android.synthetic.main.activity_edit.*
import kotlinx.coroutines.*
import java.net.URL
import java.net.URLEncoder
import java.text.SimpleDateFormat
import java.util.*

class EditActivity : AppCompatActivity() {



    @Volatile
    var responseText:String?=null

    var id=0
    var title=""
    var descr=""
    var link=""
    var titleArrayList= arrayListOf<String>()
    var descrArrayList=arrayListOf<String>()
    var linkArrayList= arrayListOf<String>()
    var locale = "ru"

    val WORDS_LIMIT=5

    lateinit var tvAutoComplTitle:AutoCompleteTextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)

        val buRem = buRemove
        buRem.visibility=Button.GONE

        var adapter = MyAutoCompliteAdapter(this,titleArrayList)

        tvAutoComplTitle= tvEditWord
        tvAutoComplTitle.threshold=4
        tvAutoComplTitle.setAdapter(adapter)
        tvAutoComplTitle.setOnItemClickListener { parent, view, position, id -> onDropDownItemClick() }

        locale=Locale.getDefault().language

        try {
            var bundle: Bundle? = intent.extras

            if (bundle != null) {
                id = bundle.getInt("MainActId", 0)
                title = bundle.getString("MainActTitle",null)
            }

            if (id != 0) {
                title = bundle!!.getString("MainActTitle")
                descr = bundle!!.getString("MainActContent")
                tvEditWord.setText(title)
                tvEditDescription.setText(descr)
                link = bundle!!.getString("MainActLink")
                buRem.visibility=Button.VISIBLE
            } else {
                if (title!=null){
                    tvEditWord.setText(title)
                }
            }
        } catch (ex: Exception) {
        }
    }

    override fun onBackPressed() {
        finish()
        super.onBackPressed()
    }

    private fun isNetworkConnected(): Boolean {

        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo

        return networkInfo != null && networkInfo.isConnected
    }

    fun onDropDownItemClick(){

        val ttl=tvEditWord.text.toString()
        for (i in 0..(titleArrayList.size-1)) {
            if (ttl==titleArrayList[i]) {
                title=titleArrayList[i]
                descr=descrArrayList[i]
                link=linkArrayList[i]
            }
        }
        clearArrs()

        tvEditDescription.setText(descr)
        tvEditWord.setText(title)
    }

    fun clearArrs(){
        if (titleArrayList.size!=0) titleArrayList.clear()
        if (descrArrayList.size!=0) descrArrayList.clear()
        if (descrArrayList.size!=0) linkArrayList.clear()
    }


    fun onWikiClick(view: View){
        clearArrs()
        if (isNetworkConnected()) {

            getWordFromWiki()

            if (titleArrayList.size>1) {
               tvAutoComplTitle.showDropDown()
            } else {
                clearArrs()
                tvEditDescription.setText(descr)
                tvEditWord.setText(title)
            }
        } else Toast.makeText(this@EditActivity, resources.getString(R.string.network_is_not_connected), Toast.LENGTH_LONG).show()
    }

    fun getWordFromWiki () {
        val word = tvEditWord.text.toString()
        if (word != "") sendGet(word)
    }

    fun sendGetAsync(word:String?){

        val async= object:Thread(Runnable {
            var count=0

            var response:String?

            val encodedWord = URLEncoder.encode(word, "utf8")
            val url =
                URL("https://$locale.wikipedia.org/w/api.php?action=opensearch&search=$encodedWord&prop=info&inprop=url&limit=$WORDS_LIMIT")
            do {
                count++
                response = null

                try {
                    if (isNetworkConnected()){
                        url.openConnection()
                        response = url.readText()}
                } catch (ex: Exception) {

                    Toast.makeText(this@EditActivity, ex.toString(), Toast.LENGTH_LONG).show()

                }
            } while (response == null)
            responseText=response
        }){}
        async.start()
        async.join(3000)
    }

    fun sendGet(word:String?) {

            sendGetAsync(word)

        if (responseText!=null) {

            if (fromJsonParse(responseText!!)) {
                Toast.makeText(this@EditActivity, resources.getString(R.string.search_done), Toast.LENGTH_LONG).show()
                title=titleArrayList[0]
                descr=descrArrayList[0]
                link= linkArrayList[0]
            } else {
                clearArrs()
                Toast.makeText(this@EditActivity, resources.getString(R.string.search_fail), Toast.LENGTH_LONG).show()
                link=""
                title="$word"
                descr=""
            }
        } else {
            clearArrs()
            Toast.makeText(this@EditActivity, resources.getString(R.string.search_fail), Toast.LENGTH_LONG).show()
            link=""
            title="$word"
            descr=""}
    }


    fun fromJsonParse(jsonString:String):Boolean  {
        var bul=false
        val jsp = JsonWikiParse(jsonString)
        if (jsp.title.isNotEmpty()&&jsp.desc.isNotEmpty()&&jsp.link.isNotEmpty()){
            bul=true
            clearArrs()
            for (i in 0..(jsp.title.size-1)) {
                titleArrayList.add(jsp.title[i])
                descrArrayList.add(jsp.desc[i])
                linkArrayList.add(jsp.link[i])
            }
        }

        return bul
    }

    fun replaceChar(str:String):String{
    var s=str
    s=s.replace("[", "")
    s=s.replace("]", "")
    s=s.replace("?", "")
    s=s.replace("\u0301", "")
    //str=str.replace("", "")
    return s
    }

    fun getCurrentDate():String{

        val currentDate = Date()
        val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        val dateText = dateFormat.format(currentDate)
        val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        val timeText = timeFormat.format(currentDate)

        return "$dateText $timeText"
    }

    fun removeButtonClick(view: View){
        if (id!=0){
        var dbManager=DbManager(this)
        var selectionArs = arrayOf(id.toString())

        val mID = dbManager.delete( "Id=?", selectionArs)

        if (mID > 0) {
            Toast.makeText(this, resources.getString(R.string.remove_note_successfully), Toast.LENGTH_LONG).show()
            finish()
        } else {
            Toast.makeText(this, resources.getString(R.string.remove_note_fail), Toast.LENGTH_LONG).show()
        }
        }

    }

    fun wordContains():Boolean{
        var contains = false
        val t = tvEditWord.text.toString()
        for (w in WordFactory.getList()) {
            if (w.interestWord==t) {
                contains=true
            }
        }
        return contains
    }


    fun saveButtonClick(view: View){

        var dbManager=DbManager(this)
        var values=ContentValues()

        if (tvEditWord.text.isNotEmpty()&&tvEditDescription.text.isNotEmpty()) {

            values.put("Title", tvEditWord.text.toString())
            values.put("Content", tvEditDescription.text.toString())
            values.put("Date", getCurrentDate())
            values.put("Link", link)

            if (id == 0) {
                if (wordContains()) {Toast.makeText(this, resources.getString(R.string.word_already_exists), Toast.LENGTH_LONG).show()}
                else {
                    val mID = dbManager.insert(values)

                    if (mID > 0) {
                        Toast.makeText(this, resources.getString(R.string.add_note_successfully), Toast.LENGTH_LONG).show()
                        finish()
                    } else {
                        Toast.makeText(this, resources.getString(R.string.add_note_fail), Toast.LENGTH_LONG).show()
                    }
                }
            } else {
                var selectionArs = arrayOf(id.toString())
                val mID = dbManager.update(values, "Id=?", selectionArs)

                if (mID > 0) {
                    Toast.makeText(this, resources.getString(R.string.update_note_successfully), Toast.LENGTH_LONG).show()
                    finish()
                } else {
                    Toast.makeText(this, resources.getString(R.string.update_note_fail), Toast.LENGTH_LONG).show()
                }
            }
        }else{
            val builder = AlertDialog.Builder(this)
            builder.apply {
                setTitle(resources.getString(R.string.error))
                setMessage(resources.getString(R.string.enter_word_title_and_description))
                setCancelable(false)
                setNegativeButton(resources.getString(R.string.close), object:DialogInterface.OnClickListener {
                    override fun onClick(dialog: DialogInterface?, which: Int) {
                        dialog!!.cancel()
                    }
                })
            }
            val alert = builder.create()
            alert.show()
        }
    }

}

