package oleg.osipenko.maga.mainactivity

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import kotlinx.android.synthetic.main.item_now_playing.*
import oleg.osipenko.maga.R
import oleg.osipenko.maga.data.db.MoviesDb
import oleg.osipenko.maga.data.network.TMDBApi
import oleg.osipenko.maga.data.repository.ConfigDataRepository
import oleg.osipenko.maga.data.repository.MoviesDataRepository

class NowPlayingFragment : Fragment() {

  companion object {
    private const val IMAGE_URL = "url.image"
    private const val TITLE = "title.movie"

    fun newInstance(url: String, title: String): NowPlayingFragment {
      val fragment = NowPlayingFragment()
      val args = Bundle()
      args.putString(IMAGE_URL, url)
      args.putString(TITLE, title)
      fragment.arguments = args
      return fragment
    }
  }

  override fun onCreateView(
      inflater: LayoutInflater, container: ViewGroup?,
      savedInstanceState: Bundle?
  ): View? {
    return inflater.inflate(R.layout.item_now_playing, container, false)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    val viewModel =
        ViewModelProviders.of(activity!!, object : ViewModelProvider.Factory {
          override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            val db = MoviesDb.create(context!!)
            val api = TMDBApi.create(context!!)
            val movieRepo = MoviesDataRepository(db, api)
            val configRepo = ConfigDataRepository(db, api)
            @Suppress("UNCHECKED_CAST") return MainActivityViewModel(
              movieRepo, configRepo
            ) as T
          }
        })[MainActivityViewModel::class.java]

    movie_title.text = arguments?.getString(TITLE)

    viewModel.configObservable.observe(this, Observer {
      val url = arguments?.getString(IMAGE_URL) ?: ""
      val baseUrl = it?.baseUrl ?: ""
      val sizes = it?.posterSizes ?: emptyList()
      if (!TextUtils.isEmpty(baseUrl) && sizes.isNotEmpty()) {
        Glide.with(this).load(getImageUrl(baseUrl, url, sizes)).thumbnail(0.2f)
          .listener(object : RequestListener<Drawable> {
            override fun onLoadFailed(
                e: GlideException?, model: Any?, target: Target<Drawable>?,
                isFirstResource: Boolean
            ): Boolean {
              movie_poster.setImageResource(R.drawable.placeholder)
              return true
            }

            override fun onResourceReady(
                resource: Drawable?, model: Any?, target: Target<Drawable>?,
                dataSource: DataSource?, isFirstResource: Boolean
            ): Boolean {
              return false
            }
          }).into(movie_poster)
      }
    })
  }

  private fun getImageUrl(
      baseUrl: String, imagePath: String, sizes: List<String>
  ): String {
    val baseUrlSize = baseUrl + closestSize(sizes)
    return baseUrlSize + imagePath
  }

  private fun closestSize(sizes: List<String>): String {
    var min = Integer.MAX_VALUE
    var closest = ""

    for (v in sizes) {
      if (v == MovieBaseHolder.ORIGINAL) break
      val intValue = v.replaceFirst(MovieBaseHolder.W_PREFIX, "", true).toInt()
      val diff = Math.abs(intValue - getImageWidth())

      if (diff < min) {
        min = diff
        closest = v
      }
    }

    return closest
  }

  private fun getImageWidth(): Int {
    return (resources.displayMetrics.widthPixels - resources.getDimension(
      R.dimen.margin_material
    ) * 2).toInt()
  }
}