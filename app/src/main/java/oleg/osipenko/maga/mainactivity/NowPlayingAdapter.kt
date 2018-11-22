package oleg.osipenko.maga.mainactivity

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v7.app.AppCompatActivity
import oleg.osipenko.domain.entities.Movie

/**
 * Adapter for displaying Now Playing feed.
 */
class NowPlayingAdapter(activity: AppCompatActivity) : FragmentStatePagerAdapter(activity.supportFragmentManager) {
  private val movies: MutableList<Movie> = ArrayList()

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

  override fun getItem(position: Int): Fragment {
    return movies[getMoviePosition(position)].let { movie ->
      NowPlayingFragment.newInstance(movie.posterPath, movie.title)
    }
  }

  private fun getMoviePosition(pagerPosition: Int): Int {
    return pagerPosition % movies.size
  }
}