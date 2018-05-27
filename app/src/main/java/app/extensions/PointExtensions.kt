package app.extensions

import android.graphics.Point
import android.graphics.Rect

operator fun Rect.contains(point: Point): Boolean {
    return point.x in left..right && point.y in top..bottom
}