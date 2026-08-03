package com.firstapp.myapplication.utils

import android.view.MotionEvent
import android.view.View
import android.view.animation.DecelerateInterpolator

/**
 * Small, reusable interaction animations that make the app feel polished:
 *
 * - [pressFeedback] scales a button / FAB down slightly while it is pressed
 *   and back to normal on release. The Material ripple already provides the
 *   ink effect; this adds a subtle physical "push" on top of it.
 * - [animateItemIn] fades and slides a RecyclerView item up the first time it
 *   appears, so lists animate in instead of popping abruptly.
 */
object UiAnimations {

    private val interpolator = DecelerateInterpolator()

    /**
     * Attaches a touch listener that scales [view] to [pressedScale] while the
     * user holds it, giving instant tactile feedback on top of the ripple.
     * The event is never consumed, so existing click listeners keep working.
     */
    fun pressFeedback(
        view: View,
        pressedScale: Float = 0.92f,
        duration: Long = 110L
    ) {
        view.setOnTouchListener { v, event ->
            when (event.actionMasked) {
                MotionEvent.ACTION_DOWN ->
                    v.animate()
                        .scaleX(pressedScale)
                        .scaleY(pressedScale)
                        .setDuration(duration)
                        .start()

                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL ->
                    v.animate()
                        .scaleX(1f)
                        .scaleY(1f)
                        .setDuration(duration)
                        .start()
            }
            // Never consume the event so click listeners still fire.
            false
        }
    }

    /**
     * Plays a subtle fade + slide-up entrance for a RecyclerView item, but only
     * the first time that item id is displayed (tracked in [animatedIds]).
     * Re-binds of already-animated items are left untouched, so scrolling and
     * DiffUtil updates never re-trigger the animation.
     */
    fun animateItemIn(itemView: View, animatedIds: MutableSet<Long>, itemId: Long) {
        if (!animatedIds.add(itemId)) {
            // A recycled view may still carry partial state from a cancelled
            // entrance animation; reset it so the item is never stuck invisible.
            itemView.alpha = 1f
            itemView.translationY = 0f
            return
        }

        itemView.alpha = 0f
        itemView.translationY = itemView.resources.displayMetrics.density * 24f
        itemView.animate()
            .alpha(1f)
            .translationY(0f)
            .setDuration(320L)
            .setInterpolator(interpolator)
            .start()
    }
}
