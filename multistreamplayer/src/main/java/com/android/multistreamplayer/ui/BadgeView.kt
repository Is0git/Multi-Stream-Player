package com.android.multistreamplayer.ui

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import androidx.core.graphics.scale
import com.android.multistreamplayer.R
import com.bumptech.glide.Glide
import kotlinx.coroutines.*

class BadgeView : View {

    var badgeSize: Float = 0f
    var badgeMarginLength: Float = 5f
    var badgePaint = Paint()

    private var listOfBadges: List<Bitmap>? = null

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

    private fun init(context: Context?, attrs: AttributeSet? = null) {
        attrs?.apply {
            val typedArray = context?.obtainStyledAttributes(attrs, R.styleable.BadgeView)
            badgeSize = typedArray?.getDimension(R.styleable.BadgeView_size, 0f)!!
            typedArray.recycle()
        }
    }

    fun addBadges(urlList: List<String>?) = CoroutineScope(Dispatchers.Default).launch {
        val jobs = urlList?.map {
            async(Dispatchers.IO) {
                Glide.with(context).asBitmap().load(it).submit().get().scale(badgeSize.toInt(), badgeSize.toInt())
            }
        }
        jobs?.awaitAll().also { listOfBadges = it }
        withContext(Dispatchers.Main) { invalidate() }
    }

    override fun onDraw(canvas: Canvas?) {
        listOfBadges?.forEachIndexed { index, bitmap ->
            canvas?.drawBitmap(bitmap, (index * badgeSize) + badgeMarginLength, 0f, badgePaint)
        }
    }


}