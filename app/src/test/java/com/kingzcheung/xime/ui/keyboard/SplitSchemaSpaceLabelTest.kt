package com.kingzcheung.xime.ui.keyboard

import org.junit.Assert.assertEquals
import org.junit.Test

class SplitSchemaSpaceLabelTest {
    @Test
    fun `声笔飞单拆成左右`() {
        assertEquals("声笔" to "飞单", splitSchemaSpaceLabel("声笔飞单"))
    }

    @Test
    fun `声笔开头其余给右边`() {
        assertEquals("声笔" to "拼音", splitSchemaSpaceLabel("声笔拼音"))
        assertEquals("声笔" to "简拼", splitSchemaSpaceLabel("声笔简拼"))
    }

    @Test
    fun `四字对半切`() {
        assertEquals("仓颉" to "输入", splitSchemaSpaceLabel("仓颉输入"))
    }

    @Test
    fun `对不上整串留左`() {
        assertEquals("仓颉" to "", splitSchemaSpaceLabel("仓颉"))
        assertEquals("" to "", splitSchemaSpaceLabel(""))
        assertEquals("" to "", splitSchemaSpaceLabel("  "))
    }
}
