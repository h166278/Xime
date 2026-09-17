package com.kingzcheung.xime.ui.keyboard

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class IdleSymbolTapTest {

    @Test
    fun `display_key 长按项是斜杠`() {
        assertEquals(listOf("/"), symbolLongPressItems("key", "/", null))
        assertEquals(listOf("/"), symbolLongPressItems("key", "/", emptyList()))
    }

    @Test
    fun `display_bubble 仍走气泡列表`() {
        assertEquals(listOf("〈", "<", "≤"), symbolLongPressItems("bubble", null, listOf("〈", "<", "≤")))
        assertNull(symbolLongPressItems("bubble", "/", null))
        assertNull(symbolLongPressItems("bubble", "/", emptyList()))
    }

    @Test
    fun `无长按标签不进长按`() {
        assertNull(symbolLongPressItems("key", null, null))
        assertNull(symbolLongPressItems("key", "", null))
        assertNull(symbolLongPressItems(null, null, null))
    }
}
