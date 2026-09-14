package com.kingzcheung.xime.ui.keyboard

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class EnglishPunctOverlayTest {

    @Test
    fun `五个键英文脸`() {
        assertEquals(";", englishPunctOverlayFace(";")?.tapLabel)
        assertEquals(":", englishPunctOverlayFace(";")?.swipeLabel)
        assertEquals(",", englishPunctOverlayFace(",")?.tapLabel)
        assertEquals("<", englishPunctOverlayFace(",")?.swipeLabel)
        assertEquals(".", englishPunctOverlayFace(".")?.tapLabel)
        assertEquals(">", englishPunctOverlayFace(".")?.swipeLabel)
        assertEquals("'", englishPunctOverlayFace("quote46")?.tapLabel)
        assertEquals("\"", englishPunctOverlayFace("quote46")?.swipeLabel)
        assertNull(englishPunctOverlayFace("a"))
    }

    @Test
    fun `拉丁上屏才算`() {
        assertTrue(isLatinLetterCommit("Hello"))
        assertTrue(isLatinLetterCommit("a"))
        assertFalse(isLatinLetterCommit("你"))
        assertFalse(isLatinLetterCommit("，"))
        assertFalse(isLatinLetterCommit(""))
        assertFalse(isLatinLetterCommit("你hao"))
    }

    @Test
    fun `26键不上钩`() {
        assertFalse(
            shouldArmEnglishPunctOverlay(
                layout46 = false,
                committed = "Hi",
                englishKeyboard = false,
                asciiMode = false,
                composing = false,
            )
        )
    }

    @Test
    fun `中文盘拉丁上屏上钩`() {
        assertTrue(
            shouldArmEnglishPunctOverlay(
                layout46 = true,
                committed = "Hi",
                englishKeyboard = false,
                asciiMode = false,
                composing = false,
            )
        )
    }

    @Test
    fun `稳定英文键盘不上钩`() {
        assertFalse(
            shouldArmEnglishPunctOverlay(
                layout46 = true,
                committed = "Hi",
                englishKeyboard = true,
                asciiMode = true,
                composing = false,
            )
        )
    }

    @Test
    fun `inline_ascii组合中上钩`() {
        assertTrue(
            shouldArmEnglishPunctOverlay(
                layout46 = true,
                committed = "Hi",
                englishKeyboard = true,
                asciiMode = true,
                composing = true,
            )
        )
    }
}
