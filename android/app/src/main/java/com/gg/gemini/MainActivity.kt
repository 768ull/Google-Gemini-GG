package com.gg.gemini

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.view.WindowManager
import android.webkit.CookieManager
import android.webkit.PermissionRequest
import android.webkit.SslErrorHandler
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.net.http.SslError
import android.widget.Toast
import androidx.webkit.WebViewCompat
import java.util.Locale

class MainActivity : Activity() {
    private lateinit var geminiView: WebView
    private var fileChooserCallback: ValueCallback<Array<Uri>>? = null
    private var pendingWebPermission: PermissionRequest? = null
    private var pendingWebResources: Array<String> = emptyArray()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        createGeminiView()
        setContentView(geminiView)
        hideSystemBars()

        val provider = WebViewCompat.getCurrentWebViewPackage(this)
        Log.i(TAG, "WebView provider: ${provider?.packageName} ${provider?.versionName}")

        if (savedInstanceState == null) {
            geminiView.loadUrl(GEMINI_URL)
        } else {
            geminiView.restoreState(savedInstanceState)
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun createGeminiView() {
        CookieManager.getInstance().setAcceptCookie(true)

        geminiView = WebView(this).apply {
            setBackgroundColor(Color.rgb(19, 19, 20))
            isVerticalScrollBarEnabled = false
            isHorizontalScrollBarEnabled = false
            isLongClickable = false
            isHapticFeedbackEnabled = false
            overScrollMode = View.OVER_SCROLL_NEVER
            setOnLongClickListener { true }

            settings.apply {
                javaScriptEnabled = true
                domStorageEnabled = true
                databaseEnabled = true
                setSupportZoom(false)
                builtInZoomControls = false
                displayZoomControls = false
                useWideViewPort = true
                loadWithOverviewMode = true
                javaScriptCanOpenWindowsAutomatically = false
                setSupportMultipleWindows(false)
                allowFileAccess = false
                allowContentAccess = true
                mixedContentMode = WebSettings.MIXED_CONTENT_NEVER_ALLOW
                cacheMode = WebSettings.LOAD_DEFAULT
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    disabledActionModeMenuItems = (
                        WebSettings.MENU_ITEM_SHARE or
                            WebSettings.MENU_ITEM_WEB_SEARCH or
                            WebSettings.MENU_ITEM_PROCESS_TEXT
                        )
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    safeBrowsingEnabled = true
                }
            }

            CookieManager.getInstance().setAcceptThirdPartyCookies(this, true)
            webViewClient = GeminiWebViewClient()
            webChromeClient = GeminiWebChromeClient()
        }

        WebView.setWebContentsDebuggingEnabled(BuildConfig.DEBUG)
    }

    private inner class GeminiWebViewClient : WebViewClient() {
        override fun shouldOverrideUrlLoading(
            view: WebView?,
            request: WebResourceRequest
        ): Boolean = !isAllowedNavigation(request.url)

        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)
            if (url != null && isAllowedNavigation(Uri.parse(url))) {
                view?.evaluateJavascript(HARDENING_SCRIPT, null)
            }
        }

        override fun onReceivedSslError(
            view: WebView?,
            handler: SslErrorHandler?,
            error: SslError?
        ) {
            handler?.cancel()
        }
    }

    private inner class GeminiWebChromeClient : WebChromeClient() {
        override fun onCreateWindow(
            view: WebView?,
            isDialog: Boolean,
            isUserGesture: Boolean,
            resultMsg: android.os.Message?
        ): Boolean = false

        override fun onShowFileChooser(
            webView: WebView?,
            filePathCallback: ValueCallback<Array<Uri>>?,
            fileChooserParams: FileChooserParams?
        ): Boolean {
            this@MainActivity.fileChooserCallback?.onReceiveValue(null)
            this@MainActivity.fileChooserCallback = filePathCallback

            return try {
                val intent = fileChooserParams?.createIntent()
                    ?: Intent(Intent.ACTION_GET_CONTENT).apply {
                        addCategory(Intent.CATEGORY_OPENABLE)
                        type = "*/*"
                    }
                startActivityForResult(intent, FILE_CHOOSER_REQUEST)
                true
            } catch (exception: Exception) {
                this@MainActivity.fileChooserCallback?.onReceiveValue(null)
                this@MainActivity.fileChooserCallback = null
                Toast.makeText(this@MainActivity, "Não foi possível abrir os arquivos.", Toast.LENGTH_SHORT).show()
                false
            }
        }

        override fun onPermissionRequest(request: PermissionRequest?) {
            if (request == null || !isAllowedNavigation(Uri.parse(request.origin.toString()))) {
                request?.deny()
                return
            }

            val requested = request.resources
            val supported = requested.filter {
                it == PermissionRequest.RESOURCE_AUDIO_CAPTURE ||
                    it == PermissionRequest.RESOURCE_VIDEO_CAPTURE
            }.toTypedArray()

            if (supported.isEmpty() || supported.size != requested.size) {
                request.deny()
                return
            }

            val needed = mutableListOf<String>()
            if (PermissionRequest.RESOURCE_AUDIO_CAPTURE in supported &&
                checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED
            ) {
                needed += Manifest.permission.RECORD_AUDIO
            }
            if (PermissionRequest.RESOURCE_VIDEO_CAPTURE in supported &&
                checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
            ) {
                needed += Manifest.permission.CAMERA
            }

            if (needed.isEmpty()) {
                request.grant(supported)
            } else {
                pendingWebPermission?.deny()
                pendingWebPermission = request
                pendingWebResources = supported
                requestPermissions(needed.toTypedArray(), WEB_PERMISSION_REQUEST)
            }
        }
    }

    private fun isAllowedNavigation(uri: Uri): Boolean {
        if (!uri.scheme.equals("https", ignoreCase = true)) return false
        val host = uri.host?.lowercase(Locale.ROOT) ?: return false
        return host in ALLOWED_NAVIGATION_HOSTS
    }

    @Suppress("DEPRECATION")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == FILE_CHOOSER_REQUEST) {
            val callback = fileChooserCallback ?: return
            fileChooserCallback = null
            callback.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(resultCode, data))
        }
    }

    @Suppress("DEPRECATION")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == WEB_PERMISSION_REQUEST) {
            val request = pendingWebPermission
            pendingWebPermission = null
            val granted = grantResults.isNotEmpty() &&
                grantResults.all { it == PackageManager.PERMISSION_GRANTED }
            if (granted && request != null) request.grant(pendingWebResources)
            else request?.deny()
            pendingWebResources = emptyArray()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if (::geminiView.isInitialized) geminiView.saveState(outState)
    }

    override fun onResume() {
        super.onResume()
        if (::geminiView.isInitialized) geminiView.onResume()
        hideSystemBars()
    }

    override fun onPause() {
        if (::geminiView.isInitialized) geminiView.onPause()
        super.onPause()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) hideSystemBars()
    }

    @Suppress("DEPRECATION")
    private fun hideSystemBars() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.let { controller ->
                controller.hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
                controller.systemBarsBehavior =
                    WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        } else {
            window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_FULLSCREEN or
                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                )
        }
    }

    override fun onBackPressed() {
        if (::geminiView.isInitialized && geminiView.canGoBack()) geminiView.goBack()
        else super.onBackPressed()
    }

    override fun onDestroy() {
        if (::geminiView.isInitialized) {
            geminiView.stopLoading()
            geminiView.webChromeClient = null
            geminiView.webViewClient = WebViewClient()
            geminiView.destroy()
        }
        pendingWebPermission?.deny()
        pendingWebPermission = null
        fileChooserCallback?.onReceiveValue(null)
        fileChooserCallback = null
        super.onDestroy()
    }

    companion object {
        private const val TAG = "GeminiGG"
        private const val GEMINI_URL = "https://gemini.google.com/app?hl=pt-BR"
        private const val FILE_CHOOSER_REQUEST = 3101
        private const val WEB_PERMISSION_REQUEST = 3102

        private val ALLOWED_NAVIGATION_HOSTS = setOf(
            "gemini.google.com",
            "accounts.google.com",
            "myaccount.google.com",
            "consent.google.com"
        )

        private const val HARDENING_SCRIPT = """
            (function () {
              try {
                var viewport = document.querySelector('meta[name="viewport"]');
                if (!viewport) {
                  viewport = document.createElement('meta');
                  viewport.name = 'viewport';
                  document.head.appendChild(viewport);
                }
                viewport.setAttribute('content', 'width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no');
                var stopContext = function (event) { event.preventDefault(); };
                document.addEventListener('contextmenu', stopContext, true);
                document.addEventListener('gesturestart', stopContext, { passive: false });
                document.addEventListener('gesturechange', stopContext, { passive: false });
                document.addEventListener('gestureend', stopContext, { passive: false });
                document.addEventListener('touchmove', function (event) {
                  if (event.touches && event.touches.length > 1) event.preventDefault();
                }, { passive: false, capture: true });
              } catch (ignored) {}
            })();
        """
    }
}
