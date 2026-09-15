package com.kingzcheung.xime.service

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class CommitStackTest {

    @Test
    fun `有选区不撤销`() {
        val top = CommitEntry("你好", "nih")
        val plan = planCommitUndo(top, "你好", hasSelection = true, composing = false)
        assertEquals(CommitUndoAction.NOOP, plan.action)
    }

    @Test
    fun `栈空不撤销`() {
        val plan = planCommitUndo(null, "你好", hasSelection = false, composing = false)
        assertEquals(CommitUndoAction.NOOP, plan.action)
    }

    @Test
    fun `光标前对不上丢掉这条`() {
        val top = CommitEntry("你好", "nih")
        val plan = planCommitUndo(top, "他好", hasSelection = false, composing = false)
        assertEquals(CommitUndoAction.DROP, plan.action)
    }

    @Test
    fun `空闲有编码删字并捞码`() {
        val top = CommitEntry("你好", "nih")
        val plan = planCommitUndo(top, "他说你好", hasSelection = false, composing = false)
        assertEquals(CommitUndoAction.DELETE_RESTORE_CODE, plan.action)
    }

    @Test
    fun `组合态只删字不改当前码`() {
        val top = CommitEntry("你好", "nih")
        val plan = planCommitUndo(top, "你好", hasSelection = false, composing = true)
        assertEquals(CommitUndoAction.DELETE, plan.action)
    }

    @Test
    fun `没编码只删字`() {
        val top = CommitEntry("·")
        val plan = planCommitUndo(top, "·", hasSelection = false, composing = false)
        assertEquals(CommitUndoAction.DELETE, plan.action)
    }

    @Test
    fun `有选区或打码不重做`() {
        val top = CommitEntry("你好", "nih")
        assertEquals(
            CommitRedoAction.NOOP,
            planCommitRedo(top, hasSelection = true, composing = false).action,
        )
        assertEquals(
            CommitRedoAction.NOOP,
            planCommitRedo(top, hasSelection = false, composing = true).action,
        )
    }

    @Test
    fun `空闲重做提交栈顶`() {
        val top = CommitEntry("你好", "nih")
        assertEquals(
            CommitRedoAction.COMMIT,
            planCommitRedo(top, hasSelection = false, composing = false).action,
        )
    }

    @Test
    fun `记录后能撤销再重做`() {
        val stack = CommitStack()
        stack.record("你", "n")
        stack.record("好", "h")
        assertEquals("好", stack.peekUndo()?.text)
        val undone = stack.popUndoToRedo()
        assertEquals("好", undone?.text)
        assertEquals("你", stack.peekUndo()?.text)
        assertEquals("好", stack.peekRedo()?.text)
        val redone = stack.popRedoToUndo()
        assertEquals("好", redone?.text)
        assertEquals("好", stack.peekUndo()?.text)
        assertNull(stack.peekRedo())
    }

    @Test
    fun `新上屏清掉重做栈`() {
        val stack = CommitStack()
        stack.record("你")
        stack.popUndoToRedo()
        stack.record("好")
        assertNull(stack.peekRedo())
        assertEquals("好", stack.peekUndo()?.text)
    }

    @Test
    fun `顶屏记处理前的码`() {
        assertEquals("nih", commitCodeForProcessResult("nih", "s"))
        assertEquals("nih", commitCodeForProcessResult("nih", ""))
        assertEquals("", commitCodeForProcessResult("nih", "nih"))
        assertEquals("", commitCodeForProcessResult("", "s"))
    }

    @Test
    fun `对不上只丢栈顶不动重做`() {
        val stack = CommitStack()
        stack.record("你")
        stack.record("好")
        stack.dropUndo()
        assertEquals("你", stack.peekUndo()?.text)
        assertNull(stack.peekRedo())
    }

    @Test
    fun `输入框 composing 后缀剥掉后能对上`() {
        assertEquals("你好", committedTextBeforeCursor("你好w", "w"))
        assertEquals("你好", committedTextBeforeCursor("你好ni'h", "ni'h"))
        assertEquals("你好", committedTextBeforeCursor("你好", ""))
        val top = CommitEntry("你好", "nih")
        val plan = planCommitUndo(
            top, "你好w", hasSelection = false, composing = true, composingSuffix = "w",
        )
        assertEquals(CommitUndoAction.DELETE, plan.action)
    }

    @Test
    fun `注入栏不撤销不重做`() {
        val top = CommitEntry("你好", "nih")
        assertEquals(
            CommitUndoAction.NOOP,
            planCommitUndo(top, "你好", hasSelection = false, composing = false, injected = true).action,
        )
        assertEquals(
            CommitRedoAction.NOOP,
            planCommitRedo(top, hasSelection = false, composing = false, injected = true).action,
        )
    }

    @Test
    fun `英文替换把逐字合成一条`() {
        val stack = CommitStack()
        stack.record("h")
        stack.record("e")
        stack.record("l")
        stack.record("l")
        stack.record("o")
        stack.replaceTail("hello", "world")
        assertEquals("world", stack.peekUndo()?.text)
        stack.popUndoToRedo()
        assertNull(stack.peekUndo())
    }
}
