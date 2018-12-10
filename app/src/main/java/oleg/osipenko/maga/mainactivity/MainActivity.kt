package oleg.osipenko.maga.mainactivity

import android.os.Bundle
import android.support.v4.view.GravityCompat
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.view.ViewGroup
import android.view.WindowManager
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import kotlinx.android.synthetic.main.activity_main.*
import oleg.osipenko.maga.R

/**
 * Main activity with movies.
 */
class MainActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    initViews()
  }

  private fun initViews() {
    window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)

    @Suppress("UnsafeCast")
    (toolbar?.layoutParams as ViewGroup.MarginLayoutParams).topMargin =
      getStatusBarHeight()

    setSupportActionBar(toolbar)
    val navController = findNavController(R.id.fragment_nav_host)
    val appBarConfig = AppBarConfiguration(navController.graph)
    setupActionBarWithNavController(navController, appBarConfig)

    supportActionBar?.apply {
      setDisplayHomeAsUpEnabled(true)
      setHomeAsUpIndicator(R.drawable.ic_kebab)
    }
  }

  private fun getStatusBarHeight(): Int {
    val resourceId =
      resources.getIdentifier("status_bar_height", "dimen", "androsid")

    return if (resourceId > 0) {
      resources.getDimensionPixelSize(resourceId)
    } else {
      resources.getDimensionPixelSize(R.dimen.height_status_bar)
    }
  }

  override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
    android.R.id.home -> openDrawer()
    else              -> super.onOptionsItemSelected(item)
  }

  private fun openDrawer(): Boolean {
    drawer.openDrawer(GravityCompat.START)
    return true
  }
}
