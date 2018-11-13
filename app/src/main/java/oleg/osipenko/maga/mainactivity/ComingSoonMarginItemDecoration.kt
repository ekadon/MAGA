package oleg.osipenko.maga.mainactivity

import android.content.Context
import android.graphics.Rect
import android.support.annotation.DimenRes
import android.support.v7.widget.RecyclerView
import android.view.View

class ComingSoonMarginItemDecoration(private val pixels: Int): RecyclerView.ItemDecoration() {
    constructor(context: Context, @DimenRes id: Int) : this(context.resources.getDimensionPixelSize(id))

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state);

        // Add left and right margin only for the first item and the last items accordingly
        // to avoid double space between items
        if (parent.getChildAdapterPosition(view) != 0) outRect.left = pixels
    }
}
