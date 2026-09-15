package com.kingzcheung.xime.service

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class RimePunctKeyCodeTest {

    @Test
    fun `反引号通道合法`() {
        assertTrue(isRimePunctKey("${RIME_PUNCT_PREFIX}`"))
    }

    @Test
    fun `非通道非法`() {
        assertFalse(isRimePunctKey("`"))
        assertFalse(isRimePunctKey("·"))
        assertFalse(isRimePunctKey("q"))
        assertFalse(isRimePunctKey(RIME_PUNCT_PREFIX))
        assertFalse(isRimePunctKey("${RIME_PUNCT_PREFIX}``"))
        assertFalse(isRimePunctKey("${RIME_PUNCT_PREFIX}·"))
        assertFalse(isRimePunctKey("${RIME_UPPER_PREFIX}B"))
    }

    @Test
    fun `间隔号候选就是这三个`() {
        assertEquals(listOf("·", "・", "･"), MIDDLE_DOT_CANDIDATES)
    }

    @Test
    fun `注入候选点选不依赖引擎组合`() {
        val action = CandidateAction.injected("·")
        assertTrue(action.isPluginCandidate)
        assertTrue(action.isInjectedCandidate)
        assertEquals("·", action.commitText)
        val plugin = CandidateAction.plugin("·")
        assertTrue(plugin.isPluginCandidate)
        assertFalse(plugin.isInjectedCandidate)
    }
}
