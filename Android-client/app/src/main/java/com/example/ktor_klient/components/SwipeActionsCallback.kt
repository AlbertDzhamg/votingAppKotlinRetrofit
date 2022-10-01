package com.example.ktor_klient.components

import android.content.Context
import android.graphics.*
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.ktor_klient.R

abstract class SwipeActionsCallback internal constructor(mContext: Context) :
    ItemTouchHelper.Callback() {
    private val mClearPaint: Paint = Paint()
    private val mBackground: ColorDrawable = ColorDrawable()
    private val editDrawable: Drawable?
    private val deleteDrawable: Drawable?
    private val intrinsicWidth: Int
    private val intrinsicHeight: Int

    init {
        mClearPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
        editDrawable = ContextCompat.getDrawable(mContext, R.drawable.ic_edit)
        deleteDrawable = ContextCompat.getDrawable(mContext, R.drawable.ic_delete)
        intrinsicWidth = editDrawable!!.intrinsicWidth
        intrinsicHeight = editDrawable.intrinsicHeight
    }

    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int = makeMovementFlags(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT)

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        viewHolder1: RecyclerView.ViewHolder
    ): Boolean = false

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        super.onChildDraw(c, recyclerView, viewHolder, dX/ 4, dY, actionState, isCurrentlyActive)

        val itemView = viewHolder.itemView

        val iconTop = itemView.top + (itemView.height - intrinsicHeight) / 2
        val iconMargin = (itemView.height - intrinsicHeight) / 2
        val iconBottom = iconTop + intrinsicHeight

        if (dX < 0f) {
            mBackground.color = Color.parseColor("#b80f0a")
            mBackground.setBounds(
                itemView.right + dX.toInt(),
                itemView.top,
                itemView.right,
                itemView.bottom
            )
            mBackground.draw(c)

            val iconLeft = itemView.right - iconMargin - intrinsicWidth
            val iconRight = itemView.right - iconMargin
            deleteDrawable!!.setBounds(iconLeft, iconTop, iconRight, iconBottom)
            deleteDrawable.draw(c)
        } else if (dX > 0f) {
            mBackground.color = Color.parseColor("#FFD600")
            mBackground.setBounds(
                itemView.left,
                itemView.top,
                itemView.left + dX.toInt(),
                itemView.bottom
            )
            mBackground.draw(c)
            val iconLeft = itemView.left + iconMargin
            val iconRight = itemView.left + intrinsicWidth + iconMargin
            editDrawable!!.setBounds(iconLeft, iconTop, iconRight, iconBottom)
            editDrawable.draw(c)
        }
        super.onChildDraw(c, recyclerView, viewHolder, dX / 4, dY, actionState, isCurrentlyActive)

    }

    private fun clearCanvas(c: Canvas, left: Float, top: Float, right: Float, bottom: Float) {
        c.drawRect(left, top, right, bottom, mClearPaint)
    }

    override fun getSwipeThreshold(viewHolder: RecyclerView.ViewHolder): Float {
        return 0.5f
    }
}