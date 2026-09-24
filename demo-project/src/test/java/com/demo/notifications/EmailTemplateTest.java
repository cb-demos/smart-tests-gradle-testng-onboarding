package com.demo.notifications;

import org.testng.Assert;
import org.testng.annotations.Test;

public class EmailTemplateTest {

    @Test
    public void testWelcomeTemplate() {
        Assert.assertEquals(EmailTemplate.render("welcome", "Usha"), "Hello Usha, welcome!");
    }

    @Test
    public void testResetPasswordTemplate() {
        Assert.assertEquals(EmailTemplate.render("reset-password", "Usha"), "Hello Usha, reset your password.");
    }

    @Test
    public void testEmptyRecipientNameAllowed() {
        Assert.assertEquals(EmailTemplate.render("welcome", ""), "Hello , welcome!");
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testUnknownTemplateRejected() {
        EmailTemplate.render("goodbye", "Usha");
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testNullRecipientNameRejected() {
        EmailTemplate.render("welcome", null);
    }
}
