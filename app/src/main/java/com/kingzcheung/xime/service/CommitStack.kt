package com.kingzcheung.xime.service

/**
 * 本键盘上屏记录。只认 IME 送出去的字，跟编辑器 Ctrl+Z 无关。
 * [code] 非空表示这笔来自 Rime 组合（选词/顶屏），撤销时空闲可 setInput 捞回编码。
 */
internal data class CommitEntry(
    val text: String,
    val code: String = "",
)

internal enum class CommitUndoAction {
    NOOP,
    /** 光标前对不上栈顶：丢掉这条，不碰编辑器。 */
    DROP,
    /** 删光标前匹配的字，不改当前编码。 */
    DELETE,
    /** 删字并把这笔的编码 setInput 回来。 */
    DELETE_RESTORE_CODE,
}

internal data class CommitUndoPlan(
    val action: CommitUndoAction,
)

internal enum class CommitRedoAction {
    NOOP,
    COMMIT,
    /** 当前码就是刚捞回来的那串：清码再贴。 */
    CLEAR_THEN_COMMIT,
}

internal data class CommitRedoPlan(
    val action: CommitRedoAction,
)

/**
 * 输入框把当前码拼在 getTextBeforeCursor 末尾。对账前先剥掉，否则「你好w」对不上「你好」。
 */
internal fun committedTextBeforeCursor(
    textBeforeCursor: String?,
    composingSuffix: String,
): String? {
    if (textBeforeCursor == null) return null
    if (composingSuffix.isEmpty()) return textBeforeCursor
    return if (textBeforeCursor.endsWith(composingSuffix)) {
        textBeforeCursor.dropLast(composingSuffix.length)
    } else {
        textBeforeCursor
    }
}

/**
 * 有选区 / 注入符号栏不碰。栈顶空或对不上（已剥 composing）→ DROP，调用方再看下一条。
 * 有编码且当前空闲才捞码；组合态只删已上屏字，不动当前码。
 */
internal fun planCommitUndo(
    top: CommitEntry?,
    textBeforeCursor: String?,
    hasSelection: Boolean,
    composing: Boolean,
    composingSuffix: String = "",
    injected: Boolean = false,
): CommitUndoPlan {
    if (hasSelection || injected) return CommitUndoPlan(CommitUndoAction.NOOP)
    if (top == null || top.text.isEmpty()) return CommitUndoPlan(CommitUndoAction.NOOP)
    val committed = committedTextBeforeCursor(textBeforeCursor, composingSuffix)
    if (committed == null || !committed.endsWith(top.text)) {
        return CommitUndoPlan(CommitUndoAction.DROP)
    }
    return if (!composing && top.code.isNotEmpty()) {
        CommitUndoPlan(CommitUndoAction.DELETE_RESTORE_CODE)
    } else {
        CommitUndoPlan(CommitUndoAction.DELETE)
    }
}

/**
 * 有选区、注入栏不重做。
 * 打码时只有当前码正好是刚撤销捞回来的那串才清码再贴，避免盖掉新打的码。
 */
internal fun planCommitRedo(
    top: CommitEntry?,
    hasSelection: Boolean,
    composing: Boolean,
    injected: Boolean = false,
    currentInput: String = "",
): CommitRedoPlan {
    if (hasSelection || injected) return CommitRedoPlan(CommitRedoAction.NOOP)
    if (top == null || top.text.isEmpty()) return CommitRedoPlan(CommitRedoAction.NOOP)
    if (composing) {
        return if (top.code.isNotEmpty() && currentInput == top.code) {
            CommitRedoPlan(CommitRedoAction.CLEAR_THEN_COMMIT)
        } else {
            CommitRedoPlan(CommitRedoAction.NOOP)
        }
    }
    return CommitRedoPlan(CommitRedoAction.COMMIT)
}

/**
 * 处理键前的编码 vs 处理后剩下的码。
 * 顶屏：剩下的不是原来那串，这笔编码就是处理前的组合。
 * 组合没动（剩下的还是原码）：这笔不是选词，不记码。
 */
internal fun commitCodeForProcessResult(codeBefore: String, remainingInput: String): String {
    if (codeBefore.isEmpty()) return ""
    if (remainingInput == codeBefore) return ""
    return codeBefore
}

internal class CommitStack(private val maxSize: Int = 32) {
    private val undo = ArrayDeque<CommitEntry>()
    private val redo = ArrayDeque<CommitEntry>()

    fun record(text: String, code: String = "") {
        if (text.isEmpty()) return
        while (undo.size >= maxSize) undo.removeFirst()
        undo.addLast(CommitEntry(text, code))
        redo.clear()
    }

    /**
     * 光标前 [expected] 被整段换成 [replacement]（英文联想替换、计算器出结果）。
     * 能用栈顶拼回 expected 就先拿掉那些碎笔，再记成一条，避免撤销对不上逐字。
     */
    fun replaceTail(expected: String, replacement: String, code: String = "") {
        if (expected.isNotEmpty()) {
            var acc = ""
            var n = 0
            for (e in undo.reversed()) {
                if (acc.length + e.text.length > expected.length) {
                    n = 0
                    break
                }
                acc = e.text + acc
                n++
                if (acc == expected) break
            }
            if (n > 0 && acc == expected) {
                repeat(n) { undo.removeLast() }
            }
        }
        record(replacement, code)
    }

    fun peekUndo(): CommitEntry? = undo.lastOrNull()

    fun peekRedo(): CommitEntry? = redo.lastOrNull()

    fun dropUndo() {
        if (undo.isNotEmpty()) undo.removeLast()
    }

    fun popUndoToRedo(): CommitEntry? {
        if (undo.isEmpty()) return null
        val entry = undo.removeLast()
        while (redo.size >= maxSize) redo.removeFirst()
        redo.addLast(entry)
        return entry
    }

    fun popRedoToUndo(): CommitEntry? {
        if (redo.isEmpty()) return null
        val entry = redo.removeLast()
        while (undo.size >= maxSize) undo.removeFirst()
        undo.addLast(entry)
        return entry
    }

    fun clear() {
        undo.clear()
        redo.clear()
    }
}
