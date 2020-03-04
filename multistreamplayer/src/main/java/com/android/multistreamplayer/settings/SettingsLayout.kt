package com.android.multistreamplayer.settings

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.res.ResourcesCompat
import com.android.multistreamplayer.R
import com.android.multistreamplayer.settings.animations.ExpandAnimation
import com.android.multistreamplayer.util.ScreenUnit
import com.google.android.material.card.MaterialCardView
import com.google.android.material.shape.CornerFamily.ROUNDED
import com.google.android.material.shape.ShapeAppearanceModel
import com.google.android.material.textview.MaterialTextView
import de.hdodenhof.circleimageview.CircleImageView

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

    companion object {
        const val TOP_CARD = 0
        const val MIDDLE_CARD = 1
        const val BOTTOM_CARD = 2
    }

    val marginStartGuideline = ScreenUnit.convertDpToPixel(32f, context).toInt()
    val componentMargin = ScreenUnit.convertDpToPixel(32f, context).toInt()
    val itemMarginTop = ScreenUnit.convertDpToPixel(8f, context).toInt()
    val headerMargin = ScreenUnit.convertDpToPixel(16f, context).toInt()
    val defaultCornerRadius = resources.getDimension(R.dimen.defaultCornerRadius)

    var expandAnimation: ExpandAnimation? = null
    var backButton: ImageButton? = null

    var cardInsetMargin = ScreenUnit.convertDpToPixel(8f, context).toInt()

    private fun init(context: Context?, attrs: AttributeSet? = null) {
        orientation = VERTICAL

        addBackButton()
        addHeader("SETTINGS")
        addComponentName("VIDEO SETTINGS")
        addItemCard(TOP_CARD)
        addItemCard(MIDDLE_CARD)
        addItemCard(MIDDLE_CARD)
        addItemCard(BOTTOM_CARD)
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

    private fun addHeader(headerName: String) {
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

    private fun addComponentName(componentName: String) {
        val view = MaterialTextView(context, null, R.attr.textAppearanceHeadline5  ).apply {
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT).apply {
                marginStart = marginStartGuideline
                topMargin = componentMargin
            }
            setTypeface(null, Typeface.BOLD)
            text = componentName
        }
        addView(view)
    }

    private fun addItemCard(cardType: Int = MIDDLE_CARD) {

        var topLeftCornerRadius = 0f
        var topRightCornerRadius = 0f
        var bottomLeftCornerRadius = 0f
        var bottomRightCornerRadius = 0f
        var topMargin = 0
        var bottomMargin = 2

        when(cardType) {
            TOP_CARD -> {
                topLeftCornerRadius = defaultCornerRadius
                topRightCornerRadius = defaultCornerRadius
                topMargin = headerMargin
            }
            BOTTOM_CARD -> {
                bottomLeftCornerRadius = defaultCornerRadius
                bottomRightCornerRadius = defaultCornerRadius
                bottomMargin = cardInsetMargin
            }
        }

        val card = MaterialCardView(context).apply {
            shapeAppearanceModel = ShapeAppearanceModel().toBuilder()
                .setTopLeftCorner(ROUNDED, topLeftCornerRadius)
                .setTopRightCorner(ROUNDED, topRightCornerRadius)
                .setBottomLeftCorner(ROUNDED, bottomLeftCornerRadius)
                .setBottomRightCorner(ROUNDED, bottomRightCornerRadius)
                .build()
            cardElevation = 4f
            layoutParams = MarginLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT).apply {
                this.topMargin = topMargin
                marginStart = marginStartGuideline
                marginEnd = marginStartGuideline
                this.bottomMargin = bottomMargin
            }
        }

        val constraintLayout = ConstraintLayout(context).apply {
            id = R.id.textThree
            val itemImage = CircleImageView(context).also {circleImage ->
                circleImage.id = R.id.itemImage
                circleImage.borderWidth = 2
                circleImage.borderColor = Color.parseColor("#000000")
                circleImage.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.full_hd_icon, context?.theme))
            }

            val textOne = MaterialTextView(context, null, R.attr.textAppearanceBody1).apply {
                id = R.id.textOne
                setTypeface(null, Typeface.BOLD)
                text = "TEXTONE"
            }

            val textTwo = MaterialTextView(context, null, R.attr.textAppearanceBody2).apply {
                id = R.id.textTwo
                text = "TEXTONE"
            }

            val selectionIcon = ImageView(context).apply {
                id = R.id.selectionIcon
                scaleType = ImageView.ScaleType.FIT_CENTER
                imageTintList = ColorStateList.valueOf(Color.GREEN)
                setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.selection_icon, context?.theme))
            }

            setConstraintSet(ConstraintSet().also { it.clone(context, R.layout.settings_item_layout) })

            addView(itemImage)
            addView(textOne)
            addView(textTwo)
            addView(selectionIcon)
        }
        card.addView(constraintLayout)
        addView(card)
    }

    override fun onViewAdded(child: View?) {
        if (child?.id == R.id.back_button) {
            backButton = child as ImageButton
        }
    }

}