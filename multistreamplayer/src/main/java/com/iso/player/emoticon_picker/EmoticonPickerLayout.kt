package com.iso.player.emoticon_picker

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.ProgressBar
import android.widget.SearchView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.ViewCompat
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.iso.player.R
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*

class EmoticonPickerLayout : ConstraintLayout {

    constructor(context: Context?) : super(context) {
        init(context)
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init(context, attrs)
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(context, attrs)
    }

    init {
        isFocusableInTouchMode = true
        isFocusable = true
    }

    var onSearchTextChange: ((text: String?) -> Unit)? = null
    private var emotesSearchBar: SearchView? = null
    var emotesTabLayout: TabLayout? = null
    var emotesViewPager: ViewPager2? = null
    var onSearchClickJob: Job? = null
    var onSearchTextChangeJob: Job? = null
    lateinit var progressBar: ProgressBar

    @ExperimentalCoroutinesApi
    private fun init(context: Context?, attrs: AttributeSet? = null) {
        emotesSearchBar = SearchView(context).also {
            it.id = R.id.emote_search_bar
            it.isIconified = true
            it.imeOptions = EditorInfo.IME_ACTION_DONE or EditorInfo.IME_FLAG_NO_EXTRACT_UI
            it.clearFocus()
            onSearchClickJob = CoroutineScope(Dispatchers.Main).launch {
                callbackFlow {
                    it.setOnSearchClickListener {
                        offer(Unit)
                    }
                    awaitClose { it.setOnSearchClickListener(null) }
                }
                    .onEach {
                        delay(100)
                        emotesViewPager?.setCurrentItem(
                            emotesViewPager?.adapter?.itemCount?.minus(1) ?: 0, true
                        )
                    }
                    .buffer(Channel.CONFLATED)
                    .collect()
            }
            onSearchTextChangeJob = CoroutineScope(Dispatchers.Main).launch {
                callbackFlow<String?> {
                    it.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                        override fun onQueryTextSubmit(query: String?): Boolean {
                            progressBar.visibility = View.VISIBLE
                            if (!query.isNullOrEmpty()) offer(query) else offer(" ")
                            return true
                        }

                        override fun onQueryTextChange(newText: String?): Boolean {
                            progressBar.visibility = View.VISIBLE
                            if (!newText.isNullOrEmpty()) offer(newText) else offer(" ")
                            return false
                        }

                    })
                    awaitClose { it.setOnQueryTextListener(null) }
                }
                    .buffer(Channel.CONFLATED)
                    .onEach { text ->
                        onSearchTextChange?.invoke(text)
                        withContext(Dispatchers.Main) { progressBar.visibility = View.INVISIBLE }
                    }
                    .launchIn(this)
            }

            it.setOnCloseListener {
                emotesViewPager?.setCurrentItem(0, false)
                false
            }

        }
        progressBar = ProgressBar(context).apply {
            id = R.id.progressBar
            visibility = View.INVISIBLE
        }
        emotesTabLayout = TabLayout(context!!).also {
            it.id = R.id.emotes_tab_layout
            it.tabMode = TabLayout.MODE_SCROLLABLE
        }
        emotesViewPager = ViewPager2(context).also {
            it.id = R.id.emotes_viewpager
            ViewCompat.setNestedScrollingEnabled(it, true)
        }
        addView(emotesSearchBar)
        addView(emotesTabLayout)
        addView(emotesViewPager)
        addView(progressBar)
        ConstraintSet().apply {
            clone(context, R.layout.emotes_layout)
            setConstraintSet(this)
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return true
    }
    fun clear() {
        onSearchTextChangeJob?.cancel()
        onSearchClickJob?.cancel()
    }

}