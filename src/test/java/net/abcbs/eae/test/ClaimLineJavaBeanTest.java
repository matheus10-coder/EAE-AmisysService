package net.abcbs.eae.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import net.abcbs.rpa.javabeans.ClaimLineJavaBean;

public class ClaimLineJavaBeanTest {
    /**
     * Method under test: {@link ClaimLineJavaBean#exceptionMessage()}
     */
    @Test
    public void testExceptionMessage() {
        assertEquals(1, (new ClaimLineJavaBean()).exceptionMessage().size());
    }

    /**
     * Method under test: {@link ClaimLineJavaBean#toDecimal(String)}
     */
    @Test
    public void testToDecimal() {
        assertEquals("Orig S.tr", ClaimLineJavaBean.toDecimal("Orig Str"));
        assertEquals(
                "Error: Current string is null, decimal manipulation cannot be done. Please, review input variable",
                ClaimLineJavaBean.toDecimal(""));
        assertEquals("0..", ClaimLineJavaBean.toDecimal("."));
    }

    /**
     * Method under test: {@link ClaimLineJavaBean#toLocalCurrency(String)}
     */
    @Test
    public void testToLocalCurrency() {
    	assertEquals("$10.43", ClaimLineJavaBean.toLocalCurrency("10.43"));
        assertEquals("0..", ClaimLineJavaBean.toDecimal("."));
    }

    /**
     * Method under test: {@link ClaimLineJavaBean#toLocalCurrency(String)}
     */
    @Test
    public void testToLocalCurrency2() {
        assertEquals(
                "Error: Current string is null, currency manipulation cannot be done. Please, review input variable\"",
                ClaimLineJavaBean.toLocalCurrency(""));
    }

    /**
     * Method under test: {@link ClaimLineJavaBean#toLocalCurrency(String)}
     */
    @Test
    public void testToLocalCurrency3() {
        assertEquals("$42.00", ClaimLineJavaBean.toLocalCurrency("42"));
    }
}