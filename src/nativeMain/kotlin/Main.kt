@file:OptIn(ExperimentalForeignApi::class)

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.multiple
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.enum
import kotlinx.cinterop.ExperimentalForeignApi
import platform.posix.system
import screenshot.window.Output
import screenshot.window.Window
import screenshot.window.WindowOption
import screenshot.window.getWindows


const val CMD = "screencapture"
const val DEFAULT_FILENAME = "out.png"
const val DEFAULT_TITLE = ""


fun takeScreenshot(
  filename: String?,
  window: Window,
  output: Output = Output.png,
  options: Array<String>
): String? {
  val args = mutableListOf("-l", window.number.toString(), *options)
  if (filename != null) args.add(filename)
  else args.add("\"${window.filename}.$output\"")
  val name = args.last()

  val cmd = "$CMD ${args.joinToString(" ")}"
  val rc = system(cmd)
  println("$name: rc=$rc")

  return name
}


class Cli : CliktCommand(
  name = "screenshot",
  help = "Take screenshots of specific apps and windows",
  printHelpOnEmptyArgs = true,
) {
  val allWindows by option("-a", "--all", help = "Capture all windows").flag()
  val filename by option("-f", "--filename", help = "Filename to save the screenshot to").default(DEFAULT_FILENAME)
  val output by option("-o", "--output", help = "screenshot.window.Output image format").enum<Output>().default(Output.png)
  val shadow by option("-s", "--shadow", help = "Include window shadow in screenshot").flag()
  val title by option("-t", "--title", help = "Title of windows from applicationName to capture").default(DEFAULT_TITLE)
  val windowSelection by option(
    "-w", "--window-selection",
    help = "Window selection options, this flag can be used multiple times"
  ).enum<WindowOption>().multiple()
  val applicationName by argument(name = "application_name", help = "Application name to capture windows from.")

  private fun getOptions(): Array<String> {
    val opts = mutableListOf<String>()

    if (output.name.isNotEmpty()) opts.add("-t $output")
    if (!shadow) opts.add("-o")

    return opts.toTypedArray()
  }

  override fun run() {
    val bitmask = windowSelection.sumOf { it.option }
    val windows = getWindows(bitmask, applicationName, title)

    if (windows.isNullOrEmpty()) {
      println("No windows found for $applicationName")
      return
    }

    val options = getOptions()

    if (allWindows) {
      windows.forEach { takeScreenshot(null, it, output, options) }
      return
    }

    val window = windows.first()
    takeScreenshot(filename, window, output, options)
  }
}


fun main(args: Array<String>) = Cli().main(args)
