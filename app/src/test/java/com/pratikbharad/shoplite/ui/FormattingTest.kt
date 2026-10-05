package com.pratikbharad.shoplite.ui

import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.Locale

class FormattingTest {

    @Test
    fun `formats cents as US dollars`() {
        assertEquals("$9.99", formatPrice(999, Locale.US))
        assertEquals("$1,899.99", formatPrice(189999, Locale.US))
        assertEquals("$0.00", formatPrice(0, Locale.US))
    }

    @Test
    fun `formats category slugs for display`() {
        assertEquals("Home decoration", formatCategory("home-decoration", Locale.US))
        assertEquals("Beauty", formatCategory("beauty", Locale.US))
    }
}
