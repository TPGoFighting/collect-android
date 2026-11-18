package top.tpgofighting.collect

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback

class MainActivity : ComponentActivity() {

    companion object {
        private const val HOME_URL = "https://collect.tpgofighting.top/"
        private const val SITE_HOST = "collect.tpgofighting.top"
    }

    private lateinit var webView: WebView
    private lateinit var splash: View
    private lateinit var splashError: TextView

    private val backCallback = object : OnBackPressedCallback(false) {
        override fun handleOnBackPressed() {
            if (::webView.isInitialized && webView.canGoBack()) {
                webView.goBack()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        onBackPressedDispatcher.addCallback(this, backCallback)

        webView = findViewById(R.id.webview)
        splash = findViewById(R.id.splash)
        splashError = findViewById(R.id.splash_error)

        splash.setOnClickListener {
            if (splashError.visibility == View.VISIBLE) {
                splashError.visibility = View.GONE
                webView.reload()
            }
        }

        webView.settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            databaseEnabled = true
            cacheMode = WebSettings.LOAD_DEFAULT
            allowFileAccess = false
            allowContentAccess = false
        }

        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(
                view: WebView,
                request: WebResourceRequest,
            ): Boolean {
                val uri = request.url
                return if (uri.host == SITE_HOST) {
                    false
                } else {
                    openInBrowser(uri)
                    true
                }
            }

            @Deprecated("Deprecated in Java")
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                val uri = Uri.parse(url)
                return if (uri.host == SITE_HOST) {
                    false
                } else {
                    openInBrowser(uri)
                    true
                }
            }

            override fun onPageStarted(view: WebView, url: String, favicon: android.graphics.Bitmap?) {
                backCallback.isEnabled = view.canGoBack()
            }

            override fun onPageFinished(view: WebView, url: String) {
                backCallback.isEnabled = view.canGoBack()
                hideSplash()
            }

            override fun onPageCommitVisible(view: WebView, url: String) {
                // Fires when first content renders, unlike onPageFinished
                // which waits for every subresource (including a font
                // stylesheet that can hang on networks where Google is
                // unreachable). The splash must never outlive first paint.
                hideSplash()
            }

            override fun onReceivedError(
                view: WebView,
                request: WebResourceRequest,
                error: WebResourceError,
            ) {
                if (request.isForMainFrame) {
                    splashError.visibility = View.VISIBLE
                }
            }

            @Deprecated("Deprecated in Java")
            override fun onReceivedError(
                view: WebView,
                errorCode: Int,
                description: String,
                failingUrl: String,
            ) {
                splashError.visibility = View.VISIBLE
            }
        }

        if (savedInstanceState == null) {
            webView.loadUrl(HOME_URL)
            // Safety net: if the load stalls entirely (e.g. a hanging
            // font stylesheet keeps onPageFinished from ever firing),
            // never leave the user on the splash longer than 10s.
            // If an error is showing, keep the splash so tap-to-retry
            // stays visible.
            webView.postDelayed({
                if (splashError.visibility != View.VISIBLE) {
                    hideSplash()
                }
            }, 10_000)
        }
    }

    private fun openInBrowser(uri: Uri) {
        try {
            startActivity(Intent(Intent.ACTION_VIEW, uri))
        } catch (_: Exception) {
            // No browser available; stay in the WebView.
        }
    }

    private fun hideSplash() {
        if (splash.visibility != View.VISIBLE || splash.alpha == 0f) return
        splash.animate()
            .alpha(0f)
            .setDuration(300)
            .withEndAction {
                splash.visibility = View.GONE
                splash.alpha = 1f
            }
            .start()
    }

    override fun onDestroy() {
        webView.destroy()
        super.onDestroy()
    }
}
