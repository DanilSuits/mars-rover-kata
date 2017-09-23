/**
 * Copyright Vast 2018. All Rights Reserved.
 * <p/>
 * http://www.vast.com
 */
package com.vocumsineratio.kata.mars;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.Charset;

/**
 * @author Danil Suits (danil@vast.com)
 */
public class DesignTest {
    @Test
    public void testSampleProgram() {
        check("5 5\n1 2 N\nLMLMLMLMM\n3 3 E\nMMRMMRMRRM\n", "1 3 N\n5 1 E\n");
    }

    @Test
    public void testWithNoInstructions() {
        // If the rovers don't have any instructions, then they should stay put.
        check("5 5\n1 2 N\n\n3 3 E\n\n", "1 2 N\n3 3 E\n");
    }
    
    private void check(String input, String expectedOutput) {
        InputStream in = new ByteArrayInputStream(input.getBytes());

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        TestableCore.runTest(in, new PrintStream(baos));

        byte [] actual = baos.toByteArray();
        Assert.assertEquals(actual, expectedOutput.getBytes());
    }
}
