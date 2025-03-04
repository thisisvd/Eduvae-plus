package com.digitalinclined.edugate.utils

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class DotItemDecorator(val count: Int) : RecyclerView.ItemDecoration() {

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDrawOver(c, parent, state)
        val width = parent.width
        val height = parent.height
        val position = (parent.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
        drawDotsToItems(c, position, width / 2.toFloat() - 25 / 2.toFloat(), height - 30.toFloat())
    }

    // custom draw function
    private fun drawDotsToItems(c: Canvas, position: Int, arbitraryX: Float, arbitraryY: Float) {

        val factor = 50
        when (position) {
            0 -> {
                drawBottomRect(true, arbitraryX - factor, arbitraryY, c)
                drawBottomRect(false, arbitraryX, arbitraryY, c) // middle
                drawBottomRect(false, arbitraryX + factor, arbitraryY, c)
            }

            1 -> {
                drawBottomRect(false, arbitraryX - factor, arbitraryY, c)
                drawBottomRect(true, arbitraryX, arbitraryY, c) // middle
                drawBottomRect(false, arbitraryX + factor, arbitraryY, c)
            }

            2 -> {
                drawBottomRect(false, arbitraryX - factor, arbitraryY, c)
                drawBottomRect(false, arbitraryX, arbitraryY, c) // middle
                drawBottomRect(true, arbitraryX + factor, arbitraryY, c)
            }
        }
    }

    private fun drawBottomRect(dark: Boolean, positionX: Float, positionY: Float, c: Canvas) {
        val rectWidth = 25f
        val rectHeight = 15f
        if (dark) {
            val r = RectF(positionX, positionY, positionX + rectWidth, positionY + rectHeight)
            c.drawRoundRect(r, 3f, 3f, Paint().apply {
                color = Color.YELLOW
            })
        } else {
            val r = RectF(positionX, positionY, positionX + rectWidth, positionY + rectHeight)
            c.drawRoundRect(r, 3f, 3f, Paint().apply {
                color = Color.parseColor("#80FFFFFF")
            })
        }
    }
}