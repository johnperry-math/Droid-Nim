package name.cantanima.droidnim

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.webkit.WebView

/**
 * Created by cantanima on 9/20/17.
 */

class Information_Activity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_information)
        val help_view : WebView = findViewById(R.id.help_webview)
        help_view.loadUrl("file:///android_asset/Help.html")
    }
}