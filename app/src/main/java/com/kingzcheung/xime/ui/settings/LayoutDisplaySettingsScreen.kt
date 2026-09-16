package com.kingzcheung.xime.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.ui.graphics.luminance
import com.kingzcheung.xime.ui.theme.KeyboardThemes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.window.Dialog
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.twotone.Straighten
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kingzcheung.xime.settings.SettingsPreferences

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LayoutDisplaySettingsContent(
    onBack: () -> Unit
) {
    val context = LocalContext.current

    Scaffold(
        containerColor = MaterialTheme.colorScheme.surface,
        topBar = {
            TopAppBar(
                title = { Text("布局与显示") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "返回"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                SettingsSection(title = "候选词", content = {
                    val candidateTextSizePref = SettingsPreferences.getCandidateTextSize(context)
                    var candidateTextSize by remember(candidateTextSizePref) {
                        mutableStateOf(candidateTextSizePref.toFloat())
                    }

                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "候选字大小",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        CandidateTextSizeCard(
                            candidateTextSize = candidateTextSize,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Slider(
                            value = candidateTextSize,
                            onValueChange = { candidateTextSize = it },
                            onValueChangeFinished = {
                                SettingsPreferences.setCandidateTextSize(context, candidateTextSize.toInt())
                            },
                            valueRange = 12f..22f,
                            steps = 9
                        )
                    }

                    HorizontalDivider(
                        modifier = Modifier.padding(start = 16.dp),
                        thickness = 0.5.dp,
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                    )

                    var showComments by remember {
                        mutableStateOf(SettingsPreferences.showCandidateComments(context))
                    }

                    Text(
                        text = "编码注释",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 8.dp)
                    )
                    Text(
                        text = "在候选词上显示对应的编码（如五笔字根、声笔提示）",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(start = 16.dp, bottom = 12.dp)
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .padding(horizontal = 16.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        CommentDisplayCard(
                            title = "显示",
                            isSelected = showComments,
                            showComment = true,
                            onClick = {
                                showComments = true
                                SettingsPreferences.setShowCandidateComments(context, true)
                            },
                            modifier = Modifier.weight(1f)
                        )
                        CommentDisplayCard(
                            title = "隐藏",
                            isSelected = !showComments,
                            showComment = false,
                            onClick = {
                                showComments = false
                                SettingsPreferences.setShowCandidateComments(context, false)
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    if (showComments) {
                        var commentLayout by remember {
                            mutableStateOf(SettingsPreferences.getCandidateCommentLayout(context))
                        }
                        Text(
                            text = "注释样式",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 8.dp)
                        )
                        Text(
                            text = "旁注贴在字右侧，叠字把编码放在字上方并用竖线分格",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(start = 16.dp, bottom = 12.dp)
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(108.dp)
                                .padding(horizontal = 16.dp, vertical = 4.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            CommentLayoutCard(
                                title = "旁注",
                                isSelected = commentLayout == SettingsPreferences.COMMENT_LAYOUT_INLINE,
                                stacked = false,
                                onClick = {
                                    commentLayout = SettingsPreferences.COMMENT_LAYOUT_INLINE
                                    SettingsPreferences.setCandidateCommentLayout(
                                        context,
                                        SettingsPreferences.COMMENT_LAYOUT_INLINE
                                    )
                                },
                                modifier = Modifier.weight(1f)
                            )
                            CommentLayoutCard(
                                title = "叠字",
                                isSelected = commentLayout == SettingsPreferences.COMMENT_LAYOUT_STACKED,
                                stacked = true,
                                onClick = {
                                    commentLayout = SettingsPreferences.COMMENT_LAYOUT_STACKED
                                    SettingsPreferences.setCandidateCommentLayout(
                                        context,
                                        SettingsPreferences.COMMENT_LAYOUT_STACKED
                                    )
                                },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    HorizontalDivider(
                        modifier = Modifier.padding(start = 16.dp),
                        thickness = 0.5.dp,
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                    )

                    var inputTextLocation by remember {
                        mutableStateOf(SettingsPreferences.getInputTextLocation(context))
                    }

                    Text(
                        text = "编码显示",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 8.dp)
                    )
                    Text(
                        text = "编码放哪。预览上屏时输入框漂高亮词，编码仍在候选栏",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(start = 16.dp, bottom = 12.dp)
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                            .padding(horizontal = 16.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CodeDisplayCard(
                            title = "显示在输入框",
                            isSelected = inputTextLocation == SettingsPreferences.INPUT_TEXT_INPUT_BOX,
                            showCodeInInputBox = true,
                            onClick = {
                                inputTextLocation = SettingsPreferences.INPUT_TEXT_INPUT_BOX
                                SettingsPreferences.setInputTextLocation(context, SettingsPreferences.INPUT_TEXT_INPUT_BOX)
                            },
                            modifier = Modifier.weight(1f)
                        )
                        CodeDisplayCard(
                            title = "显示在候选栏",
                            isSelected = inputTextLocation == SettingsPreferences.INPUT_TEXT_CANDIDATE_BAR,
                            showCodeInInputBox = false,
                            onClick = {
                                inputTextLocation = SettingsPreferences.INPUT_TEXT_CANDIDATE_BAR
                                SettingsPreferences.setInputTextLocation(context, SettingsPreferences.INPUT_TEXT_CANDIDATE_BAR)
                            },
                            modifier = Modifier.weight(1f)
                        )
                        CodeDisplayCard(
                            title = "预览上屏",
                            isSelected = inputTextLocation == SettingsPreferences.INPUT_TEXT_COMMIT_PREVIEW,
                            showCodeInInputBox = false,
                            showCandidateInInputBox = true,
                            onClick = {
                                inputTextLocation = SettingsPreferences.INPUT_TEXT_COMMIT_PREVIEW
                                SettingsPreferences.setInputTextLocation(
                                    context,
                                    SettingsPreferences.INPUT_TEXT_COMMIT_PREVIEW,
                                )
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    HorizontalDivider(
                        modifier = Modifier.padding(start = 16.dp),
                        thickness = 0.5.dp,
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                    )

                    val pageSizePref = SettingsPreferences.getPageSize(context)
                    val effectiveValue = if (pageSizePref == 0) 20f else pageSizePref.toFloat()
                    var pageSizeSlider by remember(effectiveValue) {
                        mutableStateOf(effectiveValue)
                    }

                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "每页候选词数",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "${pageSizeSlider.toInt()} 个",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Slider(
                            value = pageSizeSlider,
                            onValueChange = { pageSizeSlider = it },
                            onValueChangeFinished = {
                                val intValue = pageSizeSlider.toInt()
                                SettingsPreferences.setPageSize(context, intValue)
                            },
                            valueRange = 20f..50f,
                            steps = 29
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "修改后需到方案设置中点击部署才能生效",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                    }
                })
            }

            item {
                SettingsSection(title = "键盘", content = {
                    var layoutPref by remember {
                        mutableStateOf(SettingsPreferences.getKeyboardLayout(context))
                    }
                    var showLayoutDialog by remember { mutableStateOf(false) }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showLayoutDialog = true }
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "布局选择",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = if (layoutPref == SettingsPreferences.KEYBOARD_LAYOUT_46) "46键布局" else "默认布局",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    if (showLayoutDialog) {
                        LayoutSelectionDialog(
                            current = layoutPref,
                            onDismiss = { showLayoutDialog = false },
                            onConfirm = { selected ->
                                layoutPref = selected
                                SettingsPreferences.setKeyboardLayout(context, selected)
                                // 布局切换会改变手势映射（46 键叠加 qwerty_46 覆盖段），必须重载配置
                                com.kingzcheung.xime.settings.KeysConfigHelper.loadConfig(context)
                                showLayoutDialog = false
                            }
                        )
                    }
                })
            }

            item {
                SettingsSection(title = "按键手势", content = {
                    var swipeUpEnabled by remember {
                        mutableStateOf(SettingsPreferences.isSwipeUpHintsEnabled(context))
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "上滑提示",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "在按键上显示上滑符号提示",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = swipeUpEnabled,
                            onCheckedChange = { newValue ->
                                swipeUpEnabled = newValue
                                SettingsPreferences.setSwipeUpHintsEnabled(context, newValue)
                            }
                        )
                    }
                    HorizontalDivider(
                        modifier = Modifier.padding(start = 16.dp),
                        thickness = 0.5.dp,
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                    )
                    var swipeDownEnabled by remember {
                        mutableStateOf(SettingsPreferences.isSwipeDownHintsEnabled(context))
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "下滑提示",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "在按键上显示下滑提示内容",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = swipeDownEnabled,
                            onCheckedChange = { newValue ->
                                swipeDownEnabled = newValue
                                SettingsPreferences.setSwipeDownHintsEnabled(context, newValue)
                            }
                        )
                    }
                    HorizontalDivider(
                        modifier = Modifier.padding(start = 16.dp),
                        thickness = 0.5.dp,
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                    )
                    var showPressBubble by remember {
                        mutableStateOf(SettingsPreferences.shouldShowPressBubble(context))
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "点按弹出气泡",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "在按键上显示当前按键字符气泡（关闭可减少快速打字卡顿）",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = showPressBubble,
                            onCheckedChange = { newValue ->
                                showPressBubble = newValue
                                SettingsPreferences.setShowPressBubble(context, newValue)
                            }
                        )
                    }
                })
            }
        }
    }
}
/**
 * 布局选择：surface 卡片 + 点行即关。设置页 Dialog、IME 遮罩共用。
 * 选中圆点取当前键盘主题强调色。
 */
@Composable
fun LayoutSelectionDialog(
    current: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit,
) {
    val context = LocalContext.current
    val themeId = SettingsPreferences.getKeyboardTheme(context)
    val isDark = MaterialTheme.colorScheme.surface.luminance() < 0.5f
    val accent = KeyboardThemes.getAccentColor(themeId, isDark)
    Dialog(onDismissRequest = onDismiss) {
        LayoutSelectionCard(
            current = current,
            accent = accent,
            onSelect = onConfirm,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
fun LayoutSelectionCard(
    current: String,
    accent: Color,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val scheme = MaterialTheme.colorScheme
    val options = listOf(
        SettingsPreferences.KEYBOARD_LAYOUT_DEFAULT to "默认",
        SettingsPreferences.KEYBOARD_LAYOUT_46 to "46键",
    )
    val shape = RoundedCornerShape(20.dp)
    Column(
        modifier = modifier
            .clip(shape)
            .background(scheme.surface)
            .border(1.dp, scheme.outlineVariant, shape)
            .padding(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 12.dp),
    ) {
        Text(
            text = "布局选择",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = scheme.onSurface,
        )
        Spacer(modifier = Modifier.height(8.dp))
        options.forEach { (value, label) ->
            val selected = current == value
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .clickable { onSelect(value) },
                verticalAlignment = Alignment.CenterVertically,
            ) {
                LayoutRadioDot(
                    selected = selected,
                    accent = accent,
                    unselected = scheme.onSurface.copy(alpha = 0.45f),
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodyLarge,
                    color = scheme.onSurface,
                )
            }
        }
    }
}

@Composable
private fun LayoutRadioDot(
    selected: Boolean,
    accent: Color,
    unselected: Color,
) {
    Box(
        modifier = Modifier.size(20.dp),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .border(2.dp, if (selected) accent else unselected, CircleShape),
        )
        if (selected) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(accent),
            )
        }
    }
}
