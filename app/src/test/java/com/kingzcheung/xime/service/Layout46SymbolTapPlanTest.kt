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
}
