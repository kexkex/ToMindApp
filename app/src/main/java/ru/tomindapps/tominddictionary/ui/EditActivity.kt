package ru.tomindapps.tominddictionary.ui

import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.activity_edit.*
import ru.tomindapps.tominddictionary.R
import ru.tomindapps.tominddictionary.adapters.MyAutoCompliteAdapter
import ru.tomindapps.tominddictionary.models.InterestWord
import ru.tomindapps.tominddictionary.models.WikiWords
import ru.tomindapps.tominddictionary.viewmodels.EditActivityViewModel
import java.text.SimpleDateFormat
import java.util.*

class EditActivity : AppCompatActivity() {

    var id=0
    var title=""

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
                buRem.visibility = Button.VISIBLE
            } else {
                tvEditWord.setText(title)
            }
        } catch (ex: Exception) {
        }
    }

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
        tvAutoComplTitle = findViewById(R.id.tvEditWord)

        buRem.setOnClickListener { removeButtonClick() }
        buSave.setOnClickListener { saveButtonClick() }
        buSearch.setOnClickListener { onWikiClick() }

        progressBar.visibility = ProgressBar.INVISIBLE
        buRem.visibility = Button.GONE

        adapter = MyAutoCompliteAdapter(this)

        tvAutoComplTitle.setAdapter(adapter)
        tvAutoComplTitle.setOnItemClickListener { _, _, position, _ -> onDropDownItemClick(position) }
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

    private fun onDropDownItemClick(position: Int) {
        wikiWord = editActivityViewModel.wikiWords.value!![position]
        tvEditWord.setText(wikiWord!!.title)
        tvEditDescription.setText(wikiWord!!.description)
    }


    private fun onWikiClick(){
        if (isNetworkConnected()) {
            editActivityViewModel.findInWiki(tvEditWord.text.toString())
            tvAutoComplTitle.showDropDown()
        } else Toast.makeText(this@EditActivity, resources.getString(R.string.network_is_not_connected), Toast.LENGTH_LONG).show()
    }

    private fun getCurrentDate():String{

        val currentDate = Date()
        val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        val dateText = dateFormat.format(currentDate)
        val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        val timeText = timeFormat.format(currentDate)

        return "$dateText $timeText"
    }

    private fun removeButtonClick(){
        if (word != null) {editActivityViewModel.deleteWord(word!!)}
        finish()

    }

    private fun saveButtonClick(){

        if (word == null){
            if (checkTvNotEmpty()) {
                saveNewWord()
            } else {
                showDialogError()
            }
        } else {
            updateWord()
        }
    }

    private fun updateWord() {
        val editedWord = word
        val _link = wikiWord?.link ?: ""
        editedWord?.apply {
            interestWord = tvEditWord.text.toString()
            wordDescription = tvEditDescription.text.toString()
            date = getCurrentDate()
            link = _link
        }
        editActivityViewModel.saveWord(editedWord!!)
        finish()
    }

    private fun saveNewWord() {
        val link = wikiWord?.link ?: ""
        editActivityViewModel.saveWord(
            InterestWord(
            tvEditWord.text.toString(),
            tvEditDescription.text.toString(),
            getCurrentDate(),
            link)
        )
        finish()
    }

    private fun checkTvNotEmpty():Boolean{
        return (tvEditWord.text.isNotEmpty() && tvEditDescription.text.isNotEmpty())
    }

    private fun showDialogError() {
        val builder = AlertDialog.Builder(this)
        builder.apply {
            setTitle(resources.getString(R.string.error))
            setMessage(resources.getString(R.string.enter_word_title_and_description))
            setCancelable(false)
            setNegativeButton(resources.getString(R.string.close)
            ) { dialog, _ -> dialog!!.cancel() }
        }
        val alert = builder.create()
        alert.show()
    }

}

