package oleg.osipenko.maga.mainactivity

import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import oleg.osipenko.domain.entities.Movie

/**
 * Adapter for displaying Now Playing feed.
 */
class NowPlayingAdapter(fm: FragmentManager) :
  FragmentStatePagerAdapter(fm) {

  private val movies: MutableList<Movie> = ArrayList()
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

  /**
   * Sets the content to display using this adapter.
   */
  fun setMovies(nowPlaying: List<Movie>?) {
    nowPlaying?.let {
      movies.clear()
      movies.addAll(nowPlaying)
      notifyDataSetChanged()
    }
  }

  override fun getCount() = if (movies.isEmpty()) 0 else Int.MAX_VALUE

  override fun getItem(position: Int) =
    movies[getMoviePosition(position)].let { movie ->
      NowPlayingFragment.newInstance(movie.posterPath, baseUrl, posterSizes)
    }

  override fun getPageTitle(position: Int): CharSequence? =
    movies[getMoviePosition(position)].title

  private fun getMoviePosition(pagerPosition: Int) = pagerPosition % movies.size

}
