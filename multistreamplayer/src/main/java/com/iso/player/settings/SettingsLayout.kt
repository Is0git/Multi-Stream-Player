package com.iso.player.settings

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.core.content.res.ResourcesCompat
import com.google.android.material.textview.MaterialTextView
import com.iso.player.R
import com.iso.player.settings.animations.ExpandAnimation
import com.iso.player.settings.groups.Group
import com.iso.player.util.ScreenUnit

class SettingsLayout : LinearLayout {
    constructor(context: Context?) : super(context) {
        init(context)
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init(context)
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(context)
    }

    init {
        isFocusable = true
        isFocusableInTouchMode = true
    }

    companion object {
        const val TOP_CARD = 0
        const val MIDDLE_CARD = 1
        const val BOTTOM_CARD = 2
    }
    val groups: MutableMap<String, Group> by lazy { mutableMapOf<String, Group>() }
    val marginStartGuideline = ScreenUnit.convertDpToPixel(32f, context).toInt()
    val componentMargin = ScreenUnit.convertDpToPixel(32f, context).toInt()
    var isExpanded = false
    var expandAnimation: ExpandAnimation? = null
    var backButton: ImageButton? = null

    private fun init(context: Context?, attrs: AttributeSet? = null) {
        orientation = VERTICAL
        addBackButton()
        addHeaderView("SETTINGS")
    }

    private fun addHeaderView(headerName: String) {
        val view = MaterialTextView(context, null, R.attr.textAppearanceHeadline3  ).apply {
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT).apply {
                marginStart = marginStartGuideline
                topMargin = componentMargin
            }
            setTypeface(null, Typeface.BOLD)
            text = headerName
        }
        addView(view)
    }

    private fun addBackButton() {
        val backButton = ImageButton(context).apply {
            val buttonSizeInPx = ScreenUnit.convertDpToPixel(30f, context).toInt()
            id = R.id.back_button
            layoutParams = LayoutParams(buttonSizeInPx, buttonSizeInPx).apply {
                topMargin = marginStartGuideline
                marginStart = componentMargin
            }
            background = ResourcesCompat.getDrawable(
                context?.resources!!,
                R.drawable.back_icon,
                context?.theme
            )
        }
        addView(backButton)
    }

    fun addGroup(group: Group) {
        addView(group.headerText)
        group.items?.forEachIndexed {index, card ->
            addView(card)
        }
        groups[group.headerText.text.toString()] = group
    }

    override fun onViewAdded(child: View?) {
        if (child?.id == R.id.back_button) {
            backButton = child as ImageButton
        }
    }
}
