package com.android.multistreamplayer.settings.groups

import android.content.Context
import android.graphics.Typeface
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.android.multistreamplayer.R
import com.android.multistreamplayer.settings.SettingsLayout
import com.android.multistreamplayer.util.ScreenUnit
import com.google.android.material.card.MaterialCardView
import com.google.android.material.shape.CornerFamily
import com.google.android.material.shape.ShapeAppearanceModel
import com.google.android.material.textview.MaterialTextView
import java.util.*


abstract class Group(val headerText: TextView, var items: List<MaterialCardView>? = null) {

    var onClick: ((view: MaterialCardView, position: Int) -> Unit)? = null
    var onItemClickListener: OnItemClickListener? = null

    constructor(
        headerText: TextView,
        items: List<MaterialCardView>? = null,
        itemOnClickListener: OnItemClickListener? = null
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

    abstract class Builder<out K, out T : Group>(val context: Context) {
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

        fun build(): T {
            headerText ?: throw NullPointerException("headerText can't be null")
            val headerTextView = createComponentNameView(headerText!!)

            this.groupItems?.forEachIndexed { index, groupItem ->
                val cardType = when {
                    index == 0 -> SettingsLayout.TOP_CARD
                    index > 0 && index < this.groupItems!!.count() - 1 -> SettingsLayout.MIDDLE_CARD
                    else -> SettingsLayout.BOTTOM_CARD
                }
                createItemCardView(
                    cardType,
                    groupItem?.imageDrawableId!!,
                    groupItem.topText,
                    groupItem.bottomText
                ).also { card ->
                    if (onClick != null) card.setOnClickListener {
                       onClick!!.invoke(card, index)
                    }
                    else if(onClickListener != null) card.setOnClickListener {
                        onClickListener!!.onItemClick(card, index)
                    }
                    items?.add(card) }
            }

            return customBuild(headerTextView, items).apply {
                this.onItemClickListener = this@Builder.onClickListener
                this.onClick = this@Builder.onClick
            }
        }

        abstract fun getThis() : K

        abstract fun customBuild(headerText: MaterialTextView, items: List<MaterialCardView>?) : T

        fun addHeader(headerText: String): K {
            if (headerText.isBlank()) throw IllegalStateException("header text cannot be empty")
            this.headerText = headerText.toUpperCase(Locale.getDefault())
            return getThis()
        }

        fun addOnClickListener(onClick: (view: MaterialCardView, position: Int) -> Unit): K {
            this.onClick = onClick
            return getThis()
        }

        fun addOnClickListener(onClickListener: OnItemClickListener): K {
            this.onClickListener = onClickListener
            return getThis()
        }

        fun addItems(groupItems: Array<out GroupItem?>): K {
            if (groupItems.isEmpty()) return getThis() else this.groupItems = groupItems
            return getThis()
        }

        private fun createComponentNameView(componentName: String): MaterialTextView {
            return MaterialTextView(context, null, R.attr.textAppearanceHeadline5).apply {
                layoutParams =
                    LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply {
                        marginStart = marginStartGuideline
                        topMargin = componentMargin
                    }
                setTypeface(null, Typeface.BOLD)
                text = componentName
            }
        }

        abstract fun createCustomCardView(drawableId: Int, topText: String, bottomText: String, card: MaterialCardView) : MaterialCardView

        open fun createItemCardView(
            cardType: Int = SettingsLayout.MIDDLE_CARD,
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
                SettingsLayout.TOP_CARD -> {
                    topLeftCornerRadius = defaultCornerRadius
                    topRightCornerRadius = defaultCornerRadius
                    topMargin = headerMargin
                }
                SettingsLayout.BOTTOM_CARD -> {
                    bottomLeftCornerRadius = defaultCornerRadius
                    bottomRightCornerRadius = defaultCornerRadius
                    bottomMargin = cardInsetMargin
                }
            }

            val card = MaterialCardView(context).apply {
                shapeAppearanceModel = ShapeAppearanceModel().toBuilder()
                    .setTopLeftCorner(CornerFamily.ROUNDED, topLeftCornerRadius)
                    .setTopRightCorner(CornerFamily.ROUNDED, topRightCornerRadius)
                    .setBottomLeftCorner(CornerFamily.ROUNDED, bottomLeftCornerRadius)
                    .setBottomRightCorner(CornerFamily.ROUNDED, bottomRightCornerRadius)
                    .build()
                cardElevation = 4f
                layoutParams = ViewGroup.MarginLayoutParams(
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