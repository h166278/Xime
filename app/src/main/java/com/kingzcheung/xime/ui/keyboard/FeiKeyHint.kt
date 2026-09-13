package com.kingzcheung.xime.ui.keyboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * 46 键飞系助记：字母左上、助记字右上、字根按图分行铺满键帽。
 * E/U/I/O/A 只有居中大字根。
 */
data class FeiKeyHint(
    val mnemonic: String = "",
    val largeStroke: String? = null,
    val rows: List<String> = emptyList(),
) {
    /** 下滑气泡：助记字 + 大字根 + 各行字根，无空格。 */
    val bubbleLabel: String
        get() = buildString {
            append(mnemonic)
            largeStroke?.let { append(it) }
            rows.forEach { append(it) }
        }

    companion object {
        fun forKey(key: String): FeiKeyHint? = TABLE[key.lowercase()]

        val TABLE: Map<String, FeiKeyHint> = mapOf(
            "q" to FeiKeyHint("去", rows = listOf("气欠犬", "犭青其", "攴")),
            "w" to FeiKeyHint("我", rows = listOf("韦文瓦", "攵夂夊", "王亠韋")),
            "e" to FeiKeyHint(largeStroke = "一"),
            "r" to FeiKeyHint("人", rows = listOf("人亻")),
            "t" to FeiKeyHint("他", rows = listOf("田土士")),
            "y" to FeiKeyHint("一", rows = listOf("又用业", "页頁衣", "羊言讠", "音酉尢", "疋")),
            "u" to FeiKeyHint(largeStroke = "丿"),
            "i" to FeiKeyHint(largeStroke = "丨"),
            "o" to FeiKeyHint(largeStroke = "丶"),
            "p" to FeiKeyHint("平", rows = listOf("片皮", "⺮丿彡")),
            "a" to FeiKeyHint(largeStroke = "フ"),
            "s" to FeiKeyHint("是", rows = listOf("十山尸手", "水石矢舌", "身鼠示豕", "食饣飠殳", "丨厶")),
            "d" to FeiKeyHint("的", rows = listOf("刀大歹", "斗鬥豆", "丶冫氵", "癶")),
            "f" to FeiKeyHint("发", rows = listOf("方风風", "父缶扌")),
            "g" to FeiKeyHint("个", rows = listOf("工弓广", "戈瓜革", "骨鬼艮", "冖宀")),
            "h" to FeiKeyHint("和", rows = listOf("一户火", "禾黑虍")),
            "j" to FeiKeyHint("就", rows = listOf("几己巾斤", "见見臼角", "金钅釒纟")),
            "k" to FeiKeyHint("可", rows = listOf("口囗匚", "凵冂")),
            "l" to FeiKeyHint("了", rows = listOf("力立龙龍", "里鹿耒刂", "忄廴辶灬", "卤鹵")),
            "z" to FeiKeyHint("在", rows = listOf("子舟自走", "豸隹足⻊", "爪爫丬", "爿長镸罒巛", "乙")),
            "x" to FeiKeyHint("下", rows = listOf("夕小心", "穴血覀", "辛彐糸", "⺍⺌")),
            "c" to FeiKeyHint("出", rows = listOf("厂寸车車", "虫赤辰彳", "齿齒艹卝", "屮")),
            "v" to FeiKeyHint("而", rows = listOf("二儿耳聿", "羽鱼魚雨", "日曰月", "阝卩")),
            "b" to FeiKeyHint("不", rows = listOf("八比贝貝", "白鼻卜髟", "勹疒丷")),
            "n" to FeiKeyHint("你", rows = listOf("女⺧牛", "鸟鳥廾", "礻衤止")),
            "m" to FeiKeyHint("没", rows = listOf("马馬门門", "毛木皿目", "米麻麦麥", "母毋毌")),
        )
    }
}

/** 46 键助记开才画字根键帽。 */
internal fun letterMnemonicHint(key: String, is46Layout: Boolean, mnemonicEnabled: Boolean): FeiKeyHint? {
    if (!is46Layout || !mnemonicEnabled) return null
    return FeiKeyHint.forKey(key)
}

/** 46 键字母下滑气泡一律飞系字根；其它键沿用 yaml。 */
internal fun letterSwipeDownLabel(key: String, is46Layout: Boolean, yamlLabel: String?): String? {
    if (is46Layout) {
        FeiKeyHint.forKey(key)?.bubbleLabel?.takeIf { it.isNotEmpty() }?.let { return it }
    }
    return yamlLabel
}

/** 字根网格：列数取最宽行，格子铺满剩余区域，字号取格子较短边。 */
internal data class MnemonicGridMetrics(
    val colCount: Int,
    val rowCount: Int,
    val cellWidthDp: Float,
    val cellHeightDp: Float,
    val glyphSp: Float,
)

internal fun mnemonicGridMetrics(
    gridWidthDp: Float,
    gridHeightDp: Float,
    rows: List<String>,
): MnemonicGridMetrics {
    val colCount = rows.maxOfOrNull { it.length }?.coerceAtLeast(1) ?: 1
    val rowCount = rows.size.coerceAtLeast(1)
    val width = gridWidthDp.coerceAtLeast(1f)
    val height = gridHeightDp.coerceAtLeast(1f)
    val cellW = width / colCount
    val cellH = height / rowCount
    val glyphSp = minOf(cellW, cellH) * 0.92f
    return MnemonicGridMetrics(colCount, rowCount, cellW, cellH, glyphSp)
}

private val NoPad = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = false))

private val KeyCapInset = 0.5.dp

/**
 * 46 键助记开：字母/助记叠在键帽上沿两角，不挤进字根网格。
 * 字根按最宽行列逐字铺满剩余区域，末行短则居中。单笔大字根居中。
 */
@Composable
fun MnemonicKeyCap(
    letter: String,
    hint: FeiKeyHint,
    textColor: Color,
    fontFamily: FontFamily?,
    labelFontFamily: FontFamily?,
) {
    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val letterSp = (maxHeight.value * 0.18f).coerceIn(8f, 11f)
        val strokeSp = (minOf(maxWidth.value, maxHeight.value) * 0.58f).coerceIn(18f, 36f)
        if (hint.largeStroke != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(KeyCapInset),
            ) {
                Text(
                    text = letter,
                    color = textColor,
                    fontSize = letterSp.sp,
                    fontWeight = FontWeight.Medium,
                    fontFamily = fontFamily,
                    style = NoPad,
                    lineHeight = letterSp.sp,
                    maxLines = 1,
                    modifier = Modifier.align(Alignment.TopStart),
                )
                Text(
                    text = hint.largeStroke,
                    color = textColor,
                    fontSize = strokeSp.sp,
                    fontWeight = FontWeight.Normal,
                    fontFamily = labelFontFamily,
                    style = NoPad,
                    lineHeight = strokeSp.sp,
                    maxLines = 1,
                    modifier = Modifier.align(Alignment.Center),
                )
            }
            return@BoxWithConstraints
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(KeyCapInset),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top,
            ) {
                Text(
                    text = letter,
                    color = textColor,
                    fontSize = letterSp.sp,
                    fontWeight = FontWeight.Medium,
                    fontFamily = fontFamily,
                    style = NoPad,
                    lineHeight = letterSp.sp,
                    maxLines = 1,
                )
                if (hint.mnemonic.isNotEmpty()) {
                    Text(
                        text = hint.mnemonic,
                        color = textColor,
                        fontSize = letterSp.sp,
                        fontWeight = FontWeight.Medium,
                        fontFamily = labelFontFamily,
                        style = NoPad,
                        lineHeight = letterSp.sp,
                        maxLines = 1,
                    )
                }
            }
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
            ) {
                val metrics = mnemonicGridMetrics(
                    gridWidthDp = maxWidth.value,
                    gridHeightDp = maxHeight.value,
                    rows = hint.rows,
                )
                Column(modifier = Modifier.fillMaxSize()) {
                    hint.rows.forEachIndexed { index, row ->
                        val centerLast = index == hint.rows.lastIndex && row.length < metrics.colCount
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            horizontalArrangement = if (centerLast) {
                                Arrangement.Center
                            } else {
                                Arrangement.Start
                            },
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            row.forEach { ch ->
                                Box(
                                    modifier = Modifier
                                        .fillMaxHeight()
                                        .width(metrics.cellWidthDp.dp),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Text(
                                        text = ch.toString(),
                                        color = textColor,
                                        fontSize = metrics.glyphSp.sp,
                                        fontWeight = FontWeight.Normal,
                                        fontFamily = labelFontFamily,
                                        style = NoPad,
                                        lineHeight = metrics.glyphSp.sp,
                                        textAlign = TextAlign.Center,
                                        maxLines = 1,
                                        overflow = TextOverflow.Clip,
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
