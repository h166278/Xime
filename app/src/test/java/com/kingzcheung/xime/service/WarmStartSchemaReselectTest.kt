package com.kingzcheung.xime.service

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class WarmStartSchemaReselectTest {

    @Test
    fun `方案已对上则跳过重切`() {
        assertTrue(shouldSkipWarmStartSchemaReselect("sbfd", "sbfd"))
    }

    @Test
    fun `方案不同要切`() {
        assertFalse(shouldSkipWarmStartSchemaReselect("sbfd", "luna_pinyin"))
    }

    @Test
    fun `current 空是冷启动未就绪 不能跳过`() {
        assertFalse(shouldSkipWarmStartSchemaReselect("sbfd", ""))
    }

    @Test
    fun `saved 空不能跳过`() {
        assertFalse(shouldSkipWarmStartSchemaReselect("", "sbfd"))
        assertFalse(shouldSkipWarmStartSchemaReselect("", ""))
    }
}
