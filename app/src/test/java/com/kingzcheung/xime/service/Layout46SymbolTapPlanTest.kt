package com.kingzcheung.xime.service

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class Layout46SymbolTapPlanTest {

    @Test
    fun `前缀解析`() {
        assertEquals("/", parseLayout46SymbolKey("${LAYOUT46_SYMBOL_PREFIX}/"))
        assertEquals("quote46", parseLayout46SymbolKey("${LAYOUT46_SYMBOL_PREFIX}quote46"))
        assertNull(parseLayout46SymbolKey("/"))
        assertNull(parseLayout46SymbolKey("rime_punct:/"))
    }

    @Test
    fun `ascii 键 quote46 发单引号`() {
        assertEquals('\'', layout46SymbolAsciiKey("quote46", "'"))
        assertEquals('\'', layout46SymbolAsciiKey("quote46", "“”"))
        assertEquals('/', layout46SymbolAsciiKey("/", "/"))
        assertEquals(',', layout46SymbolAsciiKey(",", "，"))
    }

    @Test
    fun `中文有码一律进 Rime`() {
        assertEquals(
            Layout46SymbolTapAction.PROCESS,
            planLayout46SymbolTap(engineHasInput = true, asciiMode = false, idle = "、"),
        )
        assertEquals(
            Layout46SymbolTapAction.PROCESS,
            planLayout46SymbolTap(engineHasInput = true, asciiMode = false, idle = "“”"),
        )
        assertEquals(
            Layout46SymbolTapAction.PROCESS,
            planLayout46SymbolTap(engineHasInput = true, asciiMode = false, idle = null),
        )
    }

    @Test
    fun `中文空闲有 idle 直上屏字面不变`() {
        assertEquals(
            Layout46SymbolTapAction.COMMIT_IDLE,
            planLayout46SymbolTap(engineHasInput = false, asciiMode = false, idle = "、"),
        )
        assertEquals(
            Layout46SymbolTapAction.COMMIT_IDLE,
            planLayout46SymbolTap(engineHasInput = false, asciiMode = false, idle = "“”"),
        )
    }

    @Test
    fun `中文空闲无 idle 走 punctuator`() {
        assertEquals(
            Layout46SymbolTapAction.PROCESS,
            planLayout46SymbolTap(engineHasInput = false, asciiMode = false, idle = null),
        )
        assertEquals(
            Layout46SymbolTapAction.PROCESS,
            planLayout46SymbolTap(engineHasInput = false, asciiMode = false, idle = ""),
        )
    }

    @Test
    fun `英文空闲斜杠仍半角顿号`() {
        assertEquals(
            Layout46SymbolTapAction.COMMIT_IDLE,
            planLayout46SymbolTap(engineHasInput = false, asciiMode = true, idle = "､"),
        )
    }

    @Test
    fun `英文其余半角直上屏不进 Rime`() {
        assertEquals(
            Layout46SymbolTapAction.COMMIT_TAP,
            planLayout46SymbolTap(engineHasInput = false, asciiMode = true, idle = null),
        )
        assertEquals(
            Layout46SymbolTapAction.COMMIT_TAP,
            planLayout46SymbolTap(engineHasInput = true, asciiMode = true, idle = "､"),
        )
    }

    @Test
    fun `上滑前缀不误进点按`() {
        assertEquals(".", parseLayout46SymbolSwipeKey("${LAYOUT46_SYMBOL_SWIPE_PREFIX}."))
        assertEquals("quote46", parseLayout46SymbolSwipeKey("${LAYOUT46_SYMBOL_SWIPE_PREFIX}quote46"))
        assertEquals(";", parseLayout46SymbolSwipeKey("${LAYOUT46_SYMBOL_SWIPE_PREFIX};"))
        assertNull(parseLayout46SymbolSwipeKey("${LAYOUT46_SYMBOL_PREFIX}."))
        assertNull(parseLayout46SymbolKey("${LAYOUT46_SYMBOL_SWIPE_PREFIX}."))
    }

    @Test
    fun `有码上滑先选词再贴字面`() {
        assertEquals(
            Layout46SymbolSwipeAction.SELECT_THEN_COMMIT,
            planLayout46SymbolSwipe(rimeHasInput = true),
        )
        assertEquals(
            Layout46SymbolSwipeAction.COMMIT,
            planLayout46SymbolSwipe(rimeHasInput = false),
        )
    }

    @Test
    fun `有码上滑用 swipe_up 空闲用 idle_swipe`() {
        assertEquals("》", layout46SymbolSwipeText(rimeHasInput = true, swipe = "》", idleSwipe = "‘’"))
        assertEquals("‘’", layout46SymbolSwipeText(rimeHasInput = false, swipe = "\"", idleSwipe = "‘’"))
        assertEquals("﹖", layout46SymbolSwipeText(rimeHasInput = false, swipe = "﹖", idleSwipe = null))
        assertEquals("：", layout46SymbolSwipeText(rimeHasInput = true, swipe = "：", idleSwipe = null))
        assertNull(layout46SymbolSwipeText(rimeHasInput = false, swipe = null, idleSwipe = null))
    }

    @Test
    fun `覆盖脸前缀不误进点按和上滑`() {
        listOf("/", ",", ".", "quote46", ";").forEach { id ->
            assertEquals(id, parseLayout46OverlayKey("${LAYOUT46_OVERLAY_PREFIX}$id"))
            assertEquals(id, parseLayout46OverlaySwipeKey("${LAYOUT46_OVERLAY_SWIPE_PREFIX}$id"))
            assertNull(parseLayout46OverlayKey("${LAYOUT46_SYMBOL_PREFIX}$id"))
            assertNull(parseLayout46SymbolKey("${LAYOUT46_OVERLAY_PREFIX}$id"))
            assertNull(parseLayout46OverlaySwipeKey("${LAYOUT46_OVERLAY_PREFIX}$id"))
            assertNull(parseLayout46OverlayKey("${LAYOUT46_OVERLAY_SWIPE_PREFIX}$id"))
        }
    }

    @Test
    fun `覆盖脸有码进 popping 空闲贴英文符`() {
        assertEquals(
            Layout46OverlayTapAction.PROCESS,
            planLayout46OverlayTap(rimeHasInput = true),
        )
        assertEquals(
            Layout46OverlayTapAction.COMMIT_OVERLAY,
            planLayout46OverlayTap(rimeHasInput = false),
        )
    }
}
