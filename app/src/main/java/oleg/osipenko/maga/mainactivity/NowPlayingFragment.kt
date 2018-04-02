package oleg.osipenko.maga.mainactivity

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.item_now_playing.*
import oleg.osipenko.maga.R
import oleg.osipenko.maga.data.db.MoviesDb
import oleg.osipenko.maga.data.network.TMDBApi
import oleg.osipenko.maga.data.repository.MoviesDataRepository
import java.util.concurrent.Executors

class NowPlayingFragment : Fragment() {

    companion object {
        private const val IMAGE_URL = "url.image"

        fun newInstance(url: String): NowPlayingFragment {
            val fragment = NowPlayingFragment()
            val args = Bundle()
            args.putString(IMAGE_URL, url)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.item_now_playing, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val viewModel = ViewModelProviders.of(activity!!, object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                val db = MoviesDb.create(context!!)
                val api = TMDBApi.create(context!!)
                val repo = MoviesDataRepository(db, api, Executors.newFixedThreadPool(5))
                @Suppress("UNCHECKED_CAST")
                return MainActivityViewModel(repo) as T
            }
        })[MainActivityViewModel::class.java]

        viewModel.configObservable.observe(this, Observer {
            val url = arguments?.getString(IMAGE_URL) ?: ""
            val baseUrl = it?.baseUrl ?: ""
            val sizes = it?.posterSizes ?: emptyList()
            if (!TextUtils.isEmpty(baseUrl) && sizes.isNotEmpty()) {
                Glide.with(this).load(getImageUrl(baseUrl, url, sizes)).thumbnail(0.2f).into(poster)
            }
        })
    }

    private fun getImageUrl(baseUrl: String, imagePath: String, sizes: List<String>): String {
        val baseUrlSize = baseUrl + closestSize(sizes)
        return baseUrlSize + imagePath
    }

    private fun closestSize(sizes: List<String>): String {
        var min = Integer.MAX_VALUE
        var closest = ""

        for (v in sizes) {
            if (v.equals(MovieBaseHolder.ORIGINAL)) break
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
        return (resources.displayMetrics.widthPixels - resources.getDimension(R.dimen.margin_material) * 2).toInt()
    }
}