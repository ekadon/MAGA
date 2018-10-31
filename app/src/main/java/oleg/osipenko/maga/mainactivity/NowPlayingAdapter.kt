package oleg.osipenko.maga.mainactivity

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v7.app.AppCompatActivity
import oleg.osipenko.domain.entities.Movie

class NowPlayingAdapter(activity: AppCompatActivity) : FragmentStatePagerAdapter(activity.supportFragmentManager) {
  private val movies: MutableList<Movie> = ArrayList()

  fun setMovies(nowPlaying: List<Movie>?) {
    nowPlaying?.let {
      movies.clear()
      movies.addAll(nowPlaying)
      notifyDataSetChanged()
    }
  }

  override fun getCount() = if (movies.isEmpty()) 0 else Int.MAX_VALUE

  override fun getItem(position: Int): Fragment {
    val movie = movies[getMoviePosition(position)]
    return NowPlayingFragment.newInstance(movie.posterPath, movie.title)
  }

  private fun getMoviePosition(pagerPosition: Int): Int {
    return pagerPosition % movies.size
  }
}