package oleg.osipenko.maga.mainactivity

import android.support.v7.widget.RecyclerView
import android.view.View

abstract class MovieBaseHolder(itemView: View,
                               protected val baseUrl: String,
                               protected val sizes: List<String>) : RecyclerView.ViewHolder(itemView) {
    companion object {
        const val ORIGINAL = "original"
        const val W_PREFIX = "w"
    }

    abstract fun getImageViewWidth(): Int

    protected fun getImageUrl(imagePath: String): String {
        val baseUrlSize = baseUrl + closestSize
        return baseUrlSize + imagePath
    }

    private val closestSize by lazy {
        var min = Integer.MAX_VALUE
        var closest = ""

        for (v in sizes) {
            if (v.equals(ORIGINAL)) break
            val intValue = v.replaceFirst(W_PREFIX, "", true).toInt()
            val diff = Math.abs(intValue - (getImageViewWidth()))

            if (diff < min) {
                min = diff
                closest = v
            }
        }

        closest
    }
}