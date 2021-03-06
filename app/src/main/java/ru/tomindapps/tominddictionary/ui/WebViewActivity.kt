package ru.tomindapps.tominddictionary.ui

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import ru.tomindapps.tominddictionary.R
import kotlinx.android.synthetic.main.activity_web_view.*

class WebViewActivity : AppCompatActivity() {

    var url:String?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_view)

        try {
            var bundle: Bundle? = intent.extras
            if (bundle != null) {
                url = bundle.getString("Link", null)
            }
        } catch (ex: Exception) {
        }

        val webView = webView
        webView.settings.javaScriptEnabled=true
        if (url!=null) webView.loadUrl(url) else
            Toast.makeText(this, "No such page", Toast.LENGTH_LONG).show()
            finish()
    }
}
