package oleg.osipenko.maga.mainactivity

import android.support.v7.recyclerview.extensions.ListAdapter
import android.support.v7.util.DiffUtil
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_coming_soon.view.*
import oleg.osipenko.domain.entities.Movie
import oleg.osipenko.maga.R

/**
 * Adapter for displaying Coming Soon feed.
 */
class ComingSoonAdapter(
  diffCallback: DiffUtil.ItemCallback<Movie>
) : ListAdapter<Movie, ComingSoonAdapter.ComingSoonVH>(diffCallback) {

  @Suppress("LateinitUsage")
  private lateinit var baseUrl: String
  @Suppress("LateinitUsage")
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
    holder.bind(getItem(position).posterPath)
  }

  /**
   * View holder for [ComingSoonAdapter].
   */
  class ComingSoonVH(itemView: View, baseUrl: String, sizes: List<String>) :
    MovieBaseHolder(itemView, baseUrl, sizes) {

    override fun getImageViewWidth(): Int =
      itemView.resources.getDimension(R.dimen.width_coming_soon).toInt()

    /**
     * Sets the movie data to the view.
     */
    fun bind(url: String) {
      if (!TextUtils.isEmpty(baseUrl) && sizes.isNotEmpty()) {
        Picasso.get()
          .load(getImageUrl(url))
          .placeholder(android.R.color.darker_gray)
          .error(R.drawable.placeholder)
          .into(itemView.poster)
      }
    }
  }

  /**
   * Diff Callback for [ComingSoonAdapter].
   */
  class MovieDiffCallback : DiffUtil.ItemCallback<Movie>() {
    override fun areItemsTheSame(oldItem: Movie, newItem: Movie): Boolean =
      oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: Movie, newItem: Movie): Boolean =
      oldItem == newItem
  }
}
