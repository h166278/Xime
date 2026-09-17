package com.kingzcheung.xime.service

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class UndoClearedPlanTest {

    @Test
    fun `无快照不撤回`() {
        val plan = planUndoCleared(composing = false, snapshotText = "", snapshotIsComposition = false)
        assertEquals(UndoClearedAction.NOOP, plan.action)
        assertFalse(plan.clearCurrentFirst)
    }

    @Test
    fun `空闲还原组合编码`() {
        val plan = planUndoCleared(composing = false, snapshotText = "nihao", snapshotIsComposition = true)
        assertEquals(UndoClearedAction.RESTORE_COMPOSITION, plan.action)
        assertFalse(plan.clearCurrentFirst)
    }

    @Test
    fun `空闲贴回整框文字`() {
        val plan = planUndoCleared(composing = false, snapshotText = "你好", snapshotIsComposition = false)
        assertEquals(UndoClearedAction.PASTE_FIELD, plan.action)
        assertFalse(plan.clearCurrentFirst)
    }

    @Test
    fun `有编码先丢掉当前码再还原组合`() {
        val plan = planUndoCleared(composing = true, snapshotText = "nihao", snapshotIsComposition = true)
        assertEquals(UndoClearedAction.RESTORE_COMPOSITION, plan.action)
        assertTrue(plan.clearCurrentFirst)
    }

    @Test
    fun `有编码贴整框保住当前码和候选`() {
        val plan = planUndoCleared(composing = true, snapshotText = "你好", snapshotIsComposition = false)
        assertEquals(UndoClearedAction.PASTE_FIELD, plan.action)
        assertFalse(plan.clearCurrentFirst)
        assertTrue(plan.keepCurrentComposition)
    }
}
