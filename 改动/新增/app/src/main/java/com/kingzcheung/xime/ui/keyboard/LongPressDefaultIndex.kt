package com.kingzcheung.xime.ui.keyboard

/** 字母长按气泡默认项：优先选对应大小写的单字母；找不到就停在 0（数字/符号+分数不能跳到第二项）。 */
internal fun longPressDefaultIndex(items: List<String>?, preferUppercase: Boolean): Int {
    if (items.isNullOrEmpty()) return 0
    val i = items.indexOfFirst { label ->
        label.length == 1 && if (preferUppercase) label[0].isUpperCase() else label[0].isLowerCase()
    }
    return if (i >= 0) i else 0
}
