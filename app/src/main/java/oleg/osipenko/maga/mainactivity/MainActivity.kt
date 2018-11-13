package oleg.osipenko.maga.mainactivity

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.view.GravityCompat
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.support.v7.util.DiffUtil
import android.support.v7.widget.LinearLayoutManager
import android.text.Html
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import kotlinx.android.synthetic.main.activity_main.*
import oleg.osipenko.domain.entities.Movie
import oleg.osipenko.domain.repository.MoviesRepository
import oleg.osipenko.maga.R
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.android.viewmodel.ext.koin.viewModel
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module.module

/**
 * Main activity with movies
 */
class MainActivity : AppCompatActivity() {

  companion object {
    val activityModel = module {
      viewModel { MainActivityViewModel(get() as MoviesRepository) }

      single { Glide.with(androidContext()) }

      single<DiffUtil.ItemCallback<Movie>> { ComingSoonAdapter.MovieDiffCallback() }

      factory { ComingSoonAdapter(get() as RequestManager, get() as DiffUtil.ItemCallback<Movie>) }

      factory { (activity: AppCompatActivity) -> NowPlayingAdapter(activity) }
    }
  }

  private val comingSoonAdapter: ComingSoonAdapter by inject()
  private val nowPlayingAdapter: NowPlayingAdapter by inject { parametersOf(this@MainActivity) }
  private val activityViewModel: MainActivityViewModel by viewModel()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)


    initViews()
    loadConfig()
  }

  private fun initViews() {
    window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
    val comingSoonLm = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
    list_coming_soon.layoutManager = comingSoonLm
    list_coming_soon.adapter = comingSoonAdapter
    list_coming_soon.addItemDecoration(ComingSoonMarginItemDecoration(this, R.dimen.margin_material))

    pager_now_playing.adapter = nowPlayingAdapter
    pager_now_playing.clipToPadding = false
    (toolbar?.layoutParams as ViewGroup.MarginLayoutParams).topMargin = getStatusBarHeight()
    (shader?.layoutParams as ViewGroup.MarginLayoutParams).topMargin = getStatusBarHeight()
    setSupportActionBar(toolbar)

    supportActionBar?.apply {
      setDisplayHomeAsUpEnabled(true)
      setHomeAsUpIndicator(R.drawable.ic_kebab)
      setTitle(R.string.title_now_playing)
    }
  }

  private fun getStatusBarHeight(): Int {
    val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")

    return if (resourceId > 0) {
      resources.getDimensionPixelSize(resourceId)
    } else {
      resources.getDimensionPixelSize(R.dimen.height_status_bar)
    }
  }

  private fun loadConfig() {
    activityViewModel.configObservable.observe(this, Observer { config ->
      comingSoonAdapter.setConfiguration(config?.baseUrl, config?.posterSizes)
    })
    activityViewModel.refreshNowPlaying()
    activityViewModel.refreshUpcoming()
  }

  override fun onStart() {
    super.onStart()
    startObservingMovies()
  }

  private fun startObservingMovies() {
    observeNowPlaying()
    observeComingSoon()
    observeNowPlayingProgress()
  }

  private fun observeNowPlaying() {
    activityViewModel.nowPlayingMovies.observe(this, Observer {
      nowPlayingAdapter.setMovies(it)
      if (it?.isNotEmpty() == true) {
        val startIndex = it.size * 10
        pager_now_playing.setPageTransformer(false, null)
        pager_now_playing.setCurrentItem(startIndex, false)
        pager_now_playing.setPageTransformer(false, object : ViewPager.PageTransformer {
          override fun transformPage(page: View, position: Float) {
            val shadow = page.findViewById<View>(R.id.shadow)
            val title = page.findViewById<View>(R.id.movie_title)
            if (position < -0.3 || position > 0.3) {
              shadow.visibility = View.VISIBLE
              title.visibility = View.INVISIBLE
            } else {
              shadow.visibility = View.INVISIBLE
              title.visibility = View.VISIBLE
            }
          }
        })
      }
    })
    activityViewModel.error.observe(this, errorObserver)
  }

  private fun observeComingSoon() {
    activityViewModel.comingSoonMovies.observe(this, Observer { comingSoonAdapter.submitList(it) })
    activityViewModel.error.observe(this, errorObserver)
  }

  private val errorObserver = Observer<String?> {
    it?.let {
      Snackbar.make(
          list_coming_soon, Html.fromHtml(it), Snackbar.LENGTH_LONG
      ).show()
    }
  }

  private fun observeNowPlayingProgress() {
    activityViewModel.nowPlayingShowProgressBar.observe(this, Observer {
      progressbar.visibility = if (it == true) View.VISIBLE else View.GONE
    })
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    return when (item.itemId) {
      android.R.id.home -> {
        drawer.openDrawer(GravityCompat.START)
        true
      }
      else              -> super.onOptionsItemSelected(item)
    }
  }
}