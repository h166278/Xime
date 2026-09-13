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
    fun `uppercase missing falls back to index 1`() {
        val items = listOf("行首", "撤销")
        assertEquals(0, longPressDefaultIndex(items, preferUppercase = false))
        assertEquals(1, longPressDefaultIndex(items, preferUppercase = true))
    }
}
