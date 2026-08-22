package com.elegantgalaxy.tvlauncher.ui.settings

enum class SoundMode(val label: String) {
    NORMAL("Normal"),
    MOVIE("Movie"),
    MUSIC("Music"),
    NEWS("News"),
}

enum class SoundOutput(val label: String) {
    TV_SPEAKER("TV Speaker"),
    OPTICAL("Optical"),
    HDMI_ARC("HDMI ARC"),
    BLUETOOTH("Bluetooth"),
}

enum class SleepTimer(val label: String) {
    OFF("Off"),
    MIN_15("15 min"),
    MIN_30("30 min"),
    MIN_60("60 min"),
}
