package com.kingzcheung.xime.service

import org.junit.Assert.assertEquals
import org.junit.Test

class FullwidthSemicolonTapPlanTest {

    @Test
    fun `中文一码两码进组词三码交词再贴`() {
        assertEquals(
            FullwidthSemicolonTapAction.SEND_SEMICOLON,
            planFullwidthSemicolonTap(chineseMode = true, composing = true, inputLength = 1),
        )
        assertEquals(
            FullwidthSemicolonTapAction.SEND_SEMICOLON,
            planFullwidthSemicolonTap(chineseMode = true, composing = true, inputLength = 2),
        )
        assertEquals(
            FullwidthSemicolonTapAction.SELECT_THEN_COMMIT,
            planFullwidthSemicolonTap(chineseMode = true, composing = true, inputLength = 3),
        )
    }

    @Test
    fun `中文空闲直上屏`() {
        assertEquals(
            FullwidthSemicolonTapAction.COMMIT,
            planFullwidthSemicolonTap(chineseMode = true, composing = false),
        )
    }

    @Test
    fun `英文不进组词`() {
        assertEquals(
            FullwidthSemicolonTapAction.COMMIT,
            planFullwidthSemicolonTap(chineseMode = false, composing = true),
        )
        assertEquals(
            FullwidthSemicolonTapAction.COMMIT,
            planFullwidthSemicolonTap(chineseMode = false, composing = false),
        )
    }

    @Test
    fun `飞系三码交词扩标点进引擎`() {
        assertEquals(
            FullwidthSemicolonTapAction.SELECT_THEN_COMMIT,
            planFullwidthSemicolonTap(true, true, 3, "hui", "sbfd"),
        )
        assertEquals(
            FullwidthSemicolonTapAction.SEND_SEMICOLON,
            planFullwidthSemicolonTap(true, true, 3, "hka", "sbfd"),
        )
        assertEquals(
            FullwidthSemicolonTapAction.SEND_SEMICOLON,
            planFullwidthSemicolonTap(true, true, 4, "huib", "sbfm"),
        )
    }

    @Test
    fun `飞天象码整句三码仍发分号`() {
        assertEquals(
            FullwidthSemicolonTapAction.SEND_SEMICOLON,
            planFullwidthSemicolonTap(true, true, 3, "hui", "sbft"),
        )
        assertEquals(
            FullwidthSemicolonTapAction.SEND_SEMICOLON,
            planFullwidthSemicolonTap(true, true, 3, "hui", "sbxm"),
        )
        assertEquals(
            FullwidthSemicolonTapAction.SEND_SEMICOLON,
            planFullwidthSemicolonTap(true, true, 5, "nihao", "sbpy"),
        )
    }
}
