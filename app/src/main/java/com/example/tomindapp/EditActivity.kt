package com.example.tomindapp

import android.content.ContentValues
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.ConnectivityManager
import android.os.AsyncTask
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v7.app.AlertDialog
import android.view.View
import android.widget.*
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.javasampleapproach.kotlin.sqlite.DbManager
import kotlinx.android.synthetic.main.activity_edit.*
import kotlinx.coroutines.*
import java.lang.reflect.Array
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.concurrent.thread

class EditActivity : AppCompatActivity() {

    var id=0

    //var contains:Boolean=false
    @Volatile
    var responseText:String?=null
    var title=""
    var descr=""
    var link=""
    var titleArrayList= arrayListOf<String>()
    var descrArrayList=arrayListOf<String>()

    var linkArrayList= arrayListOf<String>()
    val WORDS_LIMIT=5
    lateinit var tvAutoComplTitle:AutoCompleteTextView



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)
       // R.layout.activity_main
        val buRem = buRemove
        buRem.visibility=Button.GONE



        tvAutoComplTitle= tvEditWord
        var adapter = MyAutoCompliteAdapter(this,titleArrayList)
        tvAutoComplTitle.threshold=3
        tvAutoComplTitle.setAdapter(adapter)
        tvAutoComplTitle.setOnItemClickListener { parent, view, position, id -> onDropDownItemClick() }



        try {
            var bundle: Bundle? = intent.extras
            if (bundle != null) {
                id = bundle.getInt("MainActId", 0)
                buRem.visibility=Button.VISIBLE
            }
            if (id != 0) {
                title = bundle!!.getString("MainActTitle")
                descr = bundle!!.getString("MainActContent")
                tvEditWord.setText(title)
                tvEditDescription.setText(descr)
                link = bundle!!.getString("MainActLink")
            }
        } catch (ex: Exception) {
        }
    }

    private fun isNetworkConnected(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager //1
        val networkInfo = connectivityManager.activeNetworkInfo //2
        return networkInfo != null && networkInfo.isConnected //3
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
        //tvEditWord.setText(title)
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
            tvEditWord.setText(title)}


        } else Toast.makeText(this@EditActivity, "Network is NOT connected!", Toast.LENGTH_LONG).show()
    }



    fun getWordFromWiki () {
        clearArrs()
        val word = tvEditWord.text.toString()
        if (word != "") sendGet(word)

    }

    fun sendGetAsync(word:String?){

        val async= object:Thread(Runnable {
            var count=0

            var response:String?

            val encodedWord = URLEncoder.encode(word, "utf8")
            val url =
                URL("https://ru.wikipedia.org/w/api.php?action=opensearch&search=$encodedWord&prop=info&inprop=url&limit=$WORDS_LIMIT")
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
                Toast.makeText(this@EditActivity, "Search Done", Toast.LENGTH_LONG).show()
                title=titleArrayList[0]
                descr=descrArrayList[0]
                link= linkArrayList[0]
            }

            else {
                clearArrs()
                Toast.makeText(this@EditActivity, "Search Fail", Toast.LENGTH_LONG).show()
                link=""
                title="$word"
                descr=""
            }
        } else {
            clearArrs()
            Toast.makeText(this@EditActivity, "Search Fail", Toast.LENGTH_LONG).show()
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
        // Форматирование времени как "день.месяц.год"
        val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        val dateText = dateFormat.format(currentDate)
        // Форматирование времени как "часы:минуты:секунды"
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
            Toast.makeText(this, "Remove note successfully!", Toast.LENGTH_LONG).show()
            finish()
        } else {
            Toast.makeText(this, "Fail to remove note!", Toast.LENGTH_LONG).show()
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
                if (wordContains()) {Toast.makeText(this, "Word already exists!", Toast.LENGTH_LONG).show()} else {
                val mID = dbManager.insert(values)

                if (mID > 0) {
                    Toast.makeText(this, "Add note successfully!", Toast.LENGTH_LONG).show()
                    finish()
                } else {
                    Toast.makeText(this, "Fail to add note!", Toast.LENGTH_LONG).show()
                }}
            } else {
                var selectionArs = arrayOf(id.toString())
                val mID = dbManager.update(values, "Id=?", selectionArs)

                if (mID > 0) {
                    Toast.makeText(this, "Update note successfully!", Toast.LENGTH_LONG).show()
                    finish()
                } else {
                    Toast.makeText(this, "Fail to update note!", Toast.LENGTH_LONG).show()
                }
            }
        }else{
            val builder = AlertDialog.Builder(this)
            builder.apply {
                setTitle("Error")
                setMessage("Enter word title and description")
                setCancelable(false)
                setNegativeButton("OK", object:DialogInterface.OnClickListener {
                    override fun onClick(dialog: DialogInterface?, which: Int) {
                        dialog!!.cancel()
                    }
                })

            }
            val alert = builder.create()
            alert.show()

        }
    }

    inner class AsyncGetRespose:AsyncTask<String,Void,String>() {
        override fun doInBackground(vararg params: String?): String {
            var count=0
            var word = ""
            var response:String?
            if (params.size>0) word = params[0]!!
            val encodedWord = URLEncoder.encode(word, "utf8")
            val url =
                URL("https://ru.wikipedia.org/w/api.php?action=opensearch&search=$encodedWord&prop=info&inprop=url&limit=$WORDS_LIMIT")
            do {
                count++
                responseText = null

            try {
                if (isNetworkConnected()){
                    url.openConnection()
                    responseText = url.readText()}
            } catch (ex: Exception) {

                Toast.makeText(this@EditActivity, ex.toString(), Toast.LENGTH_LONG).show()

            }
            } while (responseText == null)

            return ""
        }
    }


}

