package com.kingzcheung.xime.ui.keyboard

import org.junit.Assert.assertEquals
import org.junit.Test

class ComposingShiftSwipeUpBubbleTest {

    @Test
    fun `已翻页才是上一页`() {
        assertEquals("上一页", composingShiftSwipeUpBubble(hasPrevPage = true))
    }

    @Test
    fun `第一页一码是造词`() {
        assertEquals("造词", composingShiftSwipeUpBubble(hasPrevPage = false))
    }
}
