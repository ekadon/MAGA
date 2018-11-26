package oleg.osipenko.maga.mainactivity

import android.arch.lifecycle.Observer
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
import org.koin.android.viewmodel.ext.android.viewModel

/**
 * Fragment for displaying movie in the Now Playing feed.
 */
class NowPlayingFragment : Fragment() {

  companion object {
    private const val IMAGE_URL = "url.image"
    private const val TITLE = "title.movie"
    const val THUMBNAIL = 0.2f

    /**
     * Static factory method.
     */
    fun newInstance(url: String, title: String): NowPlayingFragment {
      val fragment = NowPlayingFragment()
      val args = Bundle()
      args.putString(IMAGE_URL, url)
      args.putString(TITLE, title)
      fragment.arguments = args
      return fragment
    }
  }

  private val fragmentViewModel: MainActivityViewModel by viewModel()

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
  ) = inflater.inflate(R.layout.item_now_playing, container, false)

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    movie_title.text = arguments?.getString(TITLE)

    fragmentViewModel.configObservable.observe(this, Observer {
      val url = arguments?.getString(IMAGE_URL) ?: ""
      val baseUrl = it?.baseUrl ?: ""
      val sizes = it?.posterSizes ?: emptyList()
      if (!TextUtils.isEmpty(baseUrl) && sizes.isNotEmpty()) {
        Glide.with(this).load(getImageUrl(baseUrl, url, sizes))
          .thumbnail(THUMBNAIL).listener(object : RequestListener<Drawable> {
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
    val screenWidth = resources.displayMetrics.widthPixels
    val margins = resources.getDimension(R.dimen.margin_material) * 2
    return (screenWidth - margins).toInt()
  }
}
