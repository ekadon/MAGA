package oleg.osipenko.maga.mainactivity

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import oleg.osipenko.domain.entities.Movie

class NowPlayingAdapter(fm: FragmentManager?) : FragmentStatePagerAdapter(fm) {
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
        return NowPlayingFragment.newInstance(movies[getMoviePosition(position)].posterPath)
    }

    private fun getMoviePosition(pagerPosition: Int): Int {
        return pagerPosition % movies.size
    }
}