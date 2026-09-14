package com.kingzcheung.xime.ui.keyboard

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ShiftSwipeZoneTest {

    private val action = 50f
    private val cancel = 90f

    @Test
    fun `未过动作线是点按`() {
        assertEquals(ShiftSwipeZone.TAP, resolveShiftSwipeZone(0f, action, cancel))
        assertEquals(ShiftSwipeZone.TAP, resolveShiftSwipeZone(49f, action, cancel))
    }

    @Test
    fun `过动作线未到取消是动作`() {
        assertEquals(ShiftSwipeZone.ACTION, resolveShiftSwipeZone(50f, action, cancel))
        assertEquals(ShiftSwipeZone.ACTION, resolveShiftSwipeZone(89f, action, cancel))
    }

    @Test
    fun `过取消线是取消`() {
        assertEquals(ShiftSwipeZone.CANCEL, resolveShiftSwipeZone(90f, action, cancel))
        assertEquals(ShiftSwipeZone.CANCEL, resolveShiftSwipeZone(140f, action, cancel))
    }

    @Test
    fun `动作后再滑回变成点按`() {
        assertEquals(ShiftSwipeZone.ACTION, resolveShiftSwipeZone(60f, action, cancel))
        assertEquals(ShiftSwipeZone.TAP, resolveShiftSwipeZone(20f, action, cancel))
    }

    @Test
    fun `直接下滑无操作`() {
        assertEquals(ShiftSwipeZone.DOWN, resolveShiftSwipeZone(-50f, action, cancel))
        assertEquals(ShiftSwipeZone.DOWN, resolveShiftSwipeZone(-80f, action, cancel))
        assertEquals(ShiftSwipeZone.TAP, resolveShiftSwipeZone(-49f, action, cancel))
    }

    @Test
    fun `上滑过线再越过起点仍算点按`() {
        assertEquals(
            ShiftSwipeZone.TAP,
            settleShiftSwipeZone(ShiftSwipeZone.DOWN, reachedAction = true),
        )
        assertEquals(
            ShiftSwipeZone.DOWN,
            settleShiftSwipeZone(ShiftSwipeZone.DOWN, reachedAction = false),
        )
        assertEquals(
            ShiftSwipeZone.ACTION,
            settleShiftSwipeZone(ShiftSwipeZone.ACTION, reachedAction = true),
        )
    }

    @Test
    fun `造词才走三档`() {
        assertTrue(isComposingShiftWordCreate("造词"))
        assertFalse(isComposingShiftWordCreate("上一页"))
        assertFalse(isComposingShiftWordCreate("组合"))
        assertFalse(isComposingShiftWordCreate("跳尾"))
        assertFalse(isComposingShiftWordCreate("纯单"))
        assertFalse(isComposingShiftWordCreate("A"))
        assertFalse(isComposingShiftWordCreate(""))
    }

    @Test
    fun `反查也走三档滑回是下一页`() {
        assertTrue(isReverseLookupInput("a"))
        assertTrue(isReverseLookupInput("an"))
        assertFalse(isReverseLookupInput("q"))
        assertFalse(isReverseLookupInput(""))
        assertTrue(isComposingShiftThreeZone("跳尾", "an"))
        assertTrue(isComposingShiftThreeZone("上一页", "a"))
        assertTrue(isComposingShiftThreeZone("", "a"))
        assertFalse(isComposingShiftThreeZone("跳尾", "qwe"))
        assertFalse(isComposingShiftThreeZone("组合", "qwrt"))
        assertEquals("下一页", composingShiftThreeZoneTapLabel("an"))
        assertEquals("点 Shift", composingShiftThreeZoneTapLabel("q"))
    }

    @Test
    fun `气泡文案按档位走`() {
        assertNull(shiftSwipeZoneBubble(ShiftSwipeZone.TAP, "造词", reachedAction = false))
        assertEquals("点 Shift", shiftSwipeZoneBubble(ShiftSwipeZone.TAP, "造词", reachedAction = true))
        assertEquals(
            "下一页",
            shiftSwipeZoneBubble(ShiftSwipeZone.TAP, "跳尾", reachedAction = true, tapLabel = "下一页"),
        )
        assertEquals("造词", shiftSwipeZoneBubble(ShiftSwipeZone.ACTION, "造词", reachedAction = true))
        assertEquals("默认大写", shiftSwipeZoneBubble(ShiftSwipeZone.ACTION, "默认大写", reachedAction = true))
        assertEquals("取消", shiftSwipeZoneBubble(ShiftSwipeZone.CANCEL, "造词", reachedAction = true))
        assertEquals("取消", shiftSwipeZoneBubble(ShiftSwipeZone.CANCEL, "默认小写", reachedAction = true))
        assertNull(shiftSwipeZoneBubble(ShiftSwipeZone.DOWN, "造词", reachedAction = false))
        assertNull(shiftSwipeZoneBubble(ShiftSwipeZone.DOWN, "默认大写", reachedAction = false))
    }
}
