package com.demo.coupons;

import org.testng.Assert;
import org.testng.annotations.Test;

public class CouponValidatorTest {

    @Test
    public void testValidCoupon() {
        Assert.assertTrue(CouponValidator.isValid("SAVE10", 100.0));
    }

    @Test
    public void testInvalidPrefix() {
        Assert.assertFalse(CouponValidator.isValid("DEAL10", 100.0));
    }

    @Test
    public void testTooShortCode() {
        Assert.assertFalse(CouponValidator.isValid("SAVE", 100.0));
    }

    @Test
    public void testNullCode() {
        Assert.assertFalse(CouponValidator.isValid(null, 100.0));
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testZeroOrderRejected() {
        CouponValidator.isValid("SAVE10", 0.0);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testNegativeOrderRejected() {
        CouponValidator.isValid("SAVE10", -5.0);
    }
}
