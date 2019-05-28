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
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.javasampleapproach.kotlin.sqlite.DbManager
import kotlinx.android.synthetic.main.activity_edit.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
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
    var responseTxt:String=""
    //var contains:Boolean=false
    @Volatile
    var responseText:String?=null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)
        R.layout.activity_main

        /*
        buEditSearch.setOnClickListener { v ->
            onSearchClick()
        }*/
        val buWiki = buEditSearch
        if (isNetworkConnected()) buWiki.visibility=Button.VISIBLE else buWiki.visibility=Button.INVISIBLE

        try {
            var bundle: Bundle = intent.extras
            id = bundle.getInt("MainActId", 0)
            if (id != 0) {

                tvEditWord.setText(bundle.getString("MainActTitle"))
                tvEditDescription.setText(bundle.getString("MainActContent"))
            }
        } catch (ex: Exception) {
        }
    }

    private fun isNetworkConnected(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager //1
        val networkInfo = connectivityManager.activeNetworkInfo //2
        return networkInfo != null && networkInfo.isConnected //3
    }


    fun onWikiClick(view: View){
        var wikiDescr:String?=""
        val word=tvEditWord.text.toString()

        if (word!=null) wikiDescr=sendGet(word)

        tvEditDescription.setText(wikiDescr)

    }


    fun sendGet(word:String): String? {
        val progBar = progressBar2
        progBar.visibility= ProgressBar.INVISIBLE
        do {
            progBar.visibility= ProgressBar.VISIBLE


            GlobalScope.launch {


                val encodedWord = URLEncoder.encode(word, "utf8")
                val url = URL("https://ru.wikipedia.org/w/api.php?action=opensearch&search=$encodedWord&prop=info&inprop=url&limit=1")
                var resptxt = ""

                try {
                    url.openConnection()

                    responseText = url.readText()
                } catch (ex: Exception) {
                    responseText = ex.toString()
                }


                /*val urlConnection=url.openConnection() as HttpURLConnection
                urlConnection.requestMethod = "GET"
                var br=urlConnection.inputStream.bufferedReader(Charsets.UTF_8)

                var inputLine=br.readLine()
                    while (inputLine!=null) {
                        resptxt += inputLine
                        inputLine = br.readLine()
                    }
                responseText=resptxt*/
            }

        } while (responseText==null)


       //responseText=fromJsonParse(responseText!!)

        progBar.visibility= ProgressBar.INVISIBLE

        Toast.makeText(this, "$responseText", Toast.LENGTH_LONG).show()

        return responseText



    }


    fun fromJsonParse(jsonString:String):String  {
        var fromJsonString=""
        val gson = Gson();


        var type = object:TypeToken<ArrayList<Any>>(){}.type
        var read = arrayListOf<Any>(gson.fromJson<Any>(jsonString,type))
        var titles= arrayListOf<String>(read[1].toString())
        var contents = arrayListOf<String>(read[2].toString())
        var links = arrayListOf<String>(read[3].toString())
        for (s in links) fromJsonString+=s
        fromJsonString=replaceChar(fromJsonString)
        return fromJsonString


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





    fun saveButtonClick(view: View){

        var dbManager=DbManager(this)
        var values=ContentValues()

        if (tvEditWord.text.isNotEmpty()&&tvEditDescription.text.isNotEmpty()) {

            values.put("Title", tvEditWord.text.toString())
            values.put("Content", tvEditDescription.text.toString())
            values.put("Date", getCurrentDate())


            if (id == 0) {
                val mID = dbManager.insert(values)

                if (mID > 0) {
                    Toast.makeText(this, "Add note successfully!", Toast.LENGTH_LONG).show()
                    finish()
                } else {
                    Toast.makeText(this, "Fail to add note!", Toast.LENGTH_LONG).show()
                }
            } else {
                var selectionArs = arrayOf(id.toString())
                val mID = dbManager.update(values, "Id=?", selectionArs)

                if (mID > 0) {
                    Toast.makeText(this, "Add note successfully!", Toast.LENGTH_LONG).show()
                    finish()
                } else {
                    Toast.makeText(this, "Fail to add note!", Toast.LENGTH_LONG).show()
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


}

