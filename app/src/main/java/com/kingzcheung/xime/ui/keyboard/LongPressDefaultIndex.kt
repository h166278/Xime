package com.kingzcheung.xime.ui.keyboard

/** 字母长按气泡默认项：优先选对应大小写的单字母，没有则大写回退到下标 1。 */
internal fun longPressDefaultIndex(items: List<String>?, preferUppercase: Boolean): Int {
    if (items.isNullOrEmpty()) return 0
    val i = items.indexOfFirst { label ->
        label.length == 1 && if (preferUppercase) label[0].isUpperCase() else label[0].isLowerCase()
    }
    return if (i >= 0) i else if (preferUppercase) 1.coerceAtMost(items.lastIndex) else 0
}
