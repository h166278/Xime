package com.kingzcheung.xime.ui.keyboard

import com.kingzcheung.xime.service.RIME_UPPER_PREFIX
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ComposingLetterSwipeUpTest {

    @Test
    fun `中文组合态字母上滑`() {
        assertTrue(shouldComposingLetterSwipeUp(isComposing = true, isAsciiMode = false, key = "b"))
        assertTrue(shouldComposingLetterSwipeUp(isComposing = true, isAsciiMode = false, key = "Q"))
    }

    @Test
    fun `空闲不上滑改大写`() {
        assertFalse(shouldComposingLetterSwipeUp(isComposing = false, isAsciiMode = false, key = "b"))
    }

    @Test
    fun `英文模式不改`() {
        assertFalse(shouldComposingLetterSwipeUp(isComposing = true, isAsciiMode = true, key = "b"))
    }

    @Test
    fun `非字母不改`() {
        assertFalse(shouldComposingLetterSwipeUp(isComposing = true, isAsciiMode = false, key = "1"))
        assertFalse(shouldComposingLetterSwipeUp(isComposing = true, isAsciiMode = false, key = ";"))
        assertFalse(shouldComposingLetterSwipeUp(isComposing = true, isAsciiMode = false, key = "space"))
        assertFalse(shouldComposingLetterSwipeUp(isComposing = true, isAsciiMode = false, key = "ñ"))
    }

    @Test
    fun `通道键是大写前缀`() {
        assertEquals("${RIME_UPPER_PREFIX}B", composingLetterSwipeUpKey("b"))
        assertEquals("${RIME_UPPER_PREFIX}B", composingLetterSwipeUpKey("B"))
        assertEquals("B", composingLetterSwipeUpLabel("b"))
    }
}
