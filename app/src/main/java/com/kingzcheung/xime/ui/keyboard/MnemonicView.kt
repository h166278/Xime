package com.kingzcheung.xime.ui.keyboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kingzcheung.xime.settings.KeysConfigHelper

/**
 * 助记面板：把当前输入序列每个键的下滑字根 + 上滑符号摊开显示。
 *
 * 数据直接取自 xime.yaml 的 qwerty / qwerty_46 / qwerty_en / qwerty_en_46 段，
 * 与键帽上显示的提示同源，不做二次维护。
 *
 * @param input 当前已输入的编码（可能为空）
 * @param isAsciiMode 是否英文模式（决定读哪一套按键配置）
 * @param embedded true 时用于键盘上方内嵌条形，只显示一行、不滚动导航
 */
@Composable
fun MnemonicView(
    input: String,
    isAsciiMode: Boolean,
    backgroundColor: Color,
    textColor: Color,
    accentColor: Color,
    keyBgColor: Color,
    onBack: (() -> Unit)? = null,
    bottomPaddingDp: Int = 0,
    embedded: Boolean = false,
    modifier: Modifier = Modifier,
) {
    val codes = remember(input) {
        input.filter { it.isLetter() }.map { it.lowercaseChar() }.distinct()
    }
    val rows = remember(codes, isAsciiMode) {
        codes.map { ch ->
            val key = ch.toString()
            MnemonicEntry(
                letter = ch.uppercaseChar().toString(),
                roots = KeysConfigHelper.getSwipeDownLabel(key, isAsciiMode)?.trim().orEmpty(),
                symbols = KeysConfigHelper.getSwipeUpLabel(key, isAsciiMode)?.trim().orEmpty(),
                longPress = KeysConfigHelper.getLongPressLabels(key, isAsciiMode),
            )
        }
    }

    if (embedded) {
        EmbeddedMnemonicRow(
            rows = rows,
            textColor = textColor,
            accentColor = accentColor,
            keyBgColor = keyBgColor,
            modifier = modifier,
        )
        return
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(backgroundColor)
    ) {
        // 导航区
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp)
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (onBack != null) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onBack() },
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                        contentDescription = "返回",
                        tint = textColor,
                    )
                }
            }
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "助记",
                color = textColor,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = if (codes.isEmpty()) "无编码" else codes.joinToString("") { it.toString() },
                color = accentColor,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
            )
            Spacer(modifier = Modifier.width(8.dp))
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 8.dp, bottom = bottomPaddingDp.dp + 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            if (rows.isEmpty()) {
                Text(
                    text = "先输入编码，再上滑 N 键查看助记",
                    color = textColor.copy(alpha = 0.5f),
                    fontSize = 13.sp,
                    modifier = Modifier.padding(12.dp),
                )
            }
            rows.forEach { entry ->
                MnemonicCard(
                    entry = entry,
                    textColor = textColor,
                    accentColor = accentColor,
                    keyBgColor = keyBgColor,
                )
            }
        }
    }
}

private data class MnemonicEntry(
    val letter: String,
    val roots: String,
    val symbols: String,
    val longPress: List<String>,
)

@Composable
private fun MnemonicCard(
    entry: MnemonicEntry,
    textColor: Color,
    accentColor: Color,
    keyBgColor: Color,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(keyBgColor)
            .padding(horizontal = 10.dp, vertical = 8.dp),
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(accentColor.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = entry.letter,
                color = accentColor,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
            )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            if (entry.roots.isNotEmpty()) {
                Text(
                    text = entry.roots,
                    color = textColor,
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                )
            }
            if (entry.symbols.isNotEmpty()) {
                Text(
                    text = "上滑 ${entry.symbols}",
                    color = textColor.copy(alpha = 0.6f),
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 4.dp),
                )
            }
            if (entry.longPress.isNotEmpty()) {
                Text(
                    text = "长按 " + entry.longPress.joinToString(" "),
                    color = textColor.copy(alpha = 0.5f),
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 2.dp),
                )
            }
        }
    }
}

/** 键盘上方内嵌一行：只显示每个编码键的字根，横向排列，不占键盘本体高度。 */
@Composable
private fun EmbeddedMnemonicRow(
    rows: List<MnemonicEntry>,
    textColor: Color,
    accentColor: Color,
    keyBgColor: Color,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.Transparent)
            .padding(horizontal = 6.dp, vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        if (rows.isEmpty()) {
            Text(
                text = "助记：先输入编码",
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                fontSize = 12.sp,
            )
            return@Row
        }
        rows.forEach { entry ->
            Row(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(keyBgColor)
                    .padding(horizontal = 6.dp, vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = entry.letter,
                    color = accentColor,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = entry.roots.ifEmpty { entry.symbols },
                    color = textColor,
                    fontSize = 11.sp,
                    maxLines = 1,
                )
            }
        }
    }
}
