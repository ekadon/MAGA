package oleg.osipenko.maga.mainactivity

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.view.ViewPager
import android.support.v7.util.DiffUtil
import android.support.v7.widget.LinearLayoutManager
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_main.*
import oleg.osipenko.domain.entities.Movie
import oleg.osipenko.maga.R
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.android.viewmodel.ext.koin.viewModel
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module.module

class MainFragment: Fragment() {

  companion object {
    private const val INFINITE_SIZE_MULTIPLIER = 10
    private const val THRESHOLD = 0.3

    val mainFragmentModule = module {
      viewModel { MainFragmentViewModel(get(), get()) }

      single<DiffUtil.ItemCallback<Movie>> {
        ComingSoonAdapter.MovieDiffCallback()
      }

      factory {
        @Suppress("UnsafeCast") ComingSoonAdapter(get())
      }

      factory { (fm: FragmentManager) -> NowPlayingAdapter(fm) }
    }
  }

  private val comingSoonAdapter: ComingSoonAdapter by inject()
  private val nowPlayingAdapter: NowPlayingAdapter by inject {
    parametersOf(this@MainFragment.childFragmentManager)
  }
  private val fragmentViewModel: MainFragmentViewModel by viewModel()

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
  ): View? =
    inflater.inflate(R.layout.fragment_main, container, false)

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    initViews()
    loadConfig()
  }

  private fun initViews() {
    initComingSoon()
    initNowPlaying()


    @Suppress("UnsafeCast")
    (shader?.layoutParams as ViewGroup.MarginLayoutParams).topMargin =
      getStatusBarHeight()
  }

  private fun getStatusBarHeight(): Int {
    val resourceId =
      resources.getIdentifier("status_bar_height", "dimen", "android")

    return if (resourceId > 0) {
      resources.getDimensionPixelSize(resourceId)
    } else {
      resources.getDimensionPixelSize(R.dimen.height_status_bar)
    }
  }

  private fun initComingSoon() {
    val comingSoonLm =
      LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
    with (list_coming_soon) {
      layoutManager = comingSoonLm
      adapter = comingSoonAdapter
      addItemDecoration(
        ComingSoonMarginItemDecoration(context, R.dimen.margin_material)
      )
    }
  }

  private fun initNowPlaying() {
    pager_now_playing.adapter = nowPlayingAdapter
    pager_now_playing.clipToPadding = false
  }

  private fun loadConfig() {
    fragmentViewModel.configObservable.observe(this, Observer { config ->
      comingSoonAdapter.setConfiguration(config?.baseUrl, config?.posterSizes)
      nowPlayingAdapter.setConfiguration(config?.baseUrl, config?.posterSizes)
      startObservingMovies()
    })
  }

  private fun startObservingMovies() {
    observeNowPlaying()
    observeComingSoon()
    observeNowPlayingProgress()
  }

  private fun observeNowPlaying() {
    fragmentViewModel.nowPlayingMovies.observe(this, Observer {
      nowPlayingAdapter.setMovies(it)
      if (it?.isNotEmpty() == true) {
        val startIndex = it.size * INFINITE_SIZE_MULTIPLIER
        movie_title.text = nowPlayingAdapter.getPageTitle(0)
        with(pager_now_playing) {
          setCurrentItem(startIndex, false)
          setPageTransformer(false) { page, position ->
            val shadow = page.findViewById<View>(R.id.shadow)

            fun isEnoughForTransformation(position: Float): Boolean {
              return position < -THRESHOLD || position > THRESHOLD
            }

            if (isEnoughForTransformation(position)) {
              shadow.visibility = View.VISIBLE
            } else {
              shadow.visibility = View.INVISIBLE
            }
          }
          addOnPageChangeListener(
            object: ViewPager.SimpleOnPageChangeListener() {
            override fun onPageScrolled(
              position: Int, positionOffset: Float, positionOffsetPixels: Int
            ) {
              movie_title.text = nowPlayingAdapter.getPageTitle(position)
            }
          })
        }
      }
    })
    fragmentViewModel.nowPlayingErrorMessage.observe(this, errorObserver)
  }

  private fun observeComingSoon() {
    fragmentViewModel.comingSoonMovies.observe(this, Observer {
      comingSoonAdapter.submitList(it)
    })
    fragmentViewModel.comingSoonErrorMessage.observe(this, errorObserver)
  }

  private val errorObserver = Observer<String?> {
    it?.let {
      Snackbar.make(list_coming_soon, Html.fromHtml(it), Snackbar.LENGTH_LONG)
        .show()
    }
  }

  private fun observeNowPlayingProgress() {
    fragmentViewModel.nowPlayingShowProgressBar.observe(this, Observer {
      progressbar.visibility = if (it == true) View.VISIBLE else View.GONE
    })
  }
}
