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
 *
 * 只认输入框预览还在。顶功已经 commit，引擎里是下一码，不能当截胡。
 * remaining>0 是自己刚 commit 的迟到 -1，哪怕新预览已经写上也不截胡。
 * QQ 先 finish 再清空走 restartInput 空框，不走这条。
 */
internal fun previewStolenByHost(
    commitPreview: Boolean,
    previewComposingActive: Boolean,
    composingStart: Int,
    composingEnd: Int,
    remainingImeCommitLoss: Int = 0,
): Boolean {
    if (!commitPreview || !previewComposingActive || remainingImeCommitLoss > 0) return false
    return composingStart < 0 && composingEnd < 0
}

/**
 * IME 自己 commitText 之后宿主会连发 composing span=-1。
 * 不是截胡：顶功已经把预览交出去，引擎里是下一码。
 * 只吞有限几次，不能挡到正 span——正 span 一清，迟到的 -1 又会进来。
 *
 * 顶功会立刻写下一码预览。迟到的 -1 来时输入框已经有新 composing，
 * 仍要吞：那是上一笔 commit 的回执，不是宿主发送。
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
 * 截胡已经清引擎。过期的 updateUI / CONFLATED 刷新还带着旧码，
 * 不能再 setComposingText 贴回去。
 *
 * 守卫是独立标志，不靠 abandonedInput 非空。T9 半提交截胡时码可能是空串。
 * 空刷新、同一串码丢掉这次写回。新码才放行。
 */
internal fun shouldDropStalePreviewWrite(
    previewAbandoned: Boolean,
    abandonedInput: String,
    incomingInput: String,
): Boolean {
    if (!previewAbandoned) return false
    if (incomingInput.isEmpty()) return true
    if (abandonedInput.isEmpty()) return false
    return incomingInput == abandonedInput
}

/** 用户打下一条跟放弃码不同的新码，才拆截胡守卫。 */
internal fun shouldClearAbandonedPreview(
    previewAbandoned: Boolean,
    abandonedInput: String,
    incomingInput: String,
): Boolean {
    if (!previewAbandoned) return true
    if (incomingInput.isEmpty()) return false
    if (abandonedInput.isEmpty()) return true
    return incomingInput != abandonedInput
}

/**
 * 探测输入框是不是空的。before/after 都拿不到（探针失败）不当空，
 * 免得误把还在打的预览当成发送截胡。extracted 只在前后都拿不到时才用。
 */
internal fun editorLooksEmpty(
    before: String?,
    after: String?,
    extracted: String? = null,
): Boolean {
    if (before != null || after != null) {
        return before.isNullOrEmpty() && after.isNullOrEmpty()
    }
    if (extracted != null) return extracted.isEmpty()
    return false
}

/**
 * QQ 发送后常 restartInput：框已经空了，键盘还开着，引擎里还有码。
 * 这时再 updateUI 会把预览写回空框。
 */
internal fun shouldAbandonPreviewOnRestart(
    commitPreview: Boolean,
    restarting: Boolean,
    engineComposing: Boolean,
    editorEmpty: Boolean,
): Boolean {
    return commitPreview && restarting && engineComposing && editorEmpty
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
