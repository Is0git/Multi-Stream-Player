package com.android.multistreamplayer.settings

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import com.android.multistreamplayer.R
import com.android.multistreamplayer.settings.animations.ExpandAnimation
import com.android.multistreamplayer.settings.groups.SelectionGroup
import com.android.multistreamplayer.util.ScreenUnit
import com.google.android.material.card.MaterialCardView
import com.google.android.material.shape.CornerFamily.ROUNDED
import com.google.android.material.shape.ShapeAppearanceModel
import com.google.android.material.textview.MaterialTextView
import java.lang.IllegalStateException
import java.lang.NullPointerException
import java.util.*

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
    val groups: MutableMap<String, Group> by lazy { mutableMapOf<String, Group>() }

    val marginStartGuideline = ScreenUnit.convertDpToPixel(32f, context).toInt()
    val componentMargin = ScreenUnit.convertDpToPixel(32f, context).toInt()

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
            if (group.onClick != null) card.setOnClickListener {
                group.onClick!!.invoke(card, index)
            }
            else if(group.onItemClickListener != null) card.setOnClickListener {
                group.onItemClickListener!!.onItemClick(card, index)
            }
        }
        groups[group.headerText.text.toString()] = group
    }
    override fun onViewAdded(child: View?) {
        if (child?.id == R.id.back_button) {
            backButton = child as ImageButton
        }
    }





    //Groups, items, views builder
    open class Group(val headerText: TextView, var items: List<MaterialCardView>? = null) {

        var onClick: ((view: MaterialCardView, position: Int) -> Unit)? = null
        var onItemClickListener: OnItemClickListener? = null

        constructor(
            headerText: TextView,
            items: List<MaterialCardView>? = null,
            itemOnClickListener: OnItemClickListener
        ) : this(headerText, items) {
            this.onItemClickListener = itemOnClickListener
        }

        constructor(
            headerText: TextView,
            items: List<MaterialCardView>? = null,
            onClick: ((view: MaterialCardView, position: Int) -> Unit)? = null
        ) : this(headerText, items) {
            this.onClick = onClick
        }

        abstract class Builder(val context: Context) {
            var headerText: String? = null


            protected val marginStartGuideline = ScreenUnit.convertDpToPixel(32f, context).toInt()
            private val componentMargin = ScreenUnit.convertDpToPixel(32f, context).toInt()
            protected val headerMargin = ScreenUnit.convertDpToPixel(16f, context).toInt()
            protected val defaultCornerRadius =
                context.resources.getDimension(R.dimen.defaultCornerRadius)
            protected var cardInsetMargin = ScreenUnit.convertDpToPixel(8f, context).toInt()

            var onClick: ((view: MaterialCardView, position: Int) -> Unit)? = null
            private var onClickListener: OnItemClickListener? = null

            val items: MutableList<MaterialCardView>? by lazy { mutableListOf<MaterialCardView>() }
            private var groupItems: Array<out GroupItem?>? = null

            fun build(): Group {
                headerText ?: throw NullPointerException("headerText can't be null")
                val headerTextView = createComponentNameView(headerText!!)

                this.groupItems?.forEachIndexed { index, groupItem ->
                    val cardType = when {
                        index == 0 -> TOP_CARD
                        index > 0 && index < this.groupItems!!.count() -> MIDDLE_CARD
                        else -> BOTTOM_CARD
                    }
                    createItemCardView(
                        cardType,
                        groupItem?.imageDrawableId!!,
                        groupItem.topText,
                        groupItem.bottomText
                    ).also { items?.add(it) }
                }
                return when {
                    onClick != null -> SelectionGroup(headerTextView, items, onClick)
                    onClickListener != null -> SelectionGroup(headerTextView, items, onClickListener!!)
                    else -> SelectionGroup(headerTextView, items)
                }
            }

            fun addHeader(headerText: String): Builder {
                if (headerText.isBlank()) throw IllegalStateException("header text cannot be empty")
                this.headerText = headerText.toUpperCase(Locale.getDefault())
                return this
            }

            fun addOnClickListener(onClick: (view: MaterialCardView, position: Int) -> Unit): Builder {
                this.onClick = onClick
                return this
            }

            fun addOnClickListener(onClickListener: OnItemClickListener): Builder {
                this.onClickListener = onClickListener
                return this
            }

            fun addItems(groupItems: Array<out GroupItem?>): Builder {
                if (groupItems.isEmpty()) return this else this.groupItems = groupItems
                return this
            }

            private fun createComponentNameView(componentName: String): MaterialTextView {
                return MaterialTextView(context, null, R.attr.textAppearanceHeadline5).apply {
                    layoutParams =
                        LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT).apply {
                            marginStart = marginStartGuideline
                            topMargin = componentMargin
                        }
                    setTypeface(null, Typeface.BOLD)
                    text = componentName
                }
            }

            abstract fun createCustomCardView(drawableId: Int, topText: String, bottomText: String, card: MaterialCardView) : MaterialCardView

            open fun createItemCardView(
                cardType: Int = MIDDLE_CARD,
                drawableId: Int,
                topText: String,
                bottomText: String
            ): MaterialCardView {
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
                    layoutParams = MarginLayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    ).apply {
                        this.topMargin = topMargin
                        marginStart = marginStartGuideline
                        marginEnd = marginStartGuideline
                        this.bottomMargin = bottomMargin
                    }
                }
              return createCustomCardView(drawableId, topText, bottomText, card)
            }

            open class GroupItem(
                val topText: String,
                val bottomText: String,
                val imageDrawableId: Int
            )
        }

        interface OnItemClickListener {
            fun onItemClick(view: MaterialCardView, position: Int)
        }
    }
}
