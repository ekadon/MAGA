package oleg.osipenko.maga.common

import android.support.annotation.DrawableRes
import android.view.View
import android.widget.ImageView
import com.squareup.picasso.Picasso
import oleg.osipenko.maga.R

/**
 * Abstract wrapper over image loading library.
 */
interface ImageLoader {
  companion object {
    /**
     * Factory generational method.
     */
    fun get(): ImageLoader = PicassoImageLoader
  }

  /**
   * Loads image using provided [url] into [view], uses [placeholder] resource
   * for placeholder image and [errorPlaceholder] resource for error
   * placeholder.
   *
   * @param[view] image view to load image into.
   * @param[url] Image URL string.
   * @param[placeholder] resource id of placeholder image. R.drawable
   * .placeholder by default.
   * @param[errorPlaceholder] resource id of error placeholder. R.drawable
   * .placeholder by default.
   */
  fun loadImage(
    view: View,
    url: String,
    @DrawableRes placeholder: Int = R.drawable.placeholder,
    @DrawableRes errorPlaceholder: Int = R.drawable.placeholder
  )
}

/**
 * Implementation of [ImageLoader] using Picasso.
 */
object PicassoImageLoader : ImageLoader {
  override fun loadImage(
    view: View,
    url: String,
    placeholder: Int,
    errorPlaceholder: Int) {

    @Suppress("UnsafeCast")
    Picasso.get()
      .load(url)
      .placeholder(placeholder)
      .error(errorPlaceholder)
      .into(view as ImageView)
  }
}
