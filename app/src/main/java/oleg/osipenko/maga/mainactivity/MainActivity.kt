package oleg.osipenko.maga.mainactivity

import android.os.Bundle
import android.support.v4.view.GravityCompat
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.view.WindowManager
import kotlinx.android.synthetic.main.activity_main.*
import oleg.osipenko.maga.R

/**
 * Main activity with movies.
 */
class MainActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)

    if (supportFragmentManager.findFragmentById(R.id.container_main) == null) {
      supportFragmentManager.beginTransaction()
        .add(R.id.container_main, MainFragment())
        .commit()
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
