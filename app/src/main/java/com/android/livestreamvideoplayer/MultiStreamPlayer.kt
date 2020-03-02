package com.android.livestreamvideoplayer

import android.content.Context
import android.util.AttributeSet
import android.widget.VideoView

class MultiStreamPlayer : VideoView {
    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    fun init() {

    }

}