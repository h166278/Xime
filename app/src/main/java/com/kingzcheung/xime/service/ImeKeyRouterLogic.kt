package com.kingzcheung.xime.service

import com.kingzcheung.xime.viewmodel.ShiftMode

internal enum class UndoClearedAction {
    NOOP,
    RESTORE_COMPOSITION,
    PASTE_FIELD,
}

internal data class UndoClearedPlan(
    val action: UndoClearedAction,
    val clearCurrentFirst: Boolean,
    val keepCurrentComposition: Boolean = false,
)

/**
 * 删除键 / 123 下滑共用。组合快照：有编码先丢掉当前码再 setInput。
 * 整框快照：只插入不删已上屏字；有编码时不清码，候选栏/首选/sk 留下。
 */
internal fun planUndoCleared(
    composing: Boolean,
    snapshotText: String,
    snapshotIsComposition: Boolean,
): UndoClearedPlan {
    if (snapshotText.isEmpty()) {
        return UndoClearedPlan(UndoClearedAction.NOOP, clearCurrentFirst = false)
    }
    if (snapshotIsComposition) {
        return UndoClearedPlan(
            UndoClearedAction.RESTORE_COMPOSITION,
            clearCurrentFirst = composing,
        )
    }
    return UndoClearedPlan(
        UndoClearedAction.PASTE_FIELD,
        clearCurrentFirst = false,
        keepCurrentComposition = composing,
    )
}

internal enum class IdleDeleteAction {
    CLEAR_WORD_BUFFER,
    DELETE_SCREEN,
}

internal enum class FullwidthSemicolonTapAction {
    SEND_SEMICOLON,
    COMMIT,
    SELECT_THEN_COMMIT,
}

/**
 * 中文一码：`;` 进标点字。两码仍进组词（ss+;）。
 * 三码以上先交高亮再贴 `；`（hui; → 遑；）。空闲/英文直上屏。
 * [input]/[schemaId] 命中进码例外时三码以上仍发 `;`（反查、扩标点、整句组合）。
 */
internal fun planFullwidthSemicolonTap(
    chineseMode: Boolean,
    composing: Boolean,
    inputLength: Int = 0,
    input: String = "",
    schemaId: String = "",
): FullwidthSemicolonTapAction = when {
    !chineseMode || !composing -> FullwidthSemicolonTapAction.COMMIT
    inputLength >= 3 &&
        !layout46PunctStaysInEngine(input, schemaId, ";") ->
        FullwidthSemicolonTapAction.SELECT_THEN_COMMIT
    else -> FullwidthSemicolonTapAction.SEND_SEMICOLON
}

/**
 * 空码退格：造词缓冲还开着就只关缓冲，不回删已上屏字。
 * 缓冲跟编码不是一回事，删空码后键盘看起来空闲，option 可能还挂着。
 */
internal fun planIdleDelete(
    isBuffered: Boolean,
    tempBuffered: Boolean,
): IdleDeleteAction =
    if (isBuffered || tempBuffered) IdleDeleteAction.CLEAR_WORD_BUFFER
    else IdleDeleteAction.DELETE_SCREEN

internal const val RIME_UPPER_PREFIX = "rime_upper:"
internal const val RIME_PUNCT_PREFIX = "rime_punct:"
internal const val LAYOUT46_SYMBOL_PREFIX = "layout46_symbol:"
internal const val LAYOUT46_SYMBOL_SWIPE_PREFIX = "layout46_swipe:"
internal const val LAYOUT46_OVERLAY_PREFIX = "layout46_overlay:"
internal const val LAYOUT46_OVERLAY_SWIPE_PREFIX = "layout46_overlay_swipe:"

internal enum class Layout46SymbolTapAction {
    PROCESS,
    COMMIT_IDLE,
    COMMIT_TAP,
    SELECT_THEN_COMMIT,
}

internal fun parseLayout46SymbolKey(key: String): String? =
    if (key.startsWith(LAYOUT46_SYMBOL_PREFIX)) key.removePrefix(LAYOUT46_SYMBOL_PREFIX) else null

internal fun parseLayout46SymbolSwipeKey(key: String): String? =
    if (key.startsWith(LAYOUT46_SYMBOL_SWIPE_PREFIX)) key.removePrefix(LAYOUT46_SYMBOL_SWIPE_PREFIX) else null

internal fun parseLayout46OverlayKey(key: String): String? =
    if (key.startsWith(LAYOUT46_OVERLAY_PREFIX) && !key.startsWith(LAYOUT46_OVERLAY_SWIPE_PREFIX)) {
        key.removePrefix(LAYOUT46_OVERLAY_PREFIX)
    } else null

internal fun parseLayout46OverlaySwipeKey(key: String): String? =
    if (key.startsWith(LAYOUT46_OVERLAY_SWIPE_PREFIX)) key.removePrefix(LAYOUT46_OVERLAY_SWIPE_PREFIX) else null

internal enum class Layout46OverlayTapAction {
    PROCESS,
    COMMIT_OVERLAY,
}

/** 引擎有码当普通 46 标点；空闲才贴覆盖脸英文符。 */
internal fun planLayout46OverlayTap(rimeHasInput: Boolean): Layout46OverlayTapAction =
    if (rimeHasInput) Layout46OverlayTapAction.PROCESS
    else Layout46OverlayTapAction.COMMIT_OVERLAY

internal enum class Layout46SymbolSwipeAction {
    SELECT_THEN_COMMIT,
    COMMIT,
}

/** 引擎有码才先选词；英文 pending 字母已在屏上，只贴标点。 */
internal fun planLayout46SymbolSwipe(rimeHasInput: Boolean): Layout46SymbolSwipeAction =
    if (rimeHasInput) Layout46SymbolSwipeAction.SELECT_THEN_COMMIT
    else Layout46SymbolSwipeAction.COMMIT

/** 有码用 swipe_up（quote46 ‘’）；空闲有 idle_swipe_up 用它（也是 ‘’）。都空返回 null。 */
internal fun layout46SymbolSwipeText(
    rimeHasInput: Boolean,
    swipe: String?,
    idleSwipe: String?,
): String? =
    if (rimeHasInput || idleSwipe.isNullOrEmpty()) swipe?.takeIf { it.isNotEmpty() }
    else idleSwipe

/** quote46 发 '；其余必须是单字符 ASCII，别把全角 label 当键码。 */
internal fun layout46SymbolAsciiKey(keyId: String, tap: String): Char {
    if (keyId == "quote46") return '\''
    val c = tap.firstOrNull()
    return if (c != null && c.code in 0x21..0x7E) c else (keyId.firstOrNull() ?: '/')
}

/**
 * 中文一码：半角进 Rime 标点字（j/ → 简，j, → 机，j. → 计）。
 * `/ , .` 两码以上交词再贴（jk/ → 叫、；jk, → 叫，；jk. → 叫。）。
 * quote46 / 分号两码仍进组词（sx+'、ss+;），三码以上才贴 “” / ；（hui' → 遑“”；hui; → 遑；）。
 * [input]/[schemaId] 命中进码例外时仍 processKey（反查 `a`/`e`、飞系扩标点、整句分隔）。
 * 空闲有 YAML idle（/ 顿号、引号弯引号）直上屏。
 * 空闲无 idle（逗号句号）仍 processKey，punctuator 出 ，。
 * 英文盘不进 Rime：空闲 / 仍 ､，其余半角直上屏。
 */
internal fun layout46SelectThenCommitMinLength(keyId: String): Int = when (keyId) {
    "quote46", ";" -> 3
    "/", ",", "." -> 2
    else -> 2
}

internal const val SB_INITIALS = "bpmfdtnlgkhjqxzcsrywv"
private val SB_INITIAL_SET = SB_INITIALS.toSet()
private val SB_STROKE_SET = "aeuio".toSet()
private val LAYOUT46_PUNCT_KEYS = setOf("quote46", ";", "/", ",", ".")

private fun isSbInitial(c: Char): Boolean = c in SB_INITIAL_SET
private fun isSbStroke(c: Char): Boolean = c in SB_STROKE_SET

private fun layout46PunctChar(keyId: String): Char? = when (keyId) {
    "quote46" -> '\''
    ";", "/", ",", "." -> keyId[0]
    else -> null
}

private fun schemaFamily(schemaId: String): String = when (schemaId) {
    "sbfm", "sbfd", "sbfy" -> "feixi"
    "sbft", "sbmf" -> "feitian"
    "sbxm" -> "xiangma"
    "sbjm" -> "jianma"
    "sbxh", "sbzr" -> "shuangpin"
    "sbpy", "sbyp", "sbjp", "sbzz", "sbhz" -> "sentence"
    else -> schemaId
}

/**
 * 长度切到交词再贴之后，这些码仍该进引擎。
 * 已做完的一码标点字、两码组词、hui'/hui; 交词贴字面，不走这里。
 */
internal fun layout46PunctStaysInEngine(
    input: String,
    schemaId: String,
    keyId: String,
): Boolean {
    if (keyId !in LAYOUT46_PUNCT_KEYS) return false
    if (input.isEmpty()) return false
    val family = schemaFamily(schemaId)
    val punct = layout46PunctChar(keyId) ?: return false
    val isQuote = punct == '\''
    val isSemicolon = punct == ';'

    // 拼音/emoji 反查：' 进码补笔。,./; 结束反查，交词贴字面。
    if (input.matches(Regex("^a[$SB_INITIALS][a-z']*$")) ||
        input.matches(Regex("^e[$SB_INITIALS][a-z']*$"))
    ) {
        return isQuote
    }
    // 两分 / 自定义 / 纯笔画：只在字词方案里当反查结束。整句 i/u/aoe 本身就是拼音。
    if (family != "sentence") {
        if (input.matches(Regex("^i[$SB_INITIALS][a-z]*$")) ||
            input.matches(Regex("^u[$SB_INITIALS][a-z]*$"))
        ) {
            return false
        }
        if (input.all { it in SB_STROKE_SET }) {
            return false
        }
    }

    when (family) {
        "feixi" -> {
            val n = input.length
            if (n < 3) return false
            val c0 = input[0]
            val c1 = input[1]
            val c2 = input[2]
            if (!isSbInitial(c0)) return false
            // ssb + 五键：扩标点字（hka' / hka,）。
            if (n == 3 && isSbInitial(c1) && isSbStroke(c2)) return true
            // sxs / sss：;' 进码（顶屏 / binder）。,./ 已做完交词贴字面，不动。
            if (n == 3 && isSbInitial(c2) && (isQuote || isSemicolon)) return true
            // 四码以上字母 + 五键：顶大写 / 扩标点。
            if (n >= 4 && input.all { it.isLetter() }) return true
            // 码里已经有标点（j/a、j;'）再加码，继续进 popping。
            if (input.any { it in ";',./" }) return true
            return false
        }
        "feitian", "xiangma" -> {
            // 三码以上五键都是第四码 / 选重。hui 这种 sbb 在飞天也进码，不是交词贴引号。
            return input.length >= 3 && input.all { it.isLetter() || it in "0123456789;',./" }
        }
        "jianma" -> {
            // alphabet 只有 ;'。三码 ' 拆词；; 三码以上仍交词贴 ；。
            return isQuote && input.length >= 3
        }
        "shuangpin" -> {
            // alphabet 只有 ;'。三码以上 ;' 进码，,./ 仍交词贴字面。
            return (isQuote || isSemicolon) && input.length >= 3
        }
        "sentence" -> {
            // ' 音节分隔 / 补笔 / binder；; 组合上屏。,./ 顶屏出标点。
            return (isQuote || isSemicolon) && input.length >= 3
        }
        else -> return false
    }
}

internal fun planLayout46SymbolTap(
    engineHasInput: Boolean,
    asciiMode: Boolean,
    idle: String?,
    inputLength: Int = 0,
    keyId: String = "",
    input: String = "",
    schemaId: String = "",
): Layout46SymbolTapAction = when {
    asciiMode && !engineHasInput && !idle.isNullOrEmpty() -> Layout46SymbolTapAction.COMMIT_IDLE
    asciiMode -> Layout46SymbolTapAction.COMMIT_TAP
    engineHasInput &&
        inputLength >= layout46SelectThenCommitMinLength(keyId) &&
        !layout46PunctStaysInEngine(input, schemaId, keyId) ->
        Layout46SymbolTapAction.SELECT_THEN_COMMIT
    engineHasInput -> Layout46SymbolTapAction.PROCESS
    !idle.isNullOrEmpty() -> Layout46SymbolTapAction.COMMIT_IDLE
    else -> Layout46SymbolTapAction.PROCESS
}

/**
 * 两码以上点按贴的字面。/ 跟空闲一样贴顿号（jk/ → 叫、）。
 * quote46 点按用 idle “”；逗号句号跟 punctuator。
 */
internal fun layout46ComposingPunctLiteral(keyId: String, idle: String?): String = when (keyId) {
    "/" -> idle?.takeIf { it.isNotEmpty() } ?: "、"
    "," -> "，"
    "." -> "。"
    ";" -> "；"
    "quote46" -> idle?.takeIf { it.isNotEmpty() } ?: "“”"
    else -> idle?.takeIf { it.isNotEmpty() } ?: keyId
}

internal val MIDDLE_DOT_CANDIDATES = listOf("·", "・", "･")
/** 空格 －；a ——；e —；u -；i ---；o ─。 */
internal val DASH_CANDIDATES = listOf("－", "——", "—", "-", "---", "─")
/** 空格 ＿；a _；e __；u ___；i ____。没有 o。 */
internal val UNDERSCORE_CANDIDATES = listOf("＿", "_", "__", "___", "____")
/** 空格 ＝；a =；e ≠；u ≡；i ≈；o ==。 */
internal val EQUALS_CANDIDATES = listOf("＝", "=", "≠", "≡", "≈", "==")
/** 空格 ……；a …；e ⋯；u ⋮；i ︙；o ‥。前两位不动。 */
internal val ELLIPSIS_CANDIDATES = listOf("……", "…", "⋯", "⋮", "︙", "‥")
/** 空格 ×；a ⨯；e ✖；u Ⅹ；i ₓ；o ⅹ。 */
internal val TIMES_CANDIDATES = listOf("×", "⨯", "✖", "Ⅹ", "ₓ", "ⅹ")
/** 空格 ÷；a ⊘；e ⟌。 */
internal val DIVISION_CANDIDATES = listOf("÷", "⊘", "⟌")
/** 乘号变体注释：首位空（空格上屏），aeuio 两字说明。 */
internal val TIMES_CANDIDATE_COMMENTS = listOf("", "叉积", "粗乘", "罗马", "下标", "小写")
/**
 * YAML 实际会发 `rime_punct:` 的通道才登记。
 * `~ + { } [ ] \ | *` 长按仍是 `process_rime_key` 单字符，进引擎不进注入栏，表里留着是死的。
 */
internal val RIME_PUNCT_CANDIDATES: Map<String, List<String>> = mapOf(
    "`" to MIDDLE_DOT_CANDIDATES,
    "-" to DASH_CANDIDATES,
    "=" to EQUALS_CANDIDATES,
    "_" to UNDERSCORE_CANDIDATES,
    "^" to ELLIPSIS_CANDIDATES,
    "x" to TIMES_CANDIDATES,
    "÷" to DIVISION_CANDIDATES,
)

/** 注入标点候选：空格选首位，aeuio 对齐声笔字母选重。 */
internal fun injectedPunctSelectIndex(key: String): Int? = when (key.lowercase()) {
    " ", "space" -> 0
    "a" -> 1
    "e" -> 2
    "u" -> 3
    "i" -> 4
    "o" -> 5
    else -> null
}

/** 注入栏注释：首位空格不上字，后面 aeuio 对齐选重。 */
internal val INJECTED_PUNCT_SELECT_COMMENTS = listOf("", "a", "e", "u", "i", "o")

internal fun injectedPunctComments(count: Int): List<String> =
    List(count) { i -> INJECTED_PUNCT_SELECT_COMMENTS.getOrElse(i) { "" } }

/** 乘号通道用两字说明；其余仍 aeuio 选重注释。 */
internal fun injectedPunctCommentsFor(key: String, count: Int): List<String> {
    if (!isRimePunctKey(key)) return injectedPunctComments(count)
    val ch = key.substring(RIME_PUNCT_PREFIX.length)
    if (ch == "x") {
        return List(count) { i -> TIMES_CANDIDATE_COMMENTS.getOrElse(i) { "" } }
    }
    return injectedPunctComments(count)
}

/** `rime_punct:` 后跟 ASCII 键或注入表里的非 ASCII 键（如 ÷）。未知键返回 null。 */
internal fun rimePunctCandidates(key: String): List<String>? {
    if (!isRimePunctKey(key)) return null
    return RIME_PUNCT_CANDIDATES[key.substring(RIME_PUNCT_PREFIX.length)]
}

internal fun hasInjectedCandidates(candState: CandidateState): Boolean =
    candState.candidateActions.any { it.isInjectedCandidate }

/** 只认注入表里的通道。未知 `rime_punct:` 不当标点吞掉。 */
internal fun isRimePunctKey(key: String): Boolean {
    if (!key.startsWith(RIME_PUNCT_PREFIX)) return false
    val ch = key.substring(RIME_PUNCT_PREFIX.length)
    return ch.length == 1 && RIME_PUNCT_CANDIDATES.containsKey(ch)
}

/**
 * 解析组合态字母上滑通道。`rime_upper:B` → 0x42。非法串返回 null。
 */
internal fun resolveRimeUpperKeyCode(key: String): Int? {
    if (!key.startsWith(RIME_UPPER_PREFIX)) return null
    val letter = key.substring(RIME_UPPER_PREFIX.length)
    if (letter.length != 1) return null
    val c = letter[0]
    if (c !in 'A'..'Z' && c !in 'a'..'z') return null
    return c.uppercaseChar().code
}

/**
 * 46 键空闲单击 Shift + 字母是否改走声笔 auto_inline（临时英文，首字母大写）。
 *
 * Caps / 26 键 / 组合态 / auto_inline 关：一律 false，保持原 Shift 硬提交或 Tab 路径。
 */
internal fun shouldRouteShiftLetterToInlineAscii(
    layout46: Boolean,
    chineseMode: Boolean,
    isLetter: Boolean,
    isShifted: Boolean,
    composing: Boolean,
    hasInput: Boolean,
    shiftMode: ShiftMode,
    autoInlineEnabled: Boolean,
): Boolean {
    return layout46 &&
        chineseMode &&
        isLetter &&
        isShifted &&
        !composing &&
        !hasInput &&
        shiftMode == ShiftMode.SINGLE &&
        autoInlineEnabled
}
