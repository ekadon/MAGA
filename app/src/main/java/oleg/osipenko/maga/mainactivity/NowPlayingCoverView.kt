package oleg.osipenko.maga.mainactivity

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.LinearGradient
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
import android.renderscript.ScriptIntrinsicResize
import android.renderscript.Type
import android.util.AttributeSet
import android.view.View
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import oleg.osipenko.maga.R
import timber.log.Timber

/**
 * Custom view for displaying movie cover with partially blurred area.
 */
class NowPlayingCoverView(context: Context?, attrs: AttributeSet?) :
  View(context, attrs) {

  companion object {
    const val BLUR_RADIUS = 15f
    const val BLACK = -0x1000000
    const val TRANSPARENT = 0x00000000
    const val SEMI_TRANSPARENT = -0x80000000
    const val RESIZE_FACTOR = 0.0625f
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


  override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
    val width = View.MeasureSpec.getSize(widthMeasureSpec)
    val height = View.MeasureSpec.getSize(heightMeasureSpec)
    rect.set(0, 0, width, height)
    rectF.set(rect)
    init()

    setMeasuredDimension(width, height)
  }

  @Suppress("ComplexMethod")
  private fun init() {
    initPaints()

    if (imageUrl.isNotEmpty()) {
      GlobalScope.launch {
        try {
          cover =
            Picasso.get().load(imageUrl).error(R.drawable.placeholder).get()

          launch(Dispatchers.Main) {
            // Get coordinates of the center-cropped cover
            val croppedRect = getCropCoordinates()

            cover = Bitmap.createScaledBitmap(
              cover,
              croppedRect.width(),
              croppedRect.height(),
              true
            )
            cover = Bitmap.createBitmap(
              cover,
              croppedRect.left,
              croppedRect.top,
              rect.width(),
              rect.height()
            )

            // Create bitmap for blurred copy
            blurredCover = Bitmap.createBitmap(
              croppedRect.width(),
              croppedRect.height(),
              Bitmap.Config.ARGB_8888)


            // determine resized copy size for blur
            val resW = Math.round(rect.width() * RESIZE_FACTOR)
            val resH = Math.round(rect.height() * RESIZE_FACTOR)

            val rs = RenderScript.create(context)

            val allocIn = Allocation.createFromBitmap(rs, cover)
            val tempT = Type.createXY(rs, allocIn.element, resW, resH)
            val t =
              Type.createXY(rs, allocIn.element, rect.width(), rect.height())
            val allocDownscaled = Allocation.createTyped(rs, tempT)
            val allocBlurred = Allocation.createTyped(rs, tempT)
            val allocOut = Allocation.createTyped(rs, t)

            val resizeScript = ScriptIntrinsicResize.create(rs)
            val blurScript = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs))

            // downscale
            resizeScript.setInput(allocIn)
            resizeScript.forEach_bicubic(allocDownscaled)

            // blur
            blurScript.setInput(allocDownscaled)
            blurScript.setRadius(BLUR_RADIUS)
            blurScript.forEach(allocBlurred)

            // upscale to original size
            resizeScript.setInput(allocBlurred)
            resizeScript.forEach_bicubic(allocOut)

            allocOut.copyTo(blurredCover)
          }

        } catch (e: Exception) {
          Timber.e(e)
          cover = BitmapFactory.decodeResource(
            resources, R.drawable.placeholder
          )
          cover = Bitmap.createScaledBitmap(
            cover, rect.width(), rect.height(), true
          )
        }
      }
    }
  }

  private fun initPaints() {
    setLayerType(View.LAYER_TYPE_SOFTWARE, null)
    gradientPaint.isDither = true
    gradientPaint.shader = gradientShader()
    gradientPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_OUT)
    oppositePaint.isDither = true
    oppositePaint.shader = oppositeShader()
    oppositePaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_OUT)
  }

  private fun gradientShader(): Shader = LinearGradient(
    rectF.left, rectF.top, rectF.left, rectF.bottom, BLACK, SEMI_TRANSPARENT,
    Shader.TileMode.CLAMP
  )

  private fun oppositeShader(): Shader = LinearGradient(
    rectF.left, rectF.top, rectF.left, rectF.bottom, TRANSPARENT, BLACK,
    Shader.TileMode.CLAMP
  )

  private fun getCropCoordinates(): Rect {
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

    return Rect(x, y, x + desiredWidth, y + desiredHeight)
  }

  override fun onDraw(canvas: Canvas) {
    if (::cover.isInitialized) {
      canvas.drawBitmap(cover, 0f, 0f, regularBitmapPaint)
      if (::blurredCover.isInitialized) {
        canvas.drawRect(rectF, oppositePaint)
        canvas.saveLayer(rectF, regularBitmapPaint)
        canvas.drawBitmap(blurredCover, 0f, 0f, blurredBitmapPaint)
        canvas.drawRect(rectF, gradientPaint)
        canvas.restore()
      }
    } else {
      super.onDraw(canvas)
    }
  }
}
