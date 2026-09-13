package com.kingzcheung.xime.ui.keyboard

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ArmedSwipeTest {

    private val up = -50f
    private val down = 50f

    @Test
    fun `上滑过阈值武装上滑`() {
        val armed = updateArmedFlags(ArmedFlags(), -51f, up, down)
        assertTrue(armed.up)
        assertFalse(armed.down)
    }

    @Test
    fun `下滑过阈值武装下滑`() {
        val armed = updateArmedFlags(ArmedFlags(), 51f, up, down)
        assertFalse(armed.up)
        assertTrue(armed.down)
    }

    @Test
    fun `上滑武装后再下滑上滑旗标保留`() {
        val upArmed = updateArmedFlags(ArmedFlags(), -51f, up, down)
        val both = updateArmedFlags(upArmed, 80f, up, down)
        assertTrue(both.up)
        assertTrue(both.down)
    }

    @Test
    fun `先下滑再上滑两旗标都在 松手走上滑`() {
        val downArmed = updateArmedFlags(ArmedFlags(), 51f, up, down)
        val both = updateArmedFlags(downArmed, -51f, up, down)
        assertTrue(both.up)
        assertTrue(both.down)
    }

    @Test
    fun `未过阈值不武装`() {
        val armed = updateArmedFlags(ArmedFlags(), -20f, up, down)
        assertFalse(armed.up)
        assertFalse(armed.down)
    }
}
