package com.android.multistreamplayer.settings

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import com.android.multistreamplayer.R
import com.google.android.material.textview.MaterialTextView

class SettingsLayout : LinearLayout {
    constructor(context: Context?) : super(context) {init(context)}
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {init(context)}
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {init(context)}

    fun init(context: Context?, attrs: AttributeSet? = null) {
        orientation = VERTICAL
        addHeader("Quality")
    }

    fun addHeader(name: String) {
        val view = MaterialTextView(context, null, R.style.TextAppearance_AppCompat_Body1).apply {
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
            text = name
        }
        addView(view)
    }

}