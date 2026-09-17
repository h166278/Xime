package com.kingzcheung.xime.service

import org.junit.Assert.assertEquals
import org.junit.Test

class IdleDeletePlanTest {

    @Test
    fun `造词缓冲开着只关缓冲不回删`() {
        assertEquals(
            IdleDeleteAction.CLEAR_WORD_BUFFER,
            planIdleDelete(isBuffered = true, tempBuffered = false),
        )
        assertEquals(
            IdleDeleteAction.CLEAR_WORD_BUFFER,
            planIdleDelete(isBuffered = false, tempBuffered = true),
        )
        assertEquals(
            IdleDeleteAction.CLEAR_WORD_BUFFER,
            planIdleDelete(isBuffered = true, tempBuffered = true),
        )
    }

    @Test
    fun `缓冲已关才回删已上屏字`() {
        assertEquals(
            IdleDeleteAction.DELETE_SCREEN,
            planIdleDelete(isBuffered = false, tempBuffered = false),
        )
    }
}
