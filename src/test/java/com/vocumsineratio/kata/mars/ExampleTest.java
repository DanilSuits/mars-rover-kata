/**
 * Copyright Vast 2018. All Rights Reserved.
 * <p/>
 * http://www.vast.com
 */
package com.vocumsineratio.kata.mars;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;

/**
 * @author Danil Suits (danil@vast.com)
 */
public class ExampleTest {
    @Test
    public void testPipesAndFiltersInterface() throws IOException {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try(InputStream in = ExampleTest.class.getResourceAsStream("/thoughtworks.sample.input.txt")) {
            PrintStream out = new PrintStream(baos);
        }

        BufferedReader actual = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(baos.toByteArray())));

        try(BufferedReader expected = new BufferedReader(new InputStreamReader(ExampleTest.class.getResourceAsStream("/thoughtworks.sample.output.txt")))) {
            String expectedLine;
            while((expectedLine = expected.readLine()) != null) {
                String actualLine = actual.readLine();
                Assert.assertEquals(actualLine, expectedLine);
            }

            Assert.assertNull(expectedLine, "The expected data should be exhausted when we exit the loop");
            Assert.assertEquals(actual.readLine(), expectedLine);
        }
    }
}
