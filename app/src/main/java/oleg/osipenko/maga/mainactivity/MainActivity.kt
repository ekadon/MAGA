package oleg.osipenko.maga.mainactivity

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.text.Html
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_main.*
import oleg.osipenko.domain.repository.MoviesRepository
import oleg.osipenko.maga.R
import oleg.osipenko.maga.data.db.MoviesDb
import oleg.osipenko.maga.data.network.TMDBApi
import oleg.osipenko.maga.data.repository.MoviesDataRepository
import org.koin.android.ext.android.startKoin
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.android.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module
import java.util.concurrent.Executors

/**
 * Main activity with movies
 */
class MainActivity : AppCompatActivity() {

    private val comingSoonAdapter by lazy { ComingSoonAdapter(Glide.with(this), ComingSoonAdapter.MovieDiffCallback()) }
    private val nowPlayingAdapter by lazy { NowPlayingAdapter(supportFragmentManager) }
    private val db by lazy { MoviesDb.create(this) }
    private val api by lazy { TMDBApi.create(this) }
    private val activityViewModel: MainActivityViewModel by viewModel()

    private val activityModule = module {
        single<MoviesRepository>(override = true) {
            MoviesDataRepository(db, api, Executors.newFixedThreadPool(5))
        }

        viewModel(override = true) {
            MainActivityViewModel(get() as MoviesRepository)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startKoin(applicationContext, listOf(activityModule))
        setContentView(R.layout.activity_main)

        initViews()
//        initViewModel()
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
    }

    private fun getStatusBarHeight(): Int {
        val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")

        return if (resourceId > 0) {
            resources.getDimensionPixelSize(resourceId)
        } else {
            resources.getDimensionPixelSize(R.dimen.height_status_bar)
        }
    }

//    private fun initViewModel() {
//        activityViewModel = ViewModelProviders.of(this, object : ViewModelProvider.Factory {
//            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
//                val db = MoviesDb.create(this@MainActivity)
//                val api = TMDBApi.create(this@MainActivity)
//                val repo = MoviesDataRepository(db, api, Executors.newFixedThreadPool(5))
//                @Suppress("UNCHECKED_CAST")
//                return MainActivityViewModel(repo) as T
//            }
//        })[MainActivityViewModel::class.java]
//    }

    private fun loadConfig() {
        activityViewModel.configObservable.observe(this, Observer { config ->
            comingSoonAdapter.setConfiguration(config?.baseUrl, config?.posterSizes)
            startObservingMovies(activityViewModel)
        })
    }

    private fun startObservingMovies(viewModel: MainActivityViewModel) {
        observeNowPlaying(viewModel)
        observeComingSoon(viewModel)
        observeNowPlayingProgress(viewModel)
    }

    private fun observeNowPlaying(viewModel: MainActivityViewModel) {
        viewModel.nowPlayingMovies.observe(this, Observer {
            nowPlayingAdapter.setMovies(it)
            if (it?.isNotEmpty() == true) {
                val startIndex = it.size * 10
                pager_now_playing.setPageTransformer(false, null)
                pager_now_playing.setCurrentItem(startIndex, false)
                pager_now_playing.setPageTransformer(false, object : ViewPager.PageTransformer {
                    override fun transformPage(page: View, position: Float) {
                        val shadow = page.findViewById<View>(R.id.shadow)
                        if (position < -0.3 || position > 0.3) {
                            shadow.visibility = View.VISIBLE
                        } else {
                            shadow.visibility = View.INVISIBLE
                        }
                    }
                })
            }
        })
        viewModel.nowPlayingErrorMessage.observe(this, errorObserver)
    }

    private fun observeComingSoon(viewModel: MainActivityViewModel) {
        viewModel.comingSoonMovies.observe(this, Observer { comingSoonAdapter.submitList(it) })
        viewModel.comingSoonErrorMessage.observe(this, errorObserver)
    }

    private val errorObserver = Observer<String?> {
        it?.let {
            Snackbar.make(list_coming_soon,
                    Html.fromHtml(it),
                    Snackbar.LENGTH_LONG).show()
        }
    }

    private fun observeNowPlayingProgress(viewModel: MainActivityViewModel) {
        viewModel.nowPlayingShowProgressBar.observe(this, Observer {
            progressbar.visibility = if (it == true) View.VISIBLE else View.GONE
        })
    }
}
