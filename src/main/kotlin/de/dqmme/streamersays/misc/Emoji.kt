package de.dqmme.streamersays.misc

enum class Emoji(private val emoji: String) {
    HOOK("✔"),
    X("✖");

    fun string() : String {
        return emoji
    }
}