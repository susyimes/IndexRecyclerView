package com.tlz.indexrecyclerview

import android.graphics.Canvas
import android.graphics.Rect
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

/**
 * Created by Tomlezen.
 * Data: 2018/7/20.
 * Time: 9:52.
 */
class NonStickyRecyclerHeadersDecoration(
    private val adapter: StickyRecyclerHeadersAdapter<RecyclerView.ViewHolder>,
    private val renderInline: Boolean = false
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        val position = parent.getChildAdapterPosition(view)
        if (position != RecyclerView.NO_POSITION && shouldDrawHeader(parent, position)) {
            val header = getHeader(parent, position).itemView
            outRect.top = getHeaderHeightForLayout(header)
        } else {
            outRect.top = 0
        }
    }

    override fun onDrawOver(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        for (i in 0 until parent.childCount) {
            val child = parent.getChildAt(i)
            val position = parent.getChildAdapterPosition(child)

            if (position != RecyclerView.NO_POSITION && shouldDrawHeader(parent, position)) {
                val headerHolder = getHeader(parent, position)
                drawHeader(canvas, child, headerHolder.itemView)
            }
        }
    }

    private fun shouldDrawHeader(parent: RecyclerView, position: Int): Boolean {
        return hasHeader(position) && (position == 0 || adapter.getHeaderId(position) != adapter.getHeaderId(position - 1))
    }

    private fun drawHeader(canvas: Canvas, child: View, header: View) {
        canvas.save()
        val left = child.left
        val top = child.top - header.height
        canvas.translate(left.toFloat(), top.toFloat())
        header.draw(canvas)
        canvas.restore()
    }

    private fun hasHeader(position: Int): Boolean = adapter.getHeaderId(position) != -1L

    private fun getHeader(parent: RecyclerView, position: Int): RecyclerView.ViewHolder {
        val holder = adapter.onCreateHeaderViewHolder(parent)
        adapter.onBindHeaderViewHolder(holder, position)
        layoutHeader(parent, holder.itemView)
        return holder
    }

    private fun layoutHeader(parent: RecyclerView, header: View) {
        val widthSpec = View.MeasureSpec.makeMeasureSpec(parent.measuredWidth, View.MeasureSpec.EXACTLY)
        val heightSpec = View.MeasureSpec.makeMeasureSpec(parent.measuredHeight, View.MeasureSpec.UNSPECIFIED)
        val childWidth = ViewGroup.getChildMeasureSpec(widthSpec,
            parent.paddingLeft + parent.paddingRight, header.layoutParams.width)
        val childHeight = ViewGroup.getChildMeasureSpec(heightSpec,
            parent.paddingTop + parent.paddingBottom, header.layoutParams.height)
        header.measure(childWidth, childHeight)
        header.layout(0, 0, header.measuredWidth, header.measuredHeight)
    }

    private fun getHeaderHeightForLayout(header: View): Int = header.height
}
