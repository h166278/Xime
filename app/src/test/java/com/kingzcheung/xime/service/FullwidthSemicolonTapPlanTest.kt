package com.kingzcheung.xime.service

import org.junit.Assert.assertEquals
import org.junit.Test

class FullwidthSemicolonTapPlanTest {

    @Test
    fun `中文有编码进组词`() {
        assertEquals(
            FullwidthSemicolonTapAction.SEND_SEMICOLON,
            planFullwidthSemicolonTap(chineseMode = true, composing = true),
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
}
