package com.android.multistreamplayer.settings.animations

import android.content.Context
import android.transition.TransitionInflater
import android.transition.TransitionManager
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout

class ExpandAnimation(context: Context, transitionId: Int, animationController: AnimationController) : AnimationController  by animationController{

    var transition = TransitionInflater.from(context).inflateTransition(transitionId)

    var isExpanded: Boolean = false

    fun playAnimation(vararg view: View?, rootView: ConstraintLayout) {

        TransitionManager.beginDelayedTransition(rootView, transition)
            view.forEach {
                if (isExpanded) hide(it, isExpanded).also { isExpanded = false } else expand(it, isExpanded).also { isExpanded = true }
            }
    }
}