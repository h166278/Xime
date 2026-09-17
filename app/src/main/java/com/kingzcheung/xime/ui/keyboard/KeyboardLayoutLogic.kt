package com.kingzcheung.xime.ui.keyboard

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.kingzcheung.xime.service.RIME_UPPER_PREFIX

private const val SHENG_MU = "bpmfdtnlgkhjqxzcsrywv"

/** 空格上滑阈值，对齐普通键 50dp。原先 24dp 太矮，组合态轻滑就进造词。 */
internal val SPACE_SWIPE_UP_THRESHOLD_DP = 50.dp

/** 声笔点 A 进拼音反查（prefix a）。单码 a 不当造词。 */
internal fun isReverseLookupInput(input: String): Boolean =
    input.isNotEmpty() && input[0] == 'a'

/**
 * 46 键有编码上滑 Shift 气泡。对齐 sbsrf，对不上就空串（不上气泡）。
 * 1. 码长 1 且非反查 → 造词（lua 先吃，即使已翻页）
 * 2. paging → 上一页
 * 3. 象码三码 → 纯单
 * 4. 飞天四码 sssx → 组合
 * 5. 拼音/简拼三声母 → 组合
 * 6. has_menu 未翻页 → 跳尾
 */
internal fun composingShiftSwipeUpBubble(
    hasPrevPage: Boolean,
    input: String = "",
    schemaId: String = "",
    hasMenu: Boolean = false,
): String {
    if (input.length == 1 && !isReverseLookupInput(input)) return "造词"
    if (hasPrevPage) return "上一页"
    val id = schemaId.lowercase()
    if (id == "sbxm" && input.length == 3) return "纯单"
    if ((id == "sbft" || id == "sbmf") &&
        input.length == 4 &&
        input.take(3).all { it in SHENG_MU } &&
        input[3].isLetter()
    ) {
        return "组合"
    }
    if ((id == "sbpy" || id == "sbjp") &&
        input.length == 3 &&
        input.all { it in SHENG_MU }
    ) {
        return "组合"
    }
    if (hasMenu) return "跳尾"
    return ""
}

internal fun isLetterKey(key: String): Boolean {
    if (key.length != 1) return false
    val c = key[0]
    return c in 'a'..'z' || c in 'A'..'Z'
}

/** 中文组合态字母上滑改发大写进 Rime；空闲/英文仍走符号。 */
internal fun shouldComposingLetterSwipeUp(
    isComposing: Boolean,
    isAsciiMode: Boolean,
    key: String,
): Boolean = isComposing && !isAsciiMode && isLetterKey(key)

/**
 * display=key 也要有 longPressItems，否则 SwipeableKeyButton 不进长按，
 * 空闲松手会当成点按，46 键 / 就变成顿号。
 */
internal fun symbolLongPressItems(
    display: String?,
    keyLabel: String?,
    bubbleLabels: List<String>?,
): List<String>? = when {
    display == "bubble" -> bubbleLabels?.takeIf { it.isNotEmpty() }
    !keyLabel.isNullOrEmpty() -> listOf(keyLabel)
    else -> null
}

internal fun composingLetterSwipeUpKey(key: String): String =
    "$RIME_UPPER_PREFIX${key.uppercase()}"

internal fun composingLetterSwipeUpLabel(key: String): String = key.uppercase()

/** 组合态 Shift 上滑：造词和反查走三档；上一页/组合/跳尾/纯单仍即时触发。 */
internal fun isComposingShiftWordCreate(label: String): Boolean = label == "造词"

internal fun isComposingShiftThreeZone(label: String, input: String): Boolean =
    isComposingShiftWordCreate(label) || isReverseLookupInput(input)

/** 造词滑回点 Shift；反查滑回下一页。 */
internal fun composingShiftThreeZoneTapLabel(input: String): String =
    if (isReverseLookupInput(input)) "下一页" else "点 Shift"

internal enum class ShiftSwipeZone { TAP, ACTION, CANCEL, DOWN }

/** 上滑过动作线 → 再往上取消 → 滑回动作线以下变点 Shift。直接下滑无操作。 */
internal val SHIFT_SWIPE_ACTION_DP = 50.dp
internal val SHIFT_SWIPE_CANCEL_DP = 90.dp

internal fun resolveShiftSwipeZone(
    upPx: Float,
    actionPx: Float,
    cancelPx: Float,
): ShiftSwipeZone = when {
    upPx >= cancelPx -> ShiftSwipeZone.CANCEL
    upPx >= actionPx -> ShiftSwipeZone.ACTION
    upPx <= -actionPx -> ShiftSwipeZone.DOWN
    else -> ShiftSwipeZone.TAP
}

/** 上滑过线再向下越过起点，仍算点 Shift，不当直接下滑。 */
internal fun settleShiftSwipeZone(
    zone: ShiftSwipeZone,
    reachedAction: Boolean,
): ShiftSwipeZone =
    if (zone == ShiftSwipeZone.DOWN && reachedAction) ShiftSwipeZone.TAP else zone

internal fun shiftSwipeZoneBubble(
    zone: ShiftSwipeZone,
    actionLabel: String,
    reachedAction: Boolean,
    tapLabel: String = "点 Shift",
): String? = when (zone) {
    ShiftSwipeZone.CANCEL -> "取消"
    ShiftSwipeZone.ACTION -> actionLabel.takeIf { it.isNotEmpty() }
    ShiftSwipeZone.TAP -> if (reachedAction) tapLabel else null
    ShiftSwipeZone.DOWN -> null
}

/**
 * 46 横屏右半第二行：GHJKL 字母宽跟 YUIOP 一样（面板五等分），
 * G 左缘跟 Y 左缘对齐。分号宽跟底行标点一样 unit*0.8，往右溢。
 * L-分号间距靠键内 2+2dp，跟 K-L 一样。
 */
internal data class Landscape46SemicolonRowMetrics(
    val letterWidth: Dp,
    val semicolonWidth: Dp,
    val rowWidth: Dp,
)

internal fun landscape46SemicolonRowMetrics(
    panelWidth: Dp,
    gap: Dp = 4.dp,
    punctWeight: Float = 0.8f,
): Landscape46SemicolonRowMetrics {
    val letterWidth = panelWidth / 5f
    val unit = (panelWidth - gap * 3) / 5f
    val semicolonWidth = unit * punctWeight
    return Landscape46SemicolonRowMetrics(
        letterWidth = letterWidth,
        semicolonWidth = semicolonWidth,
        rowWidth = letterWidth * 5 + semicolonWidth,
    )
}
