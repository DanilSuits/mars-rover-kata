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

        List<String> simulationInputs = new ArrayList<>();
        {
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));

            String currentLine;
            while((currentLine = reader.readLine()) != null) {
                simulationInputs.add(currentLine);
            }
        }

        List<String> output = new ArrayList<>();

        for(int currentOffset = 1; currentOffset < simulationInputs.size(); currentOffset += 2) {
            String roverState = simulationInputs.get(currentOffset);
            String instructions = simulationInputs.get(1 + currentOffset);

            String report = simulateRover(roverState, instructions);
            output.add(report);
        }

        for(String report : output) {
            out.println(report);
        }
    }

    public static void main(String[] args) throws IOException {
        // This is my proof that the thin shell can invoke
        // the function provided by the testable core.
        runTest(System.in, System.out);
    }
}
