package oleg.osipenko.maga.mainactivity

import android.graphics.drawable.Drawable
import android.support.v7.recyclerview.extensions.ListAdapter
import android.support.v7.util.DiffUtil
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import kotlinx.android.synthetic.main.item_coming_soon.view.*
import oleg.osipenko.domain.entities.Movie
import oleg.osipenko.maga.R

/**
 * Adapter for displaying Coming Soon feed.
 */
class ComingSoonAdapter(
  private val glide: RequestManager, diffCallback: DiffUtil.ItemCallback<Movie>
) : ListAdapter<Movie, ComingSoonAdapter.ComingSoonVH>(diffCallback) {

  private lateinit var baseUrl: String
  private lateinit var posterSizes: List<String>

  /**
   * Sets the configuration required for displaying images.
   */
  fun setConfiguration(baseUrl: String?, posterSizes: List<String>?) {
    this.baseUrl = baseUrl ?: ""
    this.posterSizes = posterSizes ?: emptyList()
  }

  override fun onCreateViewHolder(
    parent: ViewGroup, viewType: Int
  ): ComingSoonVH {
    val itemView = LayoutInflater.from(parent.context)
      .inflate(R.layout.item_coming_soon, parent, false)
    return ComingSoonVH(itemView, baseUrl, posterSizes)
  }

  override fun onBindViewHolder(holder: ComingSoonVH, position: Int) {
    holder.bind(glide, getItem(position).posterPath)
  }

  /**
   * View holder for [ComingSoonAdapter].
   */
  class ComingSoonVH(itemView: View, baseUrl: String, sizes: List<String>) :
    MovieBaseHolder(itemView, baseUrl, sizes) {

    override fun getImageViewWidth(): Int {
      return itemView.resources.getDimension(R.dimen.width_coming_soon).toInt()
    }

    /**
     * Sets the movie data to the view.
     */
    fun bind(glide: RequestManager, url: String) {
      if (!TextUtils.isEmpty(baseUrl) && sizes.isNotEmpty()) {
        glide.load(getImageUrl(url)).thumbnail(0.2f)
          .listener(object : RequestListener<Drawable> {
            override fun onLoadFailed(
              e: GlideException?, model: Any?, target: Target<Drawable>?,
              isFirstResource: Boolean
            ): Boolean {
              itemView.poster.setImageResource(R.drawable.placeholder)
              return true
            }

            override fun onResourceReady(
              resource: Drawable?, model: Any?, target: Target<Drawable>?,
              dataSource: DataSource?, isFirstResource: Boolean
            ): Boolean {
              return false
            }
          }).into(itemView.poster)
      }
    }
  }

  /**
   * Diff Callback for [ComingSoonAdapter].
   */
  class MovieDiffCallback : DiffUtil.ItemCallback<Movie>() {
    override fun areItemsTheSame(oldItem: Movie, newItem: Movie): Boolean {
      return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Movie, newItem: Movie): Boolean {
      return oldItem == newItem
    }
  }
}