package com.example.webviewtest

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.ProgressBar
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
    private lateinit var progress: ProgressBar

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
                        is MainViewModel.UIState.Empty -> {
                            viewModel.loadLink(MainViewModel.UIEvent.NONE)
                        }
                        is MainViewModel.UIState.Loading -> {
                            handleContentVisibility(false)
                        }
                        is MainViewModel.UIState.Result -> {
                            handleContentVisibility(true)
                            viewContent.loadUrl(state.url)
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

            override fun onPageFinished(view: WebView, url: String) {
                btNext.visibility = View.VISIBLE
                btPrev.visibility = View.VISIBLE
            }
        }
    }

    private fun handleContentVisibility(isContentVisible: Boolean = false) {
        if (isContentVisible) {
            viewContent.visibility = View.VISIBLE
            progress.visibility = View.GONE
        } else {
            btNext.visibility = View.GONE
            btPrev.visibility = View.GONE
            viewContent.visibility = View.GONE
            progress.visibility = View.VISIBLE
        }
    }

    private fun initViews() {
        viewContent = findViewById(R.id.web_view_content)
        btPrev = findViewById(R.id.bt_prev)
        btNext = findViewById(R.id.bt_next)
        progress = findViewById(R.id.progress)
    }
}