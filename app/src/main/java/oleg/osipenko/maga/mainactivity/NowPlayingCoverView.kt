package oleg.osipenko.maga.mainactivity

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.LinearGradient
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Shader
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import android.util.AttributeSet
import android.view.View
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * Custom view for displaying movie cover with partially blurred area.
 */
class NowPlayingCoverView(context: Context?, attrs: AttributeSet?) :
  View(context, attrs) {

  companion object {
    const val GRADIENT_END = 1f
    const val BLUR_RADIUS = 25f
    const val X = 0.5f
  }

  private val gradientPaint = Paint(Paint.ANTI_ALIAS_FLAG)
  private val oppositePaint = Paint(Paint.ANTI_ALIAS_FLAG)
  private val rect = Rect()
  private val rectF = RectF(rect)
  private val regularBitmapPaint = Paint(Paint.ANTI_ALIAS_FLAG)
  private val blurredBitmapPaint = Paint(Paint.ANTI_ALIAS_FLAG)
  var imageUrl: String = ""
    set(value) {
      field = value
      requestLayout()
    }
  @Suppress("LateinitUsage")
  private lateinit var cover: Bitmap
  @Suppress("LateinitUsage")
  private lateinit var blurredCover: Bitmap

  private val gradientShader: Shader
    get() {
      val gradient = LinearGradient(
        X, 0f, X, GRADIENT_END, -0x1000000, 0x00000000, Shader.TileMode
          .CLAMP
      )

      val matrix = Matrix()
      matrix.postTranslate(rect.centerX().toFloat(), rect.centerY().toFloat())
      matrix.postScale(
        rect.width().toFloat(), rect.height().toFloat(),
        rect.centerX().toFloat(), rect.centerY().toFloat()
      )

      gradient.setLocalMatrix(matrix)
      return gradient
    }

  private val oppositeShader: Shader
    get() {
      val gradient = LinearGradient(
        X, 0f, X, GRADIENT_END, 0x00000000, -0x1000000, Shader.TileMode
          .CLAMP
      )

      val matrix = Matrix()
      matrix.postTranslate(rect.centerX().toFloat(), rect.centerY().toFloat())
      matrix.postScale(
        rect.width().toFloat(), rect.height().toFloat(),
        rect.centerX().toFloat(), rect.centerY().toFloat()
      )

      gradient.setLocalMatrix(matrix)
      return gradient
    }

  override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
    val width = View.MeasureSpec.getSize(widthMeasureSpec)
    val height = View.MeasureSpec.getSize(heightMeasureSpec)
    rect.set(0, 0, width, height)
    rectF.set(rect)
    init()

    setMeasuredDimension(width, height)
  }

  private fun init() {
    setLayerType(View.LAYER_TYPE_SOFTWARE, null)
    gradientPaint.isDither = true
    gradientPaint.shader = gradientShader
    gradientPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_OUT)
    oppositePaint.isDither = true
    oppositePaint.shader = oppositeShader
    oppositePaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_OUT)

    if (imageUrl.isNotEmpty()) {

      GlobalScope.launch {
        cover = Picasso.get().load(imageUrl).get()

        launch(Dispatchers.Main) {
          val desiredWidth: Int
          val desiredHeight: Int

          fun getHeightRatio(): Float = rect.height() / cover.height.toFloat()

          fun getWidthRatio(): Float = rect.width() / cover.width.toFloat()

          val ratio = Math.max(getWidthRatio(), getHeightRatio())

          if (cover.width > cover.height) {
            desiredHeight = rect.height()
            desiredWidth = (cover.width * ratio).toInt()
          } else {
            desiredWidth = rect.width()
            desiredHeight = (cover.height * ratio).toInt()
          }

          val x = (desiredWidth - rect.width()) / 2
          val y = (desiredHeight - rect.height()) / 2

          cover =
            Bitmap.createScaledBitmap(cover, desiredWidth, desiredHeight, true)
          cover = Bitmap.createBitmap(cover, x, y, rect.width(), rect.height())

          blurredCover = Bitmap.createBitmap(cover)

          val rs = RenderScript.create(context)
          val blurScript = ScriptIntrinsicBlur.create(rs, Element.A_8(rs))

          val allocIn = Allocation.createFromBitmap(rs, cover)
          val allocOut = Allocation.createFromBitmap(rs, blurredCover)

          blurScript.setRadius(BLUR_RADIUS)
          blurScript.setInput(allocIn)

          blurScript.forEach(allocOut)

          allocOut.copyTo(blurredCover)
        }
      }
    }
  }

  override fun onDraw(canvas: Canvas) {
    if (::cover.isInitialized && ::blurredCover.isInitialized) {
      canvas.drawBitmap(cover, 0f, 0f, regularBitmapPaint)
      canvas.drawRect(rectF, oppositePaint)
      canvas.saveLayer(rectF, regularBitmapPaint)
      canvas.drawBitmap(blurredCover, 0f, 0f, blurredBitmapPaint)
      canvas.drawRect(rectF, gradientPaint)
      canvas.restore()
    } else {
      super.onDraw(canvas)
    }
  }
}
