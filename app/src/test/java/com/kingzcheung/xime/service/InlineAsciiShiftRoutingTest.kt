package com.kingzcheung.xime.service

import com.kingzcheung.xime.viewmodel.ShiftMode
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class InlineAsciiShiftRoutingTest {

    private fun route(
        layout46: Boolean = true,
        chineseMode: Boolean = true,
        isLetter: Boolean = true,
        isShifted: Boolean = true,
        composing: Boolean = false,
        hasInput: Boolean = false,
        shiftMode: ShiftMode = ShiftMode.SINGLE,
        autoInlineEnabled: Boolean = true,
    ) = shouldRouteShiftLetterToInlineAscii(
        layout46 = layout46,
        chineseMode = chineseMode,
        isLetter = isLetter,
        isShifted = isShifted,
        composing = composing,
        hasInput = hasInput,
        shiftMode = shiftMode,
        autoInlineEnabled = autoInlineEnabled,
    )

    @Test
    fun `46键空闲单击Shift字母走临时英文`() {
        assertTrue(route())
    }

    @Test
    fun `26键不走`() {
        assertFalse(route(layout46 = false))
    }

    @Test
    fun `双击Caps不走`() {
        assertFalse(route(shiftMode = ShiftMode.CAPS))
    }

    @Test
    fun `Shift未按下不走`() {
        assertFalse(route(isShifted = false, shiftMode = ShiftMode.OFF))
    }

    @Test
    fun `已在组合态不走_先字母再Shift仍走Tab`() {
        assertFalse(route(composing = true, hasInput = true))
    }

    @Test
    fun `英文模式不走`() {
        assertFalse(route(chineseMode = false))
    }

    @Test
    fun `方案未开auto_inline不走`() {
        assertFalse(route(autoInlineEnabled = false))
    }

    @Test
    fun `非字母不走`() {
        assertFalse(route(isLetter = false))
    }
}
