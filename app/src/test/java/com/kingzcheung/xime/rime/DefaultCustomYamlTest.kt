package com.kingzcheung.xime.rime

import com.kingzcheung.xime.settings.SchemaManager
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * default.custom.yaml 维护逻辑（纯函数）：
 * - [RimeConfigHelper.patchDefaultCustomContent]：把用户目录文件的 menu/page_size
 *   强制对齐为 app 设置值（app 固定 assets/default.custom.yaml 模板的同步规则，
 *   .custom.yaml 可以被覆盖、不保留旧的 PC 遗留值 5）；
 * - 与 [SchemaManager.replaceSchemaListBlock]（setEnabledSchemas 只替换 schema_list 块）
 *   的组合：启停方案后 menu 等其余 patch 必须保留。
 *
 * 每页候选数的最终生效在 JNI 层 setPageSize 直接注入引擎
 * （rime_jni.cc，配置缓存 + 会话幂等刷新），不依赖本文件内容。
 * 带 alternative_select_keys / select_comment_pattern 的方案不覆盖，见
 * [RimeConfigHelper.schemaKeepsOwnPageSize]。
 */
class DefaultCustomYamlTest {

    private val builtinTemplate = """
        patch:
          schema_list:
            - schema: wubi86
          menu:
            page_size: 20
          switcher:
            caption: "〔方案选单〕"
    """.trimIndent()

    /** submodule 版：page_size: 5 带 PC 风格行内注释。 */
    private val submoduleCopy = """
        patch:
          schema_list:
            - schema: wubi86
          menu:
            page_size: 5                    # 候选词数量
          switcher:
            caption: "〔方案选单〕"
    """.trimIndent()

    /** 旧版 setEnabledSchemas 整文件重写后的空壳。 */
    private val legacyShell = """
        patch:
          schema_list:
            - schema: wubi86
    """.trimIndent()

    private fun extractPageSize(text: String): Int? =
        text.lines().firstOrNull { it.trimStart().startsWith("page_size:") }
            ?.trimStart()?.removePrefix("page_size:")
            ?.substringBefore('#')?.trim()?.toIntOrNull()

    // ---- patchDefaultCustomContent（default 层对齐）----

    @Test
    fun `已是目标值无需修补`() {
        assertNull(RimeConfigHelper.patchDefaultCustomContent(builtinTemplate, 20))
    }

    @Test
    fun `旧值5强制对齐设置值`() {
        val patched = RimeConfigHelper.patchDefaultCustomContent(submoduleCopy, 20)!!
        assertEquals(20, extractPageSize(patched))
        assertFalse("行内注释随旧值一并移除", patched.contains("# 候选词数量"))
        assertTrue("menu 节保留", patched.contains("menu:"))
        assertTrue("switcher 节保留", patched.contains("switcher:"))
    }

    @Test
    fun `page_size缺失时在patch下补menu节`() {
        val patched = RimeConfigHelper.patchDefaultCustomContent(legacyShell, 20)!!
        assertEquals(20, extractPageSize(patched))
        val menuIdx = patched.lines().indexOfFirst { it.trim() == "menu:" }
        val patchIdx = patched.lines().indexOfFirst { it.trim() == "patch:" }
        assertTrue("menu 节应在 patch: 之内", menuIdx > patchIdx)
        assertTrue("schema_list 保留", patched.contains("- schema: wubi86"))
    }

    @Test
    fun `用户改过的值也会被设置值覆盖`() {
        // .custom.yaml 可以被覆盖：app 设置值（prefs）是权威
        val customized = builtinTemplate.replace("page_size: 20", "page_size: 15")
        val patched = RimeConfigHelper.patchDefaultCustomContent(customized, 20)!!
        assertEquals(20, extractPageSize(patched))
    }

    @Test
    fun `非数值的page_size自愈为设置值`() {
        // page_size: abc 是无效配置（引擎读不出会兜底 5），对齐为设置值是自愈
        val patched = RimeConfigHelper.patchDefaultCustomContent("patch:\n  page_size: abc\n", 20)!!
        assertEquals(20, extractPageSize(patched))
    }

    @Test
    fun `无patch根且无page_size的纯文本不动`() {
        assertNull(RimeConfigHelper.patchDefaultCustomContent("# just a comment\n", 20))
    }

    @Test
    fun `CRLF文件修补后保留CRLF`() {
        val crlf = "patch:\r\n  schema_list:\r\n    - schema: wubi86\r\n  menu:\r\n    page_size: 5\r\n"
        val patched = RimeConfigHelper.patchDefaultCustomContent(crlf, 20)!!
        assertTrue("CRLF 必须保留", patched.contains("\r\n"))
        assertFalse("无裸 LF 残留", patched.replace("\r\n", "").contains("\n"))
        assertEquals(20, extractPageSize(patched))
    }

    // ---- 与 replaceSchemaListBlock 的组合（setEnabledSchemas 真实路径）----

    @Test
    fun `替换patch缩进的schema_list后menu节保留`() {
        // 模拟 setEnabledSchemas：模板/修补后的文件上替换启停后的方案列表
        val result = SchemaManager.replaceSchemaListBlock(
            builtinTemplate, listOf("pinyin_simp", "t9_pinyin")
        )
        assertTrue(result.contains("  - schema: pinyin_simp"))
        assertTrue(result.contains("  - schema: t9_pinyin"))
        assertFalse("旧列表清掉", result.contains("- schema: wubi86"))
        assertEquals(20, extractPageSize(result))
        assertTrue("menu 节保留", result.contains("menu:"))
    }

    @Test
    fun `空壳先修补再替换schema_list结果完整`() {
        val patched = RimeConfigHelper.patchDefaultCustomContent(legacyShell, 20)!!
        val result = SchemaManager.replaceSchemaListBlock(patched, listOf("pinyin_simp"))
        assertEquals(20, extractPageSize(result))
        assertTrue(result.contains("  - schema: pinyin_simp"))
    }

    // ---- schemaKeepsOwnPageSize（选重布局不覆盖 page_size）----

    @Test
    fun `飞系alternative_select_keys跳过覆盖`() {
        assertTrue(RimeConfigHelper.schemaKeepsOwnPageSize("_aeuio", null))
    }

    @Test
    fun `象码飞天_23789跳过覆盖`() {
        assertTrue(RimeConfigHelper.schemaKeepsOwnPageSize("_23789", null))
    }

    @Test
    fun `只有select_comment_pattern也跳过`() {
        assertTrue(
            RimeConfigHelper.schemaKeepsOwnPageSize(null, "^[a-z]{4,}|[a-z]{3}[0-9][aeuio]{1,}$")
        )
    }

    @Test
    fun `拼音五笔两项皆空继续覆盖`() {
        assertFalse(RimeConfigHelper.schemaKeepsOwnPageSize(null, null))
        assertFalse(RimeConfigHelper.schemaKeepsOwnPageSize("", ""))
        assertFalse(RimeConfigHelper.schemaKeepsOwnPageSize("   ", "\t"))
    }

    // ---- patchShiftTabBindings（根层 Shift+Tab 对齐声笔）----

    private val oldShiftTab = """
        patch:
          key_binder:
            bindings:
              - { when: has_menu, accept: minus, send: Page_Up }
              -
              # 上下翻页 tab
              - { when: has_menu, accept: Shift+Tab, send: Page_Up }
              - { when: has_menu, accept: Tab, send: Page_Down }
              - { when: composing, accept: Control+p, send: Up }
    """.trimIndent()

    @Test
    fun `旧has_menu_ShiftTab改成paging上一页加跳尾`() {
        val patched = RimeConfigHelper.patchShiftTabBindings(oldShiftTab)!!
        assertTrue(patched.contains("when: paging, accept: Shift+Tab, send: Page_Up"))
        assertTrue(patched.contains("when: has_menu, accept: Shift+Tab, send_sequence:"))
        assertTrue(patched.contains("when: has_menu, accept: Tab, send: Page_Down"))
        assertFalse(
            "旧的 has_menu+Page_Up 必须去掉",
            patched.lines().any {
                val t = it.trimStart()
                t.startsWith("-") && t.contains("has_menu") && t.contains("Shift+Tab") &&
                    t.contains("Page_Up") && !t.contains("send_sequence")
            }
        )
        assertFalse("孤立短横线去掉", patched.lines().any { it.trim() == "-" })
        assertTrue("emacs 绑定保留", patched.contains("Control+p"))
        val downs = Regex("\\{Page_Down\\}").findAll(patched).count()
        assertEquals("跳尾 31 次 Page_Down × 两条（Tab 与 ISO）", 62, downs)
    }

    @Test
    fun `已是声笔形态不再修补`() {
        val already = """
            patch:
              key_binder:
                bindings:
                  - { when: has_menu, accept: Tab, send: Page_Down }
                  - { when: paging, accept: Shift+Tab, send: Page_Up }
                  - { when: has_menu, accept: Shift+Tab, send_sequence: "{Page_Down}{Page_Down}" }
        """.trimIndent()
        assertNull(RimeConfigHelper.patchShiftTabBindings(already))
    }

    @Test
    fun `没有ShiftTab绑定不动`() {
        assertNull(RimeConfigHelper.patchShiftTabBindings(builtinTemplate))
    }

    // ---- ascii_composer / 分号引号选重 对齐 sbsrf ----

    private val oldAsciiComposer = """
        patch:
          ascii_composer:
            good_old_caps_lock: true
            switch_key:
              Caps_Lock: commit_code
              Shift_L: commit_code
              Shift_R: commit_code
              Control_L: noop
              Control_R: noop
          key_binder:
            bindings:
              - { when: has_menu, accept: semicolon, send: 2 }
              - { when: has_menu, accept: apostrophe, send: 3 }
              - { when: has_menu, accept: minus, send: Page_Up }
    """.trimIndent()

    @Test
    fun `ascii_composer改成inline_ascii和Caps清码`() {
        val patched = RimeConfigHelper.patchAsciiComposerToSbsrf(oldAsciiComposer)!!
        assertTrue(patched.contains("Shift_L: inline_ascii"))
        assertTrue(patched.contains("Shift_R: inline_ascii"))
        assertTrue(patched.contains("Control_L: commit_code"))
        assertTrue(patched.contains("Caps_Lock: clear"))
        assertTrue(patched.contains("Eisu_toggle: clear"))
        assertFalse(patched.contains("Shift_L: commit_code"))
        assertFalse(patched.contains("Caps_Lock: commit_code"))
        assertNull("已是目标不再改", RimeConfigHelper.patchAsciiComposerToSbsrf(patched))
    }

    @Test
    fun `去掉分号引号选重保留其它绑定`() {
        val patched = RimeConfigHelper.patchRemoveSelectKeys(oldAsciiComposer)!!
        assertFalse(patched.contains("accept: semicolon"))
        assertFalse(patched.contains("accept: apostrophe"))
        assertTrue(patched.contains("accept: minus"))
        assertNull(RimeConfigHelper.patchRemoveSelectKeys(patched))
    }

    @Test
    fun `没有ascii_composer不动`() {
        assertNull(RimeConfigHelper.patchAsciiComposerToSbsrf(builtinTemplate))
    }
}
