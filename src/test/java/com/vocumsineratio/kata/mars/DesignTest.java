/**
 * Copyright Vast 2018. All Rights Reserved.
 * <p/>
 * http://www.vast.com
 */
package com.vocumsineratio.kata.mars;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.Charset;

/**
 * @author Danil Suits (danil@vast.com)
 */
public class DesignTest {
    @Test
    public void testSampleProgram() throws IOException {
        check("5 5\n1 2 N\nLMLMLMLMM\n3 3 E\nMMRMMRMRRM\n", "1 3 N\n5 1 E\n");
    }

    @Test
    public void testReversedSample() throws IOException {
        check("5 5\n3 3 E\nMMRMMRMRRM\n1 2 N\nLMLMLMLMM", "5 1 E\n1 3 N\n");
    }

    @Test
    public void testWithNoInstructions() throws IOException {
        // If the rovers don't have any instructions, then they should stay put.
        check("5 5\n1 2 N\n\n3 3 E\n\n", "1 2 N\n3 3 E\n");
    }

    @DataProvider(name = "simplePrograms")
    Object[][] simplePrograms () {
        return new Object[][] {
                {"1 2 N", "L", "1 2 W"},
                {"1 2 W", "L", "1 2 S"},
                {"1 2 S", "L", "1 2 E"}
        } ;
    }
    
    @Test (dataProvider = "simplePrograms")
    public void testSimpleProgram(String start, String instructions, String end) throws IOException {
        String [] data = { "5 5", start, instructions };
        StringBuilder input = new StringBuilder();
        for(String line : data) {
            input.append(line).append(System.lineSeparator());
        }
        
        final String expectedOutput = end + System.lineSeparator();
        check(input.toString(), expectedOutput);
    }

    private void check(String input, String expectedOutput) throws IOException {
        InputStream in = new ByteArrayInputStream(input.getBytes());

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        TestableCore.runTest(in, new PrintStream(baos));

        String actual = new String(baos.toByteArray());
        Assert.assertEquals(actual, expectedOutput);
    }
}
