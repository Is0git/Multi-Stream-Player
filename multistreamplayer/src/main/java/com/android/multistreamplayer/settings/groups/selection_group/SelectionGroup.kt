package com.android.multistreamplayer.settings.groups.selection_group

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Typeface
import android.os.Build
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.res.ResourcesCompat
import com.android.multistreamplayer.R
import com.android.multistreamplayer.settings.SettingsLayout
import com.android.multistreamplayer.settings.groups.Group
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textview.MaterialTextView
import de.hdodenhof.circleimageview.CircleImageView
import java.lang.IllegalStateException

class SelectionGroup : Group {
    constructor(headerText: TextView, items: List<MaterialCardView>?) : super(headerText, items)
    constructor(
        headerText: TextView,
        items: List<MaterialCardView>?,
        itemOnClickListener: OnItemClickListener
    ) : super(headerText, items, itemOnClickListener)

    constructor(
        headerText: TextView,
        items: List<MaterialCardView>?,
        onClick: ((view: MaterialCardView, position: Int) -> Unit)?
    ) : super(headerText, items, onClick)

    var selectedItemPosition = 0
        set(value) {
            items?.also {
                if (value < 0 && value >= it.count()) throw ArrayIndexOutOfBoundsException("select position must be in bounds of items")
                getSelectedIcon(value).visibility = View.VISIBLE
            }
        }

    var selectionListener: SelectionListener? = null
    var onSelect: ((selectionGroup: SelectionGroup, position: Int) -> Unit)? = null

    private fun getSelectedIcon(position: Int) : ImageView {
      return items?.get(position)?.findViewById(R.id.selectionIcon) ?: throw IllegalStateException("couldn't find view")
    }

    class Builder(context: Context) : Group.Builder<Builder, SelectionGroup>(context) {

        var selectionListener: SelectionListener? = null
        var onSelect: ((selectionGroup: SelectionGroup, position: Int) -> Unit)? = null

        fun addSelectionListener(selectionListener: SelectionListener) : Builder {
            this.selectionListener = selectionListener
            return this
        }

        fun addSelectionListener(onSelect: (selectionGroup: SelectionGroup, position: Int) -> Unit) : Builder {
            this.onSelect = onSelect
            return this
        }

        override fun createCustomCardView(
            drawableId: Int,
            topText: String,
            bottomText: String,
            card: MaterialCardView
        ): MaterialCardView {
            val constraintLayout = ConstraintLayout(context).apply {
                id = R.id.textThree
                val itemImage = CircleImageView(context).also { circleImage ->
                    circleImage.id = R.id.itemImage
                    circleImage.borderWidth = 2
                    circleImage.borderColor = Color.parseColor("#000000")
                    circleImage.setImageDrawable(ResourcesCompat.getDrawable(resources, drawableId, context?.theme))
                }

                val textOne = MaterialTextView(context, null, R.attr.textAppearanceBody1).apply {
                    id = R.id.textOne
                    setTypeface(null, Typeface.BOLD)
                    text = topText
                }

                val textTwo = MaterialTextView(context, null, R.attr.textAppearanceBody2).apply {
                    id = R.id.textTwo
                    text = bottomText
                }

                val selectionIcon = ImageView(context).apply {
                    id = R.id.selectionIcon
                    scaleType = ImageView.ScaleType.FIT_CENTER
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        imageTintList = ColorStateList.valueOf(Color.GREEN)
                    }
                    setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.selection_icon, context?.theme))
                }

                setConstraintSet(ConstraintSet().also { it.clone(context, R.layout.settings_item_layout) })

                addView(itemImage)
                addView(textOne)
                addView(textTwo)
                addView(selectionIcon)
            }
            card.addView(constraintLayout)
            return card
        }



        class SelectionGroupItem(
            topText: String,
            bottomText: String,
            imageDrawableId: Int,
            var isSelected: Boolean
        ) : Group.Builder.GroupItem(topText, bottomText, imageDrawableId) {
        }

        override fun customBuild(
            headerText: MaterialTextView,
            items: List<MaterialCardView>?
        ): SelectionGroup {
            val selectionGroup = when {
                onClick != null -> SelectionGroup(
                    headerText,
                    items,
                    onClick
                )

                else -> SelectionGroup(
                    headerText,
                    items
                )
            }

            return selectionGroup.apply {
                this.onSelect = this@Builder.onSelect
                this.selectionListener = this@Builder.selectionListener
                    items?.forEachIndexed { index, materialCardView ->
                        if (selectionListener != null) materialCardView.setOnClickListener { selectionListener!!.onItemSelect(this, index)}
                        else if (onSelect != null) materialCardView.setOnClickListener { onSelect?.invoke(this, index) }
                }
            }
        }

        override fun getThis(): Builder {
            return this
        }
    }
}

