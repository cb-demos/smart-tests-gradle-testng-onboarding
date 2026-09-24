package com.demo.auth;

import org.testng.Assert;
import org.testng.annotations.Test;

public class PasswordValidatorTest {

    @Test
    public void testStrongPassword() {
        Assert.assertTrue(PasswordValidator.isStrong("Abcdef12"));
    }

    @Test
    public void testTooShort() {
        Assert.assertFalse(PasswordValidator.isStrong("Ab1"));
    }

    @Test
    public void testNoDigit() {
        Assert.assertFalse(PasswordValidator.isStrong("Abcdefgh"));
    }

    @Test
    public void testNoUppercase() {
        Assert.assertFalse(PasswordValidator.isStrong("abcdefg1"));
    }

    @Test
    public void testEmptyPasswordRejected() {
        Assert.assertFalse(PasswordValidator.isStrong(""));
    }

    @Test
    public void testNullPasswordRejected() {
        Assert.assertFalse(PasswordValidator.isStrong(null));
    }
}
