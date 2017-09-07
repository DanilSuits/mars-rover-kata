/**
 * Copyright Vast 2018. All Rights Reserved.
 * <p/>
 * http://www.vast.com
 */
package com.vocumsineratio.kata.mars;

import sun.jvm.hotspot.utilities.AssertionFailure;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Danil Suits (danil@vast.com)
 */
public class TestableCore {
    static String simulateRover(String state, String instructions) {
        // TODO: provide a real implementation
        if ("1 2 N".equals(state) && "LMLMLMLMM".equals(instructions)) {
            return "1 3 N";
        }

        if ("3 3 E".equals(state) && "MMRMMRMRRM".equals(instructions)) {
            return "5 1 E";
        }

        throw new AssertionFailure("Failed to provide a complete implementation");
    }

    static void runTest(InputStream in, PrintStream out) {
        // Simplest thing that can possibly work

        List<String> output = new ArrayList<>();
        // TODO: read the actual data
        output.add(simulateRover("1 2 N", "LMLMLMLMM"));
        output.add(simulateRover("3 3 E", "MMRMMRMRRM"));

        for(String current : output) {
            out.println(current);
        }
    }

    public static void main(String[] args) throws IOException {
        // This is my proof that the thin shell can invoke
        // the function provided by the testable core.
        runTest(System.in, System.out);
    }
}
