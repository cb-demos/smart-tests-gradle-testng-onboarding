package com.demo.catalog;

import org.testng.Assert;
import org.testng.annotations.Test;

public class ProductSearchTest {

    @Test
    public void testExactMatch() {
        Assert.assertTrue(ProductSearch.matches("shoes", "shoes"));
    }

    @Test
    public void testCaseInsensitive() {
        Assert.assertTrue(ProductSearch.matches("SHOES", "running shoes"));
    }

    @Test
    public void testPartialMatch() {
        Assert.assertTrue(ProductSearch.matches("run", "running shoes"));
    }

    @Test
    public void testNoMatch() {
        Assert.assertFalse(ProductSearch.matches("hat", "running shoes"));
    }

    @Test
    public void testNullProductNameHandled() {
        Assert.assertFalse(ProductSearch.matches("hat", null));
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testNullQueryRejected() {
        ProductSearch.matches(null, "shoes");
    }
}
