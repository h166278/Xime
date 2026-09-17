package com.kingzcheung.xime.ui.keyboard

import androidx.compose.ui.unit.dp
import org.junit.Assert.assertEquals
import org.junit.Test

class Landscape46SemicolonRowTest {
    private val tolerance = 0.0001f

    @Test
    fun `字母宽等于面板扣10dp后五等分`() {
        val m = landscape46SemicolonRowMetrics(panelWidth = 360.dp)
        assertEquals(70f, m.letterWidth.value, tolerance)
    }

    @Test
    fun `分号宽等于底行标点 unit乘0点8`() {
        val m = landscape46SemicolonRowMetrics(panelWidth = 360.dp)
        val unit = (360f - 4f * 3) / 5f
        assertEquals(unit * 0.8f, m.semicolonWidth.value, tolerance)
    }

    @Test
    fun `行宽是五字母加分号字母宽距不变`() {
        val m = landscape46SemicolonRowMetrics(panelWidth = 360.dp)
        assertEquals(m.letterWidth.value * 5 + m.semicolonWidth.value, m.rowWidth.value, tolerance)
        assertEquals(70f * 5 + m.semicolonWidth.value, m.rowWidth.value, tolerance)
    }
}
