package com.kingzcheung.xime.ui.keyboard

import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.ui.unit.LayoutDirection
import org.junit.Assert.assertEquals
import org.junit.Test

class KeyboardResponsiveSizingTest {
    private val tolerance = 0.0001f

    @Test
    fun phoneSizedKeyKeepsOriginalSizing() {
        assertEquals(1f, adaptiveKeyContentScale(keyHeightDp = 56f), tolerance)
        assertEquals(1f, adaptiveHintScale(contentScale = 1f), tolerance)
        assertEquals(14f, adaptiveHintOffsetDp(contentScale = 1f), tolerance)
    }

    @Test
    fun largerKeyScalesLabelsAndHintsWithinLimits() {
        assertEquals(1.25f, adaptiveKeyContentScale(keyHeightDp = 70f), tolerance)
        assertEquals(1.7f, adaptiveHintScale(contentScale = 1.5f), tolerance)
        assertEquals(1.5f, adaptiveBubbleScale(contentScale = 1.5f), tolerance)
        assertEquals(24f, adaptiveHintOffsetDp(contentScale = 1.5f), tolerance)
    }

    @Test
    fun smallerKeysAreNotShrunkAndLargeKeysAreClamped() {
        assertEquals(1f, adaptiveKeyContentScale(keyHeightDp = 20f), tolerance)
        assertEquals(1.5f, adaptiveKeyContentScale(keyHeightDp = 120f), tolerance)
    }

    @Test
    fun spaceSwipeUpMatchesOrdinaryKeyThreshold() {
        assertEquals(50f, SPACE_SWIPE_UP_THRESHOLD_DP.value, tolerance)
    }

    @Test
    fun swipeUpHintTextSticksToCornerWhileIconKeepsGap() {
        val ltr = LayoutDirection.Ltr
        assertEquals(0.5f, swipeUpHintTextPadding.calculateTopPadding().value, tolerance)
        assertEquals(1f, swipeUpHintTextPadding.calculateEndPadding(ltr).value, tolerance)
        assertEquals(3f, swipeUpHintIconPadding.calculateTopPadding().value, tolerance)
        assertEquals(4f, swipeUpHintIconPadding.calculateEndPadding(ltr).value, tolerance)
        assertEquals(false, swipeUpHintTextStyle.platformStyle?.paragraphStyle?.includeFontPadding)
    }
}
