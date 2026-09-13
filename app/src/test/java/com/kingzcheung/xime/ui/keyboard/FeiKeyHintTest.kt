package com.kingzcheung.xime.ui.keyboard

import com.kingzcheung.xime.keyboard.GestureAction
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class FeiKeyHintTest {

    @Test
    fun `26 字母都有飞系字根`() {
        val letters = "qwertyuiopasdfghjklzxcvbnm"
        letters.forEach { ch ->
            assertNotNull("缺 $ch", FeiKeyHint.forKey(ch.toString()))
        }
    }

    @Test
    fun `单笔大字根只有 EUIOA`() {
        assertEquals("一", FeiKeyHint.forKey("e")!!.largeStroke)
        assertEquals("丿", FeiKeyHint.forKey("u")!!.largeStroke)
        assertEquals("丨", FeiKeyHint.forKey("i")!!.largeStroke)
        assertEquals("丶", FeiKeyHint.forKey("o")!!.largeStroke)
        assertEquals("フ", FeiKeyHint.forKey("a")!!.largeStroke)
        assertNull(FeiKeyHint.forKey("q")!!.largeStroke)
        assertEquals("去", FeiKeyHint.forKey("q")!!.mnemonic)
    }

    @Test
    fun `下滑气泡拼助记和大字根`() {
        assertEquals("一", FeiKeyHint.forKey("e")!!.bubbleLabel)
        assertEquals("去气欠犬犭青其攴", FeiKeyHint.forKey("q")!!.bubbleLabel)
        assertEquals("人人亻", FeiKeyHint.forKey("r")!!.bubbleLabel)
        val zBubble = FeiKeyHint.forKey("z")!!.bubbleLabel
        assertEquals("在子舟自走豸隹足⻊爪爫丬爿長镸罒巛乙", zBubble)
        assertTrue(zBubble.contains("镸罒巛"))
        assertTrue(!zBubble.contains("爫罒"))
        assertEquals(zBubble, letterSwipeDownLabel("z", is46Layout = true, ""))
    }

    @Test
    fun `末行短于列数的键会居中`() {
        listOf("q", "d", "g", "k", "l", "x", "c", "v", "b", "m", "z", "s", "y").forEach { key ->
            val hint = FeiKeyHint.forKey(key)!!
            val cols = hint.rows.maxOf { it.length }
            assertTrue("$key 末行应短于列数", hint.rows.last().length < cols)
        }
    }

    @Test
    fun `助记键帽只在 46 键且开关开`() {
        assertNull(letterMnemonicHint("q", is46Layout = false, mnemonicEnabled = true))
        assertNull(letterMnemonicHint("q", is46Layout = true, mnemonicEnabled = false))
        assertEquals("去", letterMnemonicHint("q", is46Layout = true, mnemonicEnabled = true)!!.mnemonic)
        assertNull(letterMnemonicHint(";", is46Layout = true, mnemonicEnabled = true))
    }

    @Test
    fun `46 键字母下滑改飞系 其它键沿用 yaml`() {
        assertEquals("去气欠犬犭青其攴", letterSwipeDownLabel("q", is46Layout = true, "金钅"))
        assertEquals("金钅", letterSwipeDownLabel("q", is46Layout = false, "金钅"))
        assertEquals("：", letterSwipeDownLabel(";", is46Layout = true, "："))
    }

    @Test
    fun `toggle_mnemonic 能从 yaml 解析`() {
        assertEquals(GestureAction.TOGGLE_MNEMONIC, GestureAction.fromValue("toggle_mnemonic"))
    }

    @Test
    fun `密键按图2最宽行取列数`() {
        assertEquals(4, FeiKeyHint.forKey("s")!!.rows.maxOf { it.length })
        assertEquals(5, FeiKeyHint.forKey("s")!!.rows.size)
        assertEquals(5, FeiKeyHint.forKey("z")!!.rows.maxOf { it.length })
        assertEquals(5, FeiKeyHint.forKey("z")!!.rows.size)
        assertEquals("爪爫丬", FeiKeyHint.forKey("z")!!.rows[2])
        assertEquals("爿長镸罒巛", FeiKeyHint.forKey("z")!!.rows[3])
        assertEquals("乙", FeiKeyHint.forKey("z")!!.rows[4])
        assertEquals("丨厶", FeiKeyHint.forKey("s")!!.rows.last())
        assertEquals("疋", FeiKeyHint.forKey("y")!!.rows.last())
        assertTrue(FeiKeyHint.forKey("z")!!.rows[3].contains("镸罒巛"))
        assertEquals(3, FeiKeyHint.forKey("y")!!.rows.maxOf { it.length })
        assertEquals(5, FeiKeyHint.forKey("y")!!.rows.size)
        assertEquals(4, FeiKeyHint.forKey("m")!!.rows.maxOf { it.length })
        assertEquals(3, FeiKeyHint.forKey("q")!!.rows.maxOf { it.length })
        assertEquals(1, FeiKeyHint.forKey("q")!!.rows.last().length)
    }

    @Test
    fun `字根格子按宽高较短边取字号 末行不撑满列`() {
        val s = mnemonicGridMetrics(32f, 40f, FeiKeyHint.forKey("s")!!.rows)
        assertEquals(4, s.colCount)
        assertEquals(5, s.rowCount)
        assertEquals(8f, s.cellWidthDp, 0.01f)
        assertEquals(8f, s.cellHeightDp, 0.01f)
        assertEquals(8f * 0.92f, s.glyphSp, 0.01f)

        val q = mnemonicGridMetrics(30f, 36f, FeiKeyHint.forKey("q")!!.rows)
        assertEquals(3, q.colCount)
        assertEquals(3, q.rowCount)
        assertEquals(10f, q.cellWidthDp, 0.01f)
        assertEquals(12f, q.cellHeightDp, 0.01f)
        assertEquals(10f * 0.92f, q.glyphSp, 0.01f)

        val z = mnemonicGridMetrics(30f, 40f, FeiKeyHint.forKey("z")!!.rows)
        assertEquals(5, z.colCount)
        assertEquals(5, z.rowCount)
        assertEquals(6f, z.cellWidthDp, 0.01f)
        assertEquals(8f, z.cellHeightDp, 0.01f)
        assertTrue(FeiKeyHint.forKey("z")!!.rows[4].length < z.colCount)
        assertTrue(FeiKeyHint.forKey("s")!!.rows.last().length < 4)
        assertTrue(FeiKeyHint.forKey("y")!!.rows.last().length < 3)
    }
}
