package ru.shvetsov.myTrello.custom

import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import ru.shvetsov.myTrello.utils.Generator
import kotlin.math.min

/**
 * Created by Alexander Shvetsov on 14.10.2019
 */
class CustomImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatImageView(context, attrs, defStyleAttr) {
    private var bitmapFromDrawable: Bitmap? = null
    private var currentDrawable: Drawable? = null

    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.BLACK
        textSize = 32f
        textAlign = Paint.Align.CENTER
    }
    private var cx: Float = 0f
    private var cy: Float = 0f
    private var radius: Float = 0f
    val text = "V"
    private val circlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Generator.generateColor()
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        val availableWidth = width
        val availableHeight = height
        cx = availableWidth / 2f
        cy = availableHeight / 2f
        val minSide = min(availableWidth, availableHeight)
        radius = minSide / 2f
    }

    override fun onDraw(canvas: Canvas) {
        if (drawable == null) {
            canvas.drawCircle(cx, cy, radius, circlePaint)
            canvas.drawText(text, cx, (cy - (textPaint.descent() + textPaint.ascent()) / 2), textPaint)
        } else {
            if (currentDrawable != drawable) {
                bitmapFromDrawable = getBitmapFromDrawable()
                currentDrawable = drawable
                canvas.drawBitmap(getCircularWithShader(bitmapFromDrawable!!), 0f, 0f, null)
            }
        }
    }

    private fun getCircularWithShader(bitmap: Bitmap): Bitmap {
        val matrix = Matrix()
        matrix.reset()
        matrix.postTranslate(-bitmap.width / 4f, 0f) // Двигаем картинку для центрирования по горизонтали
        val width = bitmap.width // Получаем размеры битмапа
        val height = bitmap.height
        val canvasBitmap = Bitmap.createBitmap(
            cx.toInt() * 2, cy.toInt() * 2,
            Bitmap.Config.ARGB_8888
        ) // создаем новый по размерам
        val shader = BitmapShader(
            bitmap, Shader.TileMode.CLAMP,
            Shader.TileMode.CLAMP
        )
        shader.setLocalMatrix(matrix)

        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            setShader(shader)
        }
        val canvas = Canvas(canvasBitmap)
        canvas.drawCircle(cx, cy, radius, paint)
        return canvasBitmap
    }


    private fun getBitmapFromDrawable(): Bitmap? {
        if (drawable == null)
            return null
        if (drawable is BitmapDrawable)
            return (drawable as BitmapDrawable).bitmap
        val bitmap = Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }
}