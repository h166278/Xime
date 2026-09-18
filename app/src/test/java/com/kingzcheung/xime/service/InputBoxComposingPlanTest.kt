package com.kingzcheung.xime.service

import com.kingzcheung.xime.settings.SettingsPreferences
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class InputBoxComposingPlanTest {

    @Test
    fun `空闲不写`() {
        assertNull(
            planInputBoxComposing(
                SettingsPreferences.INPUT_TEXT_INPUT_BOX,
                "shu ru fa",
                listOf("输入法"),
                0,
                isComposing = false,
            ),
        )
        assertNull(
            planInputBoxComposing(
                SettingsPreferences.INPUT_TEXT_COMMIT_PREVIEW,
                "shu ru fa",
                listOf("输入法"),
                0,
                isComposing = false,
            ),
        )
    }

    @Test
    fun `编码在输入框写编码`() {
        assertEquals(
            "shu ru fa",
            planInputBoxComposing(
                SettingsPreferences.INPUT_TEXT_INPUT_BOX,
                "shu ru fa",
                listOf("输入法", "力学"),
                1,
                isComposing = true,
            ),
        )
    }

    @Test
    fun `候选栏不写输入框`() {
        assertNull(
            planInputBoxComposing(
                SettingsPreferences.INPUT_TEXT_CANDIDATE_BAR,
                "shu ru fa",
                listOf("输入法"),
                0,
                isComposing = true,
            ),
        )
    }

    @Test
    fun `预览上屏跟高亮走`() {
        assertEquals(
            "输入法",
            planInputBoxComposing(
                SettingsPreferences.INPUT_TEXT_COMMIT_PREVIEW,
                "shu ru fa",
                listOf("输入法", "力学"),
                0,
                isComposing = true,
            ),
        )
        assertEquals(
            "力学",
            planInputBoxComposing(
                SettingsPreferences.INPUT_TEXT_COMMIT_PREVIEW,
                "shu ru fa",
                listOf("输入法", "力学"),
                1,
                isComposing = true,
            ),
        )
    }

    @Test
    fun `预览无候选清空输入框 composing`() {
        assertEquals(
            "",
            planInputBoxComposing(
                SettingsPreferences.INPUT_TEXT_COMMIT_PREVIEW,
                "shu ru fa",
                emptyList(),
                0,
                isComposing = true,
            ),
        )
    }

    @Test
    fun `高亮越界钳到末尾`() {
        assertEquals(
            "力学",
            planInputBoxComposing(
                SettingsPreferences.INPUT_TEXT_COMMIT_PREVIEW,
                "ni",
                listOf("输入法", "力学"),
                9,
                isComposing = true,
            ),
        )
    }

    @Test
    fun `写入判定`() {
        assertTrue(writesComposingToInputBox(SettingsPreferences.INPUT_TEXT_INPUT_BOX))
        assertTrue(writesComposingToInputBox(SettingsPreferences.INPUT_TEXT_COMMIT_PREVIEW))
        assertFalse(writesComposingToInputBox(SettingsPreferences.INPUT_TEXT_CANDIDATE_BAR))
        assertTrue(isCommitPreview(SettingsPreferences.INPUT_TEXT_COMMIT_PREVIEW))
        assertFalse(isCommitPreview(SettingsPreferences.INPUT_TEXT_INPUT_BOX))
    }

    @Test
    fun `T9 前缀拼高亮词`() {
        assertEquals("你好世界", composingTextWithT9Prefix("世界", "你好"))
        assertEquals("你好", composingTextWithT9Prefix("", "你好"))
        assertEquals("世界", composingTextWithT9Prefix("世界", ""))
        assertNull(composingTextWithT9Prefix(null, "你好"))
    }

    @Test
    fun `T9 半提交展示态不叠末词`() {
        assertEquals("你好", composingTextWithT9Prefix("你好", "你好"))
        assertEquals("你好世界", composingTextWithT9Prefix("世界", "你好世界"))
    }

    @Test
    fun `编码档 composing 不拼 T9 前缀`() {
        assertEquals(
            "nihao",
            inputBoxComposingText(
                SettingsPreferences.INPUT_TEXT_INPUT_BOX,
                "nihao",
                listOf("你好"),
                0,
                isComposing = true,
                t9Prefix = "你",
            ),
        )
    }

    @Test
    fun `预览被宿主拿走只在 span 消失时成立`() {
        assertTrue(
            previewStolenByHost(
                commitPreview = true,
                engineComposing = true,
                composingStart = -1,
                composingEnd = -1,
            ),
        )
        assertFalse(
            previewStolenByHost(
                commitPreview = true,
                engineComposing = true,
                composingStart = 0,
                composingEnd = 2,
            ),
        )
        assertFalse(
            previewStolenByHost(
                commitPreview = false,
                engineComposing = true,
                composingStart = -1,
                composingEnd = -1,
            ),
        )
        assertFalse(
            previewStolenByHost(
                commitPreview = true,
                engineComposing = false,
                composingStart = -1,
                composingEnd = -1,
            ),
        )
    }

    @Test
    fun `IME自己上屏的负一只吞有限次`() {
        assertTrue(
            shouldSwallowImeCommitComposingLoss(
                commitPreview = true,
                remaining = 3,
                previewComposingActive = false,
                composingStart = -1,
                composingEnd = -1,
            ),
        )
        assertFalse(
            shouldSwallowImeCommitComposingLoss(
                commitPreview = true,
                remaining = 3,
                previewComposingActive = true,
                composingStart = -1,
                composingEnd = -1,
            ),
        )
        assertFalse(
            shouldSwallowImeCommitComposingLoss(
                commitPreview = true,
                remaining = 0,
                previewComposingActive = false,
                composingStart = -1,
                composingEnd = -1,
            ),
        )
        assertFalse(
            shouldSwallowImeCommitComposingLoss(
                commitPreview = false,
                remaining = 3,
                previewComposingActive = false,
                composingStart = -1,
                composingEnd = -1,
            ),
        )
        assertFalse(
            shouldSwallowImeCommitComposingLoss(
                commitPreview = true,
                remaining = 3,
                previewComposingActive = false,
                composingStart = 0,
                composingEnd = 2,
            ),
        )
        assertEquals(3, IME_COMMIT_COMPOSING_LOSS_SWALLOW)
    }

    @Test
    fun `截胡后过期刷新不写回同一串码`() {
        assertTrue(
            shouldDropStalePreviewWrite(
                previewAbandoned = true,
                abandonedInput = "ni",
                incomingInput = "ni",
            ),
        )
        assertTrue(
            shouldDropStalePreviewWrite(
                previewAbandoned = true,
                abandonedInput = "ni",
                incomingInput = "",
            ),
        )
        assertFalse(
            shouldDropStalePreviewWrite(
                previewAbandoned = true,
                abandonedInput = "ni",
                incomingInput = "hao",
            ),
        )
        assertFalse(
            shouldDropStalePreviewWrite(
                previewAbandoned = false,
                abandonedInput = "ni",
                incomingInput = "ni",
            ),
        )
        assertTrue(
            shouldDropStalePreviewWrite(
                previewAbandoned = true,
                abandonedInput = "",
                incomingInput = "",
            ),
        )
        assertFalse(
            shouldDropStalePreviewWrite(
                previewAbandoned = true,
                abandonedInput = "",
                incomingInput = "ni",
            ),
        )
    }

    @Test
    fun `空刷新不拆截胡守卫新码才拆`() {
        assertFalse(
            shouldClearAbandonedPreview(
                previewAbandoned = true,
                abandonedInput = "ni",
                incomingInput = "",
            ),
        )
        assertFalse(
            shouldClearAbandonedPreview(
                previewAbandoned = true,
                abandonedInput = "ni",
                incomingInput = "ni",
            ),
        )
        assertTrue(
            shouldClearAbandonedPreview(
                previewAbandoned = true,
                abandonedInput = "ni",
                incomingInput = "hao",
            ),
        )
        assertTrue(
            shouldClearAbandonedPreview(
                previewAbandoned = false,
                abandonedInput = "ni",
                incomingInput = "",
            ),
        )
        assertFalse(
            shouldClearAbandonedPreview(
                previewAbandoned = true,
                abandonedInput = "",
                incomingInput = "",
            ),
        )
        assertTrue(
            shouldClearAbandonedPreview(
                previewAbandoned = true,
                abandonedInput = "",
                incomingInput = "ni",
            ),
        )
    }

    @Test
    fun `发送后空框当截胡探针失败不当空`() {
        assertTrue(editorLooksEmpty("", ""))
        assertTrue(editorLooksEmpty(null, ""))
        assertTrue(editorLooksEmpty("", null))
        assertFalse(editorLooksEmpty(null, null))
        assertFalse(editorLooksEmpty("你", ""))
        assertTrue(editorLooksEmpty(null, null, ""))
        assertFalse(editorLooksEmpty(null, null, "你"))
        assertFalse(editorLooksEmpty("", "x", ""))
        assertTrue(
            shouldAbandonPreviewOnRestart(
                commitPreview = true,
                restarting = true,
                engineComposing = true,
                editorEmpty = true,
            ),
        )
        assertFalse(
            shouldAbandonPreviewOnRestart(
                commitPreview = true,
                restarting = true,
                engineComposing = true,
                editorEmpty = false,
            ),
        )
        assertFalse(
            shouldAbandonPreviewOnRestart(
                commitPreview = true,
                restarting = false,
                engineComposing = true,
                editorEmpty = true,
            ),
        )
        assertFalse(
            shouldAbandonPreviewOnRestart(
                commitPreview = true,
                restarting = true,
                engineComposing = false,
                editorEmpty = true,
            ),
        )
    }

    @Test
    fun `刚上屏的字不当截胡残留删`() {
        assertFalse(shouldDeleteLeftoverPreview("你好", "你好"))
        assertTrue(shouldDeleteLeftoverPreview("你好", "世界"))
        assertTrue(shouldDeleteLeftoverPreview("你好", ""))
        assertFalse(shouldDeleteLeftoverPreview("", "你好"))
    }

    @Test
    fun `预览档 composing 拼 T9 前缀`() {
        assertEquals(
            "你好世界",
            inputBoxComposingText(
                SettingsPreferences.INPUT_TEXT_COMMIT_PREVIEW,
                "shijie",
                listOf("世界"),
                0,
                isComposing = true,
                t9Prefix = "你好",
            ),
        )
    }
}
