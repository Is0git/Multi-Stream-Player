package com.android.multistreamplayer.settings

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import com.android.multistreamplayer.R
import com.android.multistreamplayer.settings.animations.ExpandAnimation
import com.android.multistreamplayer.util.ScreenUnit
import com.google.android.material.textview.MaterialTextView

class SettingsLayout : LinearLayout {
    constructor(context: Context?) : super(context) {init(context)}
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {init(context)}
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {init(context)}

    var expandAnimation: ExpandAnimation? = null
    var backButton: ImageButton? = null

    private fun init(context: Context?, attrs: AttributeSet? = null) {
        orientation = VERTICAL

        addBackButton()
        addHeader("Quality")
    }

    private fun addBackButton() {
        val backButton = ImageButton(context).apply {
            val buttonSizeInPx = ScreenUnit.convertDpToPixel(30f, context).toInt()
            id = R.id.back_button
            layoutParams = LayoutParams(buttonSizeInPx, buttonSizeInPx)
            background = ResourcesCompat.getDrawable(context?.resources!!, R.drawable.back_icon, context?.theme)
        }
        addView(backButton)
    }

    private fun addHeader(headerName: String) {
        val view = MaterialTextView(context, null, R.style.TextAppearance_AppCompat_Body1).apply {
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
            text = headerName
        }
        addView(view)
    }

    override fun onViewAdded(child: View?) {
       if(child?.id == R.id.back_button) {
           child.setOnClickListener { expandAnimation?.playAnimation(this, rootView = this.parent as ConstraintLayout) }
       }
    }

}