# screenshot-kt
Screenshot CLI utility for Macs written in Kotlin Native

## Usage

```bash
Usage: screenshot [<options>] <application_name>

  Take screenshots of specific apps and windows

Options:
  -a, --all                        Capture all windows
  -f, --filename=<text>            Filename to save the screenshot to
  -o, --output=(png|pdf|tiff|jpg)  screenshot.window.Output image format
  -s, --shadow                     Include window shadow in screenshot
  -t, --title=<text>               Title of windows from applicationName to capture
  -w, --window-selection=(allWindows|onScreenOnly|aboveWindow|belowWindow|excludeDesktop)
                                   Window selection options, this flag can be used multiple times
  -h, --help                       Show this message and exit

Arguments:
  <application_name>  Application name to capture windows from.
```
