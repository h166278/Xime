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
    fun `中文一码进 Rime 斜杠两码交词引号三码交词`() {
        assertEquals(
            Layout46SymbolTapAction.PROCESS,
            planLayout46SymbolTap(engineHasInput = true, asciiMode = false, idle = "、", inputLength = 1, keyId = "/"),
        )
        assertEquals(
            Layout46SymbolTapAction.SELECT_THEN_COMMIT,
            planLayout46SymbolTap(engineHasInput = true, asciiMode = false, idle = "、", inputLength = 2, keyId = "/"),
        )
        assertEquals(
            Layout46SymbolTapAction.PROCESS,
            planLayout46SymbolTap(engineHasInput = true, asciiMode = false, idle = "“”", inputLength = 2, keyId = "quote46"),
        )
        assertEquals(
            Layout46SymbolTapAction.SELECT_THEN_COMMIT,
            planLayout46SymbolTap(engineHasInput = true, asciiMode = false, idle = "“”", inputLength = 3, keyId = "quote46"),
        )
        assertEquals(
            Layout46SymbolTapAction.PROCESS,
            planLayout46SymbolTap(engineHasInput = true, asciiMode = false, idle = null, inputLength = 1, keyId = ","),
        )
        assertEquals(
            Layout46SymbolTapAction.SELECT_THEN_COMMIT,
            planLayout46SymbolTap(engineHasInput = true, asciiMode = false, idle = null, inputLength = 2, keyId = ","),
        )
        assertEquals(
            Layout46SymbolTapAction.PROCESS,
            planLayout46SymbolTap(engineHasInput = true, asciiMode = false, idle = null, inputLength = 1, keyId = "."),
        )
        assertEquals(
            Layout46SymbolTapAction.SELECT_THEN_COMMIT,
            planLayout46SymbolTap(engineHasInput = true, asciiMode = false, idle = null, inputLength = 2, keyId = "."),
        )
    }

    @Test
    fun `两码以上点按贴逗号分号弯引号`() {
        assertEquals("、", layout46ComposingPunctLiteral("/", "、"))
        assertEquals("、", layout46ComposingPunctLiteral("/", null))
        assertEquals("，", layout46ComposingPunctLiteral(",", null))
        assertEquals("。", layout46ComposingPunctLiteral(".", null))
        assertEquals("；", layout46ComposingPunctLiteral(";", null))
        assertEquals("“”", layout46ComposingPunctLiteral("quote46", "“”"))
        assertEquals("“”", layout46ComposingPunctLiteral("quote46", null))
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
        assertEquals("‘’", layout46SymbolSwipeText(rimeHasInput = true, swipe = "‘’", idleSwipe = "‘’"))
        assertEquals("‘’", layout46SymbolSwipeText(rimeHasInput = false, swipe = "“”", idleSwipe = "‘’"))
        assertEquals("？", layout46SymbolSwipeText(rimeHasInput = false, swipe = "？", idleSwipe = null))
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

    private fun tap(
        input: String,
        schemaId: String,
        keyId: String,
        idle: String? = null,
    ) = planLayout46SymbolTap(
        engineHasInput = true,
        asciiMode = false,
        idle = idle,
        inputLength = input.length,
        keyId = keyId,
        input = input,
        schemaId = schemaId,
    )

    @Test
    fun `空方案三码引号仍交词`() {
        assertEquals(
            Layout46SymbolTapAction.SELECT_THEN_COMMIT,
            tap("hui", "", "quote46", idle = "“”"),
        )
        assertEquals(
            Layout46SymbolTapAction.SELECT_THEN_COMMIT,
            tap("jk", "", "/"),
        )
    }

    @Test
    fun `飞系三码引号交词扩标点进引擎`() {
        assertEquals(Layout46SymbolTapAction.SELECT_THEN_COMMIT, tap("hui", "sbfd", "quote46"))
        assertEquals(Layout46SymbolTapAction.SELECT_THEN_COMMIT, tap("hui", "sbfd", ";"))
        assertEquals(Layout46SymbolTapAction.SELECT_THEN_COMMIT, tap("hui", "sbfd", ","))
        assertEquals(Layout46SymbolTapAction.PROCESS, tap("hka", "sbfd", "quote46"))
        assertEquals(Layout46SymbolTapAction.PROCESS, tap("hka", "sbfm", "/"))
        assertEquals(Layout46SymbolTapAction.PROCESS, tap("hkb", "sbfy", ";"))
        assertEquals(Layout46SymbolTapAction.PROCESS, tap("hkj", "sbfd", "quote46"))
        assertEquals(Layout46SymbolTapAction.SELECT_THEN_COMMIT, tap("hkj", "sbfd", "/"))
        assertEquals(Layout46SymbolTapAction.PROCESS, tap("huk", "sbfd", ";"))
        assertEquals(Layout46SymbolTapAction.SELECT_THEN_COMMIT, tap("huk", "sbfd", ","))
        assertEquals(Layout46SymbolTapAction.PROCESS, tap("huib", "sbfd", "quote46"))
        assertEquals(Layout46SymbolTapAction.PROCESS, tap("j/a", "sbfd", "/"))
    }

    @Test
    fun `拼音反查后引号进引擎`() {
        assertEquals(Layout46SymbolTapAction.PROCESS, tap("anihao", "sbfd", "quote46"))
        assertEquals(Layout46SymbolTapAction.PROCESS, tap("anihao", "sbpy", "quote46"))
        assertEquals(Layout46SymbolTapAction.PROCESS, tap("enihao", "sbfd", "quote46"))
        assertEquals(Layout46SymbolTapAction.SELECT_THEN_COMMIT, tap("anihao", "sbfd", ","))
        assertEquals(Layout46SymbolTapAction.SELECT_THEN_COMMIT, tap("inihao", "sbfd", "quote46"))
        assertEquals(Layout46SymbolTapAction.SELECT_THEN_COMMIT, tap("aeiou", "sbfd", "quote46"))
    }

    @Test
    fun `飞天象码三码五键进引擎`() {
        assertEquals(Layout46SymbolTapAction.PROCESS, tap("hui", "sbft", "quote46"))
        assertEquals(Layout46SymbolTapAction.PROCESS, tap("hui", "sbxm", "/"))
        assertEquals(Layout46SymbolTapAction.PROCESS, tap("hui", "sbmf", ";"))
        assertEquals(Layout46SymbolTapAction.PROCESS, tap("hkb", "sbxm", "."))
    }

    @Test
    fun `整句引号分号进引擎斜杠仍交词`() {
        assertEquals(Layout46SymbolTapAction.PROCESS, tap("nihao", "sbpy", "quote46"))
        assertEquals(Layout46SymbolTapAction.PROCESS, tap("aoe", "sbpy", "quote46"))
        assertEquals(Layout46SymbolTapAction.PROCESS, tap("hui", "sbyp", ";"))
        assertEquals(Layout46SymbolTapAction.PROCESS, tap("hkj", "sbzz", "quote46"))
        assertEquals(Layout46SymbolTapAction.SELECT_THEN_COMMIT, tap("nihao", "sbpy", ","))
        assertEquals(Layout46SymbolTapAction.SELECT_THEN_COMMIT, tap("nihao", "sbpy", "/"))
    }

    @Test
    fun `简码三码引号拆词分号仍交词`() {
        assertEquals(Layout46SymbolTapAction.PROCESS, tap("hkj", "sbjm", "quote46"))
        assertEquals(Layout46SymbolTapAction.SELECT_THEN_COMMIT, tap("hui", "sbjm", ";"))
        assertEquals(Layout46SymbolTapAction.SELECT_THEN_COMMIT, tap("hui", "sbjm", ","))
    }

    @Test
    fun `双拼三码引号分号进引擎斜杠交词`() {
        assertEquals(Layout46SymbolTapAction.PROCESS, tap("hui", "sbxh", "quote46"))
        assertEquals(Layout46SymbolTapAction.PROCESS, tap("hui", "sbzr", ";"))
        assertEquals(Layout46SymbolTapAction.SELECT_THEN_COMMIT, tap("hui", "sbxh", "/"))
    }
}
