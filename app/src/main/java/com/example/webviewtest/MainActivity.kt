package com.example.webviewtest

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.webviewtest.viewmodel.MainViewModel
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {
    private lateinit var btPrev: Button
    private lateinit var btNext: Button
    private lateinit var viewContent: WebView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViews()
        configureWebView()
        val viewModel: MainViewModel by viewModels()
        handleViewEvents(viewModel)
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    when (state) {
                        is MainViewModel.UIState.Loading -> {
                            viewModel.loadLink(MainViewModel.UIEvent.NONE)
                        }
                        is MainViewModel.UIState.Result -> {
                            viewContent.loadUrl(state.url);
                        }
                    }
                }
            }
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun configureWebView() {
        viewContent.settings.javaScriptEnabled = true
    }

    private fun handleViewEvents(viewModel: MainViewModel) {
        btPrev.setOnClickListener {
            viewModel.loadLink(MainViewModel.UIEvent.PREV)
        }
        btNext.setOnClickListener {
            viewModel.loadLink(MainViewModel.UIEvent.NEXT)
        }
        viewContent.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                handleContentVisibility(false)
            }
            override fun onPageFinished(view: WebView, url: String) {
                handleContentVisibility(true)
            }
        }
    }

    private fun handleContentVisibility(isContentVisible: Boolean = false) {
        if (isContentVisible) {
            btPrev.visibility = View.VISIBLE
            btNext.visibility = View.VISIBLE
        } else {
            btPrev.visibility = View.GONE
            btNext.visibility = View.GONE
        }
    }


    private fun initViews() {
        viewContent = findViewById(R.id.web_view_content)
        btPrev = findViewById(R.id.bt_prev)
        btNext = findViewById(R.id.bt_next)
    }
}