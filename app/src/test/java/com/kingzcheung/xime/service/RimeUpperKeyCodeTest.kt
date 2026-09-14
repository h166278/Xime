package com.kingzcheung.xime.service

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class RimeUpperKeyCodeTest {

    @Test
    fun `大写字母解析成 A-Z 键码`() {
        assertEquals('B'.code, resolveRimeUpperKeyCode("${RIME_UPPER_PREFIX}B"))
        assertEquals('A'.code, resolveRimeUpperKeyCode("${RIME_UPPER_PREFIX}A"))
        assertEquals('Z'.code, resolveRimeUpperKeyCode("${RIME_UPPER_PREFIX}Z"))
    }

    @Test
    fun `小写也会抬成大写键码`() {
        assertEquals('B'.code, resolveRimeUpperKeyCode("${RIME_UPPER_PREFIX}b"))
    }

    @Test
    fun `非通道返回 null`() {
        assertNull(resolveRimeUpperKeyCode("b"))
        assertNull(resolveRimeUpperKeyCode("B"))
        assertNull(resolveRimeUpperKeyCode("shift"))
        assertNull(resolveRimeUpperKeyCode("${RIME_UPPER_PREFIX}"))
        assertNull(resolveRimeUpperKeyCode("${RIME_UPPER_PREFIX}BB"))
        assertNull(resolveRimeUpperKeyCode("${RIME_UPPER_PREFIX}1"))
        assertNull(resolveRimeUpperKeyCode("${RIME_UPPER_PREFIX};"))
    }
}
