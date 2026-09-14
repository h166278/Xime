package com.kingzcheung.xime.ui.keyboard

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ArmedSwipeTest {

    private val up = -50f
    private val down = 50f

    private fun step(y: Float) = updateArmedFlags(y, up, down)

    @Test
    fun `上滑过阈值武装上滑`() {
        val armed = step(-51f)
        assertTrue(armed.up)
        assertFalse(armed.down)
    }

    @Test
    fun `下滑过阈值武装下滑`() {
        val armed = step(51f)
        assertFalse(armed.up)
        assertTrue(armed.down)
    }

    @Test
    fun `上滑过线再滑回中间卸武装`() {
        assertTrue(step(-51f).up)
        val mid = step(-20f)
        assertFalse(mid.up)
        assertFalse(mid.down)
    }

    @Test
    fun `上滑过线再下滑过线只留撤回档`() {
        assertTrue(step(-51f).up)
        val both = step(80f)
        assertFalse(both.up)
        assertTrue(both.down)
    }

    @Test
    fun `先下滑再上滑过线只留清空档`() {
        assertTrue(step(51f).down)
        val both = step(-51f)
        assertTrue(both.up)
        assertFalse(both.down)
    }

    @Test
    fun `未过阈值不武装`() {
        val armed = step(-20f)
        assertEquals(ArmedFlags(), armed)
    }
}
