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

    @Test
    fun `注入候选空格选首位aeuio选重`() {
        assertEquals(0, injectedPunctSelectIndex(" "))
        assertEquals(0, injectedPunctSelectIndex("space"))
        assertEquals(1, injectedPunctSelectIndex("a"))
        assertEquals(1, injectedPunctSelectIndex("A"))
        assertEquals(2, injectedPunctSelectIndex("e"))
        assertEquals(2, injectedPunctSelectIndex("E"))
        assertEquals(3, injectedPunctSelectIndex("u"))
        assertEquals(4, injectedPunctSelectIndex("i"))
        assertEquals(5, injectedPunctSelectIndex("o"))
        assertEquals(null, injectedPunctSelectIndex("q"))
        assertEquals(null, injectedPunctSelectIndex("1"))
    }

    @Test
    fun `注入栏注释首位空后面aeuio`() {
        assertEquals(listOf("", "a", "e"), injectedPunctComments(3))
        assertEquals(listOf("", "a", "e", "u", "i", "o"), injectedPunctComments(6))
        assertEquals(emptyList<String>(), injectedPunctComments(0))
        assertEquals(listOf(""), injectedPunctComments(1))
        assertEquals(listOf("", "a", "e", "u", "i", "o", ""), injectedPunctComments(7))
    }

    @Test
    fun `破折号候选空格是全角减号a是破折号`() {
        assertEquals(listOf("－", "——", "—", "-", "---", "─"), DASH_CANDIDATES)
        assertEquals(DASH_CANDIDATES, rimePunctCandidates("${RIME_PUNCT_PREFIX}-"))
        assertEquals(MIDDLE_DOT_CANDIDATES, rimePunctCandidates("${RIME_PUNCT_PREFIX}`"))
        assertEquals(null, rimePunctCandidates("${RIME_PUNCT_PREFIX}#"))
        assertEquals(null, rimePunctCandidates("-"))
    }

    @Test
    fun `英文46长按符号注入表气泡项排首位`() {
        assertEquals("～", rimePunctCandidates("${RIME_PUNCT_PREFIX}~")!!.first())
        assertEquals("＋", rimePunctCandidates("${RIME_PUNCT_PREFIX}+")!!.first())
        assertEquals("＝", rimePunctCandidates("${RIME_PUNCT_PREFIX}=")!!.first())
        assertEquals("——", rimePunctCandidates("${RIME_PUNCT_PREFIX}_")!!.first())
        assertEquals("『", rimePunctCandidates("${RIME_PUNCT_PREFIX}{")!!.first())
        assertEquals("』", rimePunctCandidates("${RIME_PUNCT_PREFIX}}")!!.first())
        assertEquals("「", rimePunctCandidates("${RIME_PUNCT_PREFIX}[")!!.first())
        assertEquals("」", rimePunctCandidates("${RIME_PUNCT_PREFIX}]")!!.first())
        assertEquals("、", rimePunctCandidates("${RIME_PUNCT_PREFIX}\\")!!.first())
        assertEquals("｜", rimePunctCandidates("${RIME_PUNCT_PREFIX}|")!!.first())
        assertEquals("×", rimePunctCandidates("${RIME_PUNCT_PREFIX}*")!!.first())
        assertEquals("÷", rimePunctCandidates("${RIME_PUNCT_PREFIX}/")!!.first())
        assertEquals(null, rimePunctCandidates("${RIME_PUNCT_PREFIX}g"))
    }

    @Test
    fun `有注入动作才算注入栏`() {
        val injected = CandidateState(
            candidates = MIDDLE_DOT_CANDIDATES,
            candidateComments = injectedPunctComments(3),
            candidateActions = MIDDLE_DOT_CANDIDATES.map { CandidateAction.injected(it) },
        )
        assertTrue(hasInjectedCandidates(injected))
        assertFalse(hasInjectedCandidates(CandidateState()))
        assertFalse(
            hasInjectedCandidates(
                CandidateState(
                    candidates = listOf("#"),
                    candidateActions = listOf(CandidateAction.plugin("#")),
                )
            )
        )
    }
}
