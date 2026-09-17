package com.kingzcheung.xime.service

import com.kingzcheung.xime.settings.SettingsPreferences

/**
 * 输入框 composing 写什么。
 *
 * - `null`：不往输入框写（候选栏模式，或已经空闲该清）
 * - `""`：有码但没有可预览的词，清掉输入框 composing，编码仍留候选栏
 * - 非空：写入 composing，光标钉在末尾
 *
 * 预览上屏跟高亮走，不钉死首选。
 */
internal fun planInputBoxComposing(
    location: String,
    codeDisplay: String,
    candidates: List<String>,
    highlightIndex: Int,
    isComposing: Boolean,
): String? {
    if (!isComposing) return null
    return when (location) {
        SettingsPreferences.INPUT_TEXT_INPUT_BOX ->
            codeDisplay.takeIf { it.isNotEmpty() } ?: ""
        SettingsPreferences.INPUT_TEXT_COMMIT_PREVIEW -> {
            if (candidates.isEmpty()) ""
            else candidates[highlightIndex.coerceIn(0, candidates.lastIndex)]
        }
        else -> null
    }
}

internal fun writesComposingToInputBox(location: String): Boolean =
    location == SettingsPreferences.INPUT_TEXT_INPUT_BOX ||
        location == SettingsPreferences.INPUT_TEXT_COMMIT_PREVIEW

internal fun isCommitPreview(location: String): Boolean =
    location == SettingsPreferences.INPUT_TEXT_COMMIT_PREVIEW

/**
 * 宿主把 composing 拿走了（QQ 发送键常见：读走预览词，span 变成 -1），
 * 键盘还开着。这时不能再 setComposingText 写回去，也不能 finish 把字钉死在输入框。
 */
internal fun previewStolenByHost(
    commitPreview: Boolean,
    previewComposingActive: Boolean,
    composingStart: Int,
    composingEnd: Int,
): Boolean {
    if (!commitPreview || !previewComposingActive) return false
    return composingStart < 0 && composingEnd < 0
}

/**
 * IME 自己 commitText 之后宿主会连发 composing span=-1。
 * 不是截胡：顶功已经把预览交出去，引擎里是下一码。
 * 只吞有限几次，不能挡到正 span——正 span 一清，迟到的 -1 又会进来。
 */
internal const val IME_COMMIT_COMPOSING_LOSS_SWALLOW = 3

internal fun shouldSwallowImeCommitComposingLoss(
    commitPreview: Boolean,
    remaining: Int,
    composingStart: Int,
    composingEnd: Int,
): Boolean {
    if (!commitPreview || remaining <= 0) return false
    return composingStart < 0 && composingEnd < 0
}

/**
 * 截胡后要不要删光标前那段预览。
 * IME 刚 commitText 的字会留在光标前，跟预览字面一样，不能当残留删，否则空格像退格。
 */
internal fun shouldDeleteLeftoverPreview(leftover: String, lastImeCommit: String): Boolean {
    if (leftover.isEmpty()) return false
    if (lastImeCommit.isNotEmpty() && leftover == lastImeCommit) return false
    return true
}

/**
 * 截胡已经清引擎。过期的 updateUIWithResult 还带着旧码，不能再 setComposingText 贴回去。
 * 引擎仍报 composing，且码对得上刚放弃的那串，才丢掉这次刷新。
 * applyComposition 同一条。
 */
internal fun shouldDropStalePreviewWrite(
    previewAbandoned: Boolean,
    abandonedInput: String,
    incomingInput: String,
    incomingComposing: Boolean,
): Boolean {
    if (!previewAbandoned || abandonedInput.isEmpty()) return false
    return incomingComposing && incomingInput == abandonedInput
}

/**
 * 预览上屏才把 T9 半提交拼到高亮词前面。
 * 编码在输入框时 displayText 已经含半提交，不能再拼。
 * 半提交展示态候选就是末段，[t9Prefix] 已经以它结尾，不要再加一遍。
 */
internal fun composingTextWithT9Prefix(planned: String?, t9Prefix: String): String? {
    if (planned == null) return null
    return when {
        t9Prefix.isEmpty() -> planned
        planned.isEmpty() -> t9Prefix
        t9Prefix.endsWith(planned) -> t9Prefix
        else -> t9Prefix + planned
    }
}

internal fun inputBoxComposingText(
    location: String,
    codeDisplay: String,
    candidates: List<String>,
    highlightIndex: Int,
    isComposing: Boolean,
    t9Prefix: String,
): String {
    val planned = planInputBoxComposing(
        location, codeDisplay, candidates, highlightIndex, isComposing,
    )
    val merged = if (isCommitPreview(location)) {
        composingTextWithT9Prefix(planned, t9Prefix)
    } else {
        planned
    }
    return merged.orEmpty()
}
