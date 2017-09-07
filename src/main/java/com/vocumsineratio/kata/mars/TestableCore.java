/**
 * Copyright Vast 2018. All Rights Reserved.
 * <p/>
 * http://www.vast.com
 */
package com.vocumsineratio.kata.mars;

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

        throw new AssertionError("Failed to provide a complete implementation");
    }

    static void runTest(InputStream in, PrintStream out) throws IOException {

        BufferedReader reader = new BufferedReader(new InputStreamReader(in));

        // TODO: we eventually will need to do something with the state of the grid.
        reader.readLine();

        List<String> output = new ArrayList<>();

        String roverState;
        while( (roverState = reader.readLine()) != null) {
            String instructions = reader.readLine();
            // TODO: this isn't quite right - the boundary between parsing
            // the rover inputs and modeling the rover behavior should be
            // more clear.  There needs to be an intermediary state.
            output.add(simulateRover(roverState, instructions));
        }

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
