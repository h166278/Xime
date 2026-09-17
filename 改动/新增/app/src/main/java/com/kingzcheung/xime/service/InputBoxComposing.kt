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
