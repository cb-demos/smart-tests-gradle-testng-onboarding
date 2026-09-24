package com.demo.orders;

import org.testng.Assert;
import org.testng.annotations.Test;

public class OrderTotalCalculatorTest {

    @Test
    public void testBasicTotal() {
        Assert.assertEquals(OrderTotalCalculator.calculateTotal(100.0, 0.0, 0.0), 100.0);
    }

    @Test
    public void testWithTax() {
        Assert.assertEquals(OrderTotalCalculator.calculateTotal(100.0, 10.0, 0.0), 110.0);
    }

    @Test
    public void testWithShipping() {
        Assert.assertEquals(OrderTotalCalculator.calculateTotal(100.0, 0.0, 15.0), 115.0);
    }

    @Test
    public void testWithTaxAndShipping() {
        Assert.assertEquals(OrderTotalCalculator.calculateTotal(200.0, 5.0, 10.0), 220.0);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testNegativeSubtotalRejected() {
        OrderTotalCalculator.calculateTotal(-50.0, 10.0, 0.0);
    }

    @Test
    public void testZeroEverything() {
        Assert.assertEquals(OrderTotalCalculator.calculateTotal(0.0, 0.0, 0.0), 0.0);
    }
}
