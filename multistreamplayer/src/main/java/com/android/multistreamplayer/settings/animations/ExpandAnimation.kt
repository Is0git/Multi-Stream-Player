package com.android.multistreamplayer.settings.animations

import android.content.Context
import android.transition.TransitionInflater
import android.transition.TransitionManager
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout

class ExpandAnimation(context: Context, transitionId: Int) {

    var transition = TransitionInflater.from(context).inflateTransition(transitionId)

    fun playAnimation(vararg view: View?, rootView: ConstraintLayout) {

        TransitionManager.beginDelayedTransition(rootView, transition)
            view.forEach {
                if (it?.visibility == View.INVISIBLE) expand(it) else hide(it)
            }
    }

    private fun expand(view: View?) {
        view?.apply {
            visibility = View.VISIBLE
           layoutParams = ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.MATCH_CONSTRAINT).apply {
                this.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
                this.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
                this.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
                this.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
            }
        }

    }

    private fun hide(view: View?) {
        view?.apply {
            layoutParams = ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, 0).apply {
                this.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
                this.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
                this.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
                this.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
            }
            visibility = View.VISIBLE
        }

    }
}