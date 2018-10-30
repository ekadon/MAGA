package oleg.osipenko.maga.mainactivity

import android.support.v7.recyclerview.extensions.ListAdapter
import android.support.v7.util.DiffUtil
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.RequestManager
import kotlinx.android.synthetic.main.item_coming_soon.view.*
import oleg.osipenko.domain.entities.Movie
import oleg.osipenko.maga.R

class ComingSoonAdapter(
        private val glide: RequestManager, diffCallback: DiffUtil.ItemCallback<Movie>
) : ListAdapter<Movie, ComingSoonAdapter.ComingSoonVH>(diffCallback) {

    private lateinit var baseUrl: String
    private lateinit var posterSizes: List<String>

    fun setConfiguration(baseUrl: String?, posterSizes: List<String>?) {
        this.baseUrl = baseUrl ?: ""
        this.posterSizes = posterSizes ?: emptyList()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ComingSoonVH {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_coming_soon, parent, false)
        return ComingSoonVH(itemView, baseUrl, posterSizes)
    }

    override fun onBindViewHolder(holder: ComingSoonVH, position: Int) {
        holder.bind(glide, getItem(position).posterPath)
    }

    class ComingSoonVH(itemView: View?, baseUrl: String, sizes: List<String>) : MovieBaseHolder(itemView, baseUrl, sizes) {

        override fun getImageViewWidth(): Int {
            return itemView.resources.getDimension(R.dimen.width_coming_soon).toInt()
        }

        fun bind(glide: RequestManager, url: String) {
            if (!TextUtils.isEmpty(baseUrl) && sizes.isNotEmpty()) {
                glide.load(getImageUrl(url)).thumbnail(0.2f).into(itemView.poster)
            }
        }
    }

    class MovieDiffCallback: DiffUtil.ItemCallback<Movie>() {
        override fun areItemsTheSame(oldItem: Movie?, newItem: Movie?): Boolean {
            return oldItem?.id == newItem?.id
        }

        override fun areContentsTheSame(oldItem: Movie?, newItem: Movie?): Boolean {
            return oldItem == newItem
        }

    }
}