package com.kingzcheung.xime.ui.keyboard

import org.junit.Assert.assertEquals
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
}
