package com.kingzcheung.xime.ui.keyboard

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ArmedFlagsTest {

    private val upTh = -32f
    private val downTh = 32f

    private fun step(y: Float) = updateArmedFlags(y, upTh, downTh)

    @Test
    fun `没过线不武装`() {
        assertEquals(ArmedFlags(), step(-10f))
        assertEquals(ArmedFlags(), step(10f))
        assertEquals(ArmedFlags(), step(0f))
    }

    @Test
    fun `上滑过线武装清空档`() {
        val armed = step(-40f)
        assertTrue(armed.up)
        assertFalse(armed.down)
    }

    @Test
    fun `下滑过线武装撤回档`() {
        val armed = step(40f)
        assertFalse(armed.up)
        assertTrue(armed.down)
    }

    @Test
    fun `上滑过线再滑回中间卸武装 松手当点击`() {
        val up = step(-40f)
        val mid = step(-10f)
        assertTrue(up.up)
        assertFalse(mid.up)
        assertFalse(mid.down)
    }

    @Test
    fun `上滑过线再下滑过线只留撤回档`() {
        val up = step(-40f)
        val down = step(40f)
        assertTrue(up.up)
        assertFalse(down.up)
        assertTrue(down.down)
    }

    @Test
    fun `下滑过线再上滑过线只留清空档`() {
        val down = step(40f)
        val up = step(-40f)
        assertTrue(down.down)
        assertTrue(up.up)
        assertFalse(up.down)
    }

    @Test
    fun `跨过零点的中间档也卸武装`() {
        val up = step(-40f)
        val crossed = step(10f)
        assertTrue(up.up)
        assertFalse(crossed.up)
        assertFalse(crossed.down)
    }
}
