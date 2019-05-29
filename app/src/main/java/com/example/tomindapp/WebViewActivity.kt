package com.example.tomindapp

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
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
