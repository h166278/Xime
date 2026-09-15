package com.kingzcheung.xime.ui.keyboard

import org.junit.Assert.assertEquals
import org.junit.Test

class LongPressDefaultIndexTest {

    @Test
    fun `k_K defaults to lowercase then uppercase`() {
        val items = listOf("k", "K")
        assertEquals(0, longPressDefaultIndex(items, preferUppercase = false))
        assertEquals(1, longPressDefaultIndex(items, preferUppercase = true))
    }

    @Test
    fun `e with diacritics still picks E when preferring uppercase`() {
        val items = listOf("e", "E", "è", "é", "ê", "ë")
        assertEquals(0, longPressDefaultIndex(items, preferUppercase = false))
        assertEquals(1, longPressDefaultIndex(items, preferUppercase = true))
    }

    @Test
    fun `empty list stays at zero`() {
        assertEquals(0, longPressDefaultIndex(null, preferUppercase = true))
        assertEquals(0, longPressDefaultIndex(emptyList(), preferUppercase = false))
    }

    @Test
    fun `no letter stays at zero even when preferring uppercase`() {
        val items = listOf("行首", "撤销")
        assertEquals(0, longPressDefaultIndex(items, preferUppercase = false))
        assertEquals(0, longPressDefaultIndex(items, preferUppercase = true))
    }

    @Test
    fun `symbol plus fractions stays at first english symbol`() {
        val items = listOf("!", "½", "⅓", "¼")
        assertEquals(0, longPressDefaultIndex(items, preferUppercase = false))
        assertEquals(0, longPressDefaultIndex(items, preferUppercase = true))
    }

    @Test
    fun `symbol plus letters skips symbol and picks case`() {
        val zh = listOf("·", "q", "Q")
        assertEquals(1, longPressDefaultIndex(zh, preferUppercase = false))
        assertEquals(2, longPressDefaultIndex(zh, preferUppercase = true))
        val en = listOf("·", "q", "Q")
        assertEquals(1, longPressDefaultIndex(en, preferUppercase = false))
        assertEquals(2, longPressDefaultIndex(en, preferUppercase = true))
    }
}
