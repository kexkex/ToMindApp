package ru.tomindapps.tominddictionary.ui

import android.content.Context
import android.content.DialogInterface
import android.net.ConnectivityManager
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.activity_edit.*
import ru.tomindapps.tominddictionary.R
import ru.tomindapps.tominddictionary.WordFactory
import ru.tomindapps.tominddictionary.adapters.MyAutoCompliteAdapter
import ru.tomindapps.tominddictionary.models.InterestWord
import ru.tomindapps.tominddictionary.models.WikiWords
import ru.tomindapps.tominddictionary.viewmodels.EditActivityViewModel
import java.text.SimpleDateFormat
import java.util.*

class EditActivity : AppCompatActivity() {

    var id=0
    var title=""
    var link=""
    var locale = "ru"

    private lateinit var tvAutoComplTitle:AutoCompleteTextView
    private lateinit var progressBar: ProgressBar
    private lateinit var editActivityViewModel: EditActivityViewModel
    private lateinit var adapter: MyAutoCompliteAdapter
    private lateinit var buRem: Button
    private lateinit var buSearch: Button
    private lateinit var buSave: Button

    private var word: InterestWord? = null
    private var wikiWord: WikiWords? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)

        setTitle(R.string.edit_activity_lable)

        setupViews()
        setupViewModels()
        setupFromBundle()

    }

    private fun setupFromBundle() {
        try {
            val bundle: Bundle? = intent.extras

            if (bundle != null) {
                id = bundle.getInt("MainActId", 0)
                title = bundle.getString("MainActTitle",null)
                setTitle(title)
            } else return

            if (id != 0) {
                editActivityViewModel.selectWordById(id)
                buRem.visibility=Button.VISIBLE
            } else {
                if (title!=null){
                    tvEditWord.setText(title)
                }
            }
        } catch (ex: Exception) {
        }    }

    private fun setupViewModels() {
        editActivityViewModel = ViewModelProviders.of(this).get(EditActivityViewModel(application)::class.java)
        editActivityViewModel.wikiWords.observe(this, androidx.lifecycle.Observer { adapter.updateList(it) })
        editActivityViewModel.word.observe(this, androidx.lifecycle.Observer { updateTextViews(it) })
    }

    private fun updateTextViews(word: InterestWord?) {
        this.word = word
        tvEditWord.setText(word?.interestWord)
        tvEditDescription.setText(word?.wordDescription)
    }

    private fun setupViews() {
        progressBar = findViewById(R.id.progressBar)
        buRem = findViewById(R.id.buRemove)
        buSave = findViewById(R.id.buSave)
        buSearch = findViewById(R.id.buEditSearch)

        buRem.setOnClickListener { removeButtonClick() }
        buSave.setOnClickListener { saveButtonClick() }
        buSearch.setOnClickListener { onWikiClick() }

        progressBar.visibility = ProgressBar.INVISIBLE
        buRem.visibility = Button.GONE

        adapter = MyAutoCompliteAdapter(this)

        tvAutoComplTitle = tvEditWord
        tvAutoComplTitle.threshold = 4
        tvAutoComplTitle.setAdapter(adapter)
        tvAutoComplTitle.setOnItemClickListener { parent, view, position, id -> onDropDownItemClick(position) }

        locale = Locale.getDefault().language


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

    fun onDropDownItemClick(position: Int) {
        wikiWord = editActivityViewModel.wikiWords.value!![position]
        tvEditWord.setText(wikiWord!!.title)
        tvEditDescription.setText(wikiWord!!.description)
    }


    fun onWikiClick(){
        if (isNetworkConnected()) {
            editActivityViewModel.findInWiki(tvEditWord.text.toString())
            tvAutoComplTitle.showDropDown()
        } else Toast.makeText(this@EditActivity, resources.getString(R.string.network_is_not_connected), Toast.LENGTH_LONG).show()
    }

    fun getCurrentDate():String{

        val currentDate = Date()
        val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        val dateText = dateFormat.format(currentDate)
        val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        val timeText = timeFormat.format(currentDate)

        return "$dateText $timeText"
    }

    fun removeButtonClick(){
        if (word != null) {editActivityViewModel.deleteWord(word!!)}
        finish()

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


    fun saveButtonClick(){

        if (word == null){
            if (tvEditWord.text.isNotEmpty() && tvEditDescription.text.isNotEmpty()) {
                val link = wikiWord?.link ?: ""
                editActivityViewModel.saveWord(InterestWord.createWord(
                    tvEditWord.text.toString(),
                    tvEditDescription.text.toString(),
                    getCurrentDate(),
                    link
                ))
                finish()

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
        } else {

            editActivityViewModel.saveWord(word!!)
            finish()
        }


    }

}

