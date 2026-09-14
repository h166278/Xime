package com.kingzcheung.xime.ui.keyboard

/**
 * 46 键中文盘：拉丁字母上屏后，五个符号键临时改成英文符号。
 * 点按或上滑其中任一键后收回。英文键盘不走这套。
 */
internal data class EnglishPunctFace(
    val tapLabel: String,
    val tapValue: String,
    val swipeLabel: String,
    val swipeValue: String,
)

internal fun englishPunctOverlayFace(keyId: String): EnglishPunctFace? = when (keyId) {
    ";" -> EnglishPunctFace(";", ";", ":", ":")
    "/" -> EnglishPunctFace("/", "/", "?", "?")
    "," -> EnglishPunctFace(",", ",", "<", "<")
    "." -> EnglishPunctFace(".", ".", ">", ">")
    "quote46" -> EnglishPunctFace("'", "'", "\"", "\"")
    else -> null
}

internal fun isLatinLetterCommit(text: String): Boolean =
    text.isNotEmpty() &&
        text.any { it.isLetter() && it.code < 128 } &&
        text.none { it.code in 0x4E00..0x9FFF }

/**
 * 稳定英文键盘空闲不上钩。inline_ascii 组合中（英文盘+ascii+组合）要上钩，
 * 上屏后回到中文盘才看得到五个键。
 */
internal fun shouldArmEnglishPunctOverlay(
    layout46: Boolean,
    committed: String,
    englishKeyboard: Boolean,
    asciiMode: Boolean,
    composing: Boolean,
): Boolean {
    if (!layout46) return false
    if (!isLatinLetterCommit(committed)) return false
    if (englishKeyboard && asciiMode && !composing) return false
    return true
}
