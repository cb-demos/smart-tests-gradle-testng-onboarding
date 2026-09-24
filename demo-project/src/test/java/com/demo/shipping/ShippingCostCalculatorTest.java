package com.demo.shipping;

import org.testng.Assert;
import org.testng.annotations.Test;

public class ShippingCostCalculatorTest {

    @Test
    public void testStandardShipping() {
        Assert.assertEquals(ShippingCostCalculator.calculate(0.0, "STANDARD"), 5.0);
    }

    @Test
    public void testExpressShipping() {
        Assert.assertEquals(ShippingCostCalculator.calculate(0.0, "EXPRESS"), 15.0);
    }

    @Test
    public void testHeavyPackageStandard() {
        Assert.assertEquals(ShippingCostCalculator.calculate(10.0, "STANDARD"), 17.0);
    }

    @Test
    public void testHeavyPackageExpress() {
        Assert.assertEquals(ShippingCostCalculator.calculate(10.0, "EXPRESS"), 27.0);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testUnknownMethodRejected() {
        ShippingCostCalculator.calculate(5.0, "TELEPORT");
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testNegativeWeightRejected() {
        ShippingCostCalculator.calculate(-1.0, "STANDARD");
    }
}
