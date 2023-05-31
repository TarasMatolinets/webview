package com.example.webviewtest.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow<UIState>(UIState.Loading)
    val uiState: StateFlow<UIState> = _uiState.asStateFlow()
    private val linkArray = listOf(
        "https://www.amazon.com/-/de/dp/B07N3RFXRL/ref=pd_ci_mcx_mh_ci_mcx_mr_mp_m_0?pd_rd_w=C8JmI&content-id=amzn1.sym.07889413-fea5-4a13-b06c-d39edf4aa03e&pf_rd_p=07889413-fea5-4a13-b06c-d39edf4aa03e&pf_rd_r=49PVZJ7PQETKW6WJ8R3N&pd_rd_wg=I909t&pd_rd_r=74e6ce3d-f18f-4e84-b9a5-b9073478d5e0&pd_rd_i=B07N3RFXRL&th=1",
        "https://www.youtube.com/watch?v=LbWjVNjlpjA",
        "https://www.spiegel.de/"
    )
    private var linkCounter = 0

    fun loadLink(event: UIEvent) {
        findLinkId(event)
        _uiState.value = UIState.Result(linkArray[linkCounter])
    }

    private fun findLinkId(event: UIEvent) {
        when (event) {
            UIEvent.PREV -> {
                linkCounter = if (linkCounter == 0) {
                    2
                } else {
                    linkCounter.dec()
                }
            }
            UIEvent.NEXT -> {
                linkCounter = if (linkCounter == 2) {
                    0
                } else {
                    linkCounter.inc()
                }
            }
            else -> {
                linkCounter = 0
            }
        }
    }

    sealed class UIState {
        object Loading : UIState()
        data class Result(val url: String) : UIState()
    }

    enum class UIEvent {
        NONE, PREV, NEXT
    }
}