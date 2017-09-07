/**
 * Copyright Vast 2018. All Rights Reserved.
 * <p/>
 * http://www.vast.com
 */
package com.vocumsineratio.kata.mars;

import sun.jvm.hotspot.utilities.AssertionFailure;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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

    static void runTest(InputStream in, PrintStream out) throws IOException {
        // Simplest thing that can possibly work
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));

        // TODO: we eventually will need to do something with the state of the grid.
        reader.readLine();

        List<String> output = new ArrayList<>();
        // TODO: let the input data drive the number of rovers.
        output.add(simulateRover(reader.readLine(), reader.readLine()));
        output.add(simulateRover(reader.readLine(), reader.readLine()));

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
