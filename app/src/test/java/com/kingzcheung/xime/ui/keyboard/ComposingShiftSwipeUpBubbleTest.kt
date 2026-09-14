package com.kingzcheung.xime.ui.keyboard

import org.junit.Assert.assertEquals
import org.junit.Test

class ComposingShiftSwipeUpBubbleTest {

    @Test
    fun `已翻页是上一页`() {
        assertEquals("上一页", composingShiftSwipeUpBubble(hasPrevPage = true, input = "awo", hasMenu = true))
        assertEquals("上一页", composingShiftSwipeUpBubble(hasPrevPage = true, input = "qwe", schemaId = "sbxm"))
        assertEquals("上一页", composingShiftSwipeUpBubble(hasPrevPage = true, input = "qwrt", schemaId = "sbft"))
    }

    @Test
    fun `一码是造词 lua先吃`() {
        assertEquals("造词", composingShiftSwipeUpBubble(hasPrevPage = false, input = "q"))
        assertEquals("造词", composingShiftSwipeUpBubble(hasPrevPage = false, input = "q", hasMenu = true))
        assertEquals("造词", composingShiftSwipeUpBubble(hasPrevPage = true, input = "q"))
    }

    @Test
    fun `象码三码是纯单`() {
        assertEquals("纯单", composingShiftSwipeUpBubble(hasPrevPage = false, input = "qwe", schemaId = "sbxm"))
    }

    @Test
    fun `飞天四码是组合`() {
        assertEquals(
            "组合",
            composingShiftSwipeUpBubble(hasPrevPage = false, input = "qwrt", schemaId = "sbft"),
        )
        assertEquals(
            "组合",
            composingShiftSwipeUpBubble(hasPrevPage = false, input = "qwrx", schemaId = "sbmf"),
        )
    }

    @Test
    fun `拼音简拼三声母是组合`() {
        assertEquals(
            "组合",
            composingShiftSwipeUpBubble(hasPrevPage = false, input = "qwr", schemaId = "sbpy"),
        )
        assertEquals(
            "组合",
            composingShiftSwipeUpBubble(hasPrevPage = false, input = "qwr", schemaId = "sbjp"),
        )
    }

    @Test
    fun `有菜单未翻页是跳尾`() {
        assertEquals("跳尾", composingShiftSwipeUpBubble(hasPrevPage = false, input = "awo", hasMenu = true))
        assertEquals("跳尾", composingShiftSwipeUpBubble(hasPrevPage = false, input = "aei", hasMenu = true))
    }

    @Test
    fun `对不上就没文案`() {
        assertEquals("", composingShiftSwipeUpBubble(hasPrevPage = false, input = "qw"))
        assertEquals("", composingShiftSwipeUpBubble(hasPrevPage = false, input = "qwer", schemaId = "sbfm"))
        assertEquals("", composingShiftSwipeUpBubble(hasPrevPage = false, input = ""))
        assertEquals(
            "",
            composingShiftSwipeUpBubble(hasPrevPage = false, input = "qwea", schemaId = "sbft"),
        )
    }
}
