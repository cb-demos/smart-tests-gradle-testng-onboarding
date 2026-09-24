package com.demo.auth;

import org.testng.Assert;
import org.testng.annotations.Test;

public class SessionTokenTest {

    @Test
    public void testGenerateToken() {
        Assert.assertEquals(SessionToken.generate("user1"), "user1-token");
    }

    @Test
    public void testValidToken() {
        Assert.assertTrue(SessionToken.isValid("user1-token"));
    }

    @Test
    public void testInvalidTokenNull() {
        Assert.assertFalse(SessionToken.isValid(null));
    }

    @Test
    public void testInvalidTokenFormat() {
        Assert.assertFalse(SessionToken.isValid("garbage"));
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testGenerateWithEmptyUserIdRejected() {
        SessionToken.generate("");
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testGenerateWithNullUserIdRejected() {
        SessionToken.generate(null);
    }
}
