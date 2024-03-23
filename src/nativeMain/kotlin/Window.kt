@file:OptIn(ExperimentalForeignApi::class)
package screenshot.window

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.memScoped
import kotlinx.datetime.Clock
import platform.CoreFoundation.CFTypeRef
import platform.CoreGraphics.*
import platform.Foundation.CFBridgingRelease
import platform.Foundation.CFBridgingRetain


const val DEFAULT_TITLE = "No title"
const val MAX_LENGTH = 12


typealias WindowInfo = Map<String, Any>
typealias Windows = List<WindowInfo>


enum class Output { png, pdf, tiff, jpg }


enum class WindowOption(val option: UInt) {
  allWindows(kCGWindowListOptionAll),
  onScreenOnly(kCGWindowListOptionOnScreenOnly),
  aboveWindow(kCGWindowListOptionOnScreenAboveWindow),
  belowWindow(kCGWindowListOptionOnScreenBelowWindow),
  excludeDesktop(kCGWindowListExcludeDesktopElements),
}


enum class WindowId(val id: String) {
  owner(kCGWindowOwnerName?.cast<String>()!!),
  window(kCGWindowName?.cast<String>()!!),
  number(kCGWindowNumber?.cast<String>()!!);

  companion object {
    operator fun contains(id: String): Boolean = enumValues<WindowId>().any { it.id == id }
  }
}


data class Window(val owner: String, val window: String?, val number: Long) {
  val filename: String
    get() {
      var title = window ?: DEFAULT_TITLE
      if (window != null && window.length > MAX_LENGTH) title = window.substring(0, MAX_LENGTH)

      return "$owner - $title ${Clock.System.now()}"
    }

  companion object {
    fun fromInfo(info: WindowInfo): Window = info.run {
      Window(
        get(WindowId.owner.id) as String,
        get(WindowId.window.id) as? String,
        get(WindowId.number.id) as Long
      )
    }
  }
}


fun Any.retain(): CFTypeRef? = CFBridgingRetain(this)
fun <T: CFTypeRef> Any.cast(): T? = memScoped { retain() as? T }

fun <T> CFTypeRef.release(): T? = CFBridgingRelease(this) as? T
fun <T> CFTypeRef.cast(): T? = memScoped { release() as? T }


fun getWindowInfo(
  options: UInt = WindowOption.allWindows.option,
  relativeTo: UInt = kCGNullWindowID
): List<Window>? = memScoped {
  CGWindowListCopyWindowInfo(options, relativeTo)
    ?.cast<Windows>()
    ?.map { info -> info.filterKeys { it in WindowId } }
    ?.map(Window::fromInfo)
}


fun getWindows(bitmask: UInt, application: String, title: String): List<Window>? =
  getWindowInfo(bitmask)
    ?.filter { application.lowercase() in it.owner.lowercase() }
    ?.filter { title.lowercase() in it.window?.lowercase().orEmpty() }
