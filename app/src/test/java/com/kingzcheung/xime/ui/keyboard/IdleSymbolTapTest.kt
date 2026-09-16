package com.kingzcheung.xime.ui.keyboard

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class IdleSymbolTapTest {

    @Test
    fun `空闲走 idle`() {
        assertEquals("、", idleSymbolTapValue(composing = false, idleValue = "、", tapValue = "/"))
        assertEquals("､", idleSymbolTapValue(composing = false, idleValue = "､", tapValue = "/"))
    }

    @Test
    fun `组合走 tap`() {
        assertEquals("/", idleSymbolTapValue(composing = true, idleValue = "、", tapValue = "/"))
    }

    @Test
    fun `无 idle 走 tap`() {
        assertEquals("/", idleSymbolTapValue(composing = false, idleValue = null, tapValue = "/"))
        assertEquals("/", idleSymbolTapValue(composing = false, idleValue = "", tapValue = "/"))
    }

    @Test
    fun `空闲上滑走 idle_swipe`() {
        assertEquals("‘’", idleSymbolTapValue(composing = false, idleValue = "‘’", tapValue = "\""))
        assertEquals("\"", idleSymbolTapValue(composing = true, idleValue = "‘’", tapValue = "\""))
    }

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
