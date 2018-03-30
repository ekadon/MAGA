package oleg.osipenko.maga

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import oleg.osipenko.maga.data.db.MoviesDb
import oleg.osipenko.maga.data.network.TMDBApi
import oleg.osipenko.maga.data.repository.MoviesDataRepository
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val viewModel = ViewModelProviders.of(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                val db = MoviesDb.create(this@MainActivity)
                val api = TMDBApi.create()
                val repo = MoviesDataRepository(db, api, Executors.newFixedThreadPool(5))
                @Suppress("UNCHECKED_CAST")
                return MainActivityViewModel(repo) as T
            }
        })[MainActivityViewModel::class.java]
        viewModel.nowPlayingMovies.observe(this, Observer { now.text = it?.toString() })
        viewModel.nowPlayingNetworkState.observe(this, Observer { Toast.makeText(this, it?.throwableMessage, Toast.LENGTH_LONG).show() })
        viewModel.comingSoonMovies.observe(this, Observer { soon.text = it?.toString() })
        viewModel.comingSoonNetworkState.observe(this, Observer { Toast.makeText(this, it?.throwableMessage, Toast.LENGTH_LONG).show() })
    }
}
