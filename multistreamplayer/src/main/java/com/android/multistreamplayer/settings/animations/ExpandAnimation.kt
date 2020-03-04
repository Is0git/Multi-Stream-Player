package com.android.multistreamplayer.settings.animations

import android.content.Context
import android.transition.TransitionInflater
import android.transition.TransitionManager
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout

class ExpandAnimation(context: Context, transitionId: Int) {

    var transition = TransitionInflater.from(context).inflateTransition(transitionId)

    var isExpanded: Boolean = false

    fun playAnimation(vararg view: View?, rootView: ConstraintLayout) {

        TransitionManager.beginDelayedTransition(rootView, transition)
            view.forEach {
                if (isExpanded) hide(it) else expand(it)
            }
    }

    private fun expand(view: View?) {
        view?.apply {
            isExpanded = true
           layoutParams = ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.MATCH_CONSTRAINT).apply {
                this.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
                this.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
                this.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
                this.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
            }
            visibility = View.VISIBLE
        }

    }

    private fun hide(view: View?) {
        view?.apply {
            isExpanded = false
            layoutParams = ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, 0).apply {
                this.topToBottom = ConstraintLayout.LayoutParams.PARENT_ID
                this.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
                this.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
            }

        }

    }
}