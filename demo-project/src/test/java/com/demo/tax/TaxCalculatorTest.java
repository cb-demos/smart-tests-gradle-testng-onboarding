package com.demo.tax;

import org.testng.Assert;
import org.testng.annotations.Test;

public class TaxCalculatorTest {

    @Test
    public void testUSTax() {
        Assert.assertEquals(TaxCalculator.calculateTax(100.0, "US"), 7.5);
    }

    @Test
    public void testEUTax() {
        Assert.assertEquals(TaxCalculator.calculateTax(100.0, "EU"), 20.0);
    }

    @Test
    public void testUKTax() {
        Assert.assertEquals(TaxCalculator.calculateTax(100.0, "UK"), 20.0);
    }

    @Test
    public void testZeroAmount() {
        Assert.assertEquals(TaxCalculator.calculateTax(0.0, "US"), 0.0);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testUnknownRegionRejected() {
        TaxCalculator.calculateTax(100.0, "MARS");
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testNegativeAmountRejected() {
        TaxCalculator.calculateTax(-10.0, "US");
    }
}
