package com.ajt.simpletasks

import android.annotation.SuppressLint
import android.content.Context
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import kotlin.math.abs

//Copied from RapidLearning
open class GesturesDetectingTouchListener(context: Context, private val swipeThreshold: Int = 30, private val velocityThreshold: Int = 50) : View.OnTouchListener {

    private val gestureDetector = GestureDetector(context, GestureListener())

    var eventTriggered = false

    var swipeLeftDetected = false
    var swipeRightDetected = false
    var swipeUpDetected = false
    var swipeDownDetected = false

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(v: View?, event: MotionEvent) = gestureDetector.onTouchEvent(event)

    fun resetSwipeFlags() {
        swipeLeftDetected = false
        swipeRightDetected = false
        swipeUpDetected = false
        swipeDownDetected = false
        eventTriggered = false
    }

    //Open means can be overridden
    //Called when swipe left detected
    open fun onSwipeLeft() = Unit

    open fun onSwipeRight() = Unit

    open fun onSwipeUp() = Unit

    open fun onSwipeDown() = Unit

    open fun onClick() = Unit

    open fun onDoubleTap() = Unit

    open fun onLongPress() = Unit

    open fun onScroll(dx: Float, dy: Float) = Unit


    //A class nested inside of a class = an inner class
    private inner class GestureListener : GestureDetector.SimpleOnGestureListener() {

        override fun onDoubleTap(e: MotionEvent): Boolean {
            this@GesturesDetectingTouchListener.onDoubleTap()
            return false
        }

        override fun onDown(e: MotionEvent) = true

        override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
            onClick()
            return super.onSingleTapConfirmed(e)
        }

        override fun onLongPress(e: MotionEvent) = this@GesturesDetectingTouchListener.onLongPress()

        override fun onFling(e1: MotionEvent?, e2: MotionEvent?, velocityX: Float, velocityY: Float): Boolean {
            var result = false

            val diffX = (e2?.x ?: 0F) - (e1?.x ?: 0F)
            val diffY = (e2?.y ?: 0F) - (e1?.y ?: 0F)

            val absDiffX = abs(diffX)
            val absDiffY = abs(diffY)

            val absVeloX = abs(velocityX)
            val absVeloY = abs(velocityY)

            try {
                if (absDiffX > absDiffY) {
                    if (absDiffX > swipeThreshold && absVeloX > velocityThreshold) {
                        resetSwipeFlags()
                        swipeRightDetected = diffX > 0
                        swipeLeftDetected = !swipeRightDetected
                        if (swipeRightDetected) onSwipeRight() else onSwipeLeft()
                        result = true
                    }

                } else {
                    if (absDiffY > swipeThreshold && absVeloY > velocityThreshold) {
                        resetSwipeFlags()
                        swipeDownDetected = diffY > 0
                        swipeUpDetected = !swipeDownDetected
                        if (swipeDownDetected) onSwipeDown() else onSwipeUp()
                        result = true
                    }
                }
            } catch (exception: Exception) {
                exception.printStackTrace()
            }
            return result
        }

        override fun onScroll(e1: MotionEvent?, e2: MotionEvent?, distanceX: Float, distanceY: Float): Boolean {
            onScroll(distanceX, distanceY)
            return super.onScroll(e1, e2, distanceX, distanceY)
        }
    }

}