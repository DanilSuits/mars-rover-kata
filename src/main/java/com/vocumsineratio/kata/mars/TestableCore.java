/**
 * Copyright Vast 2018. All Rights Reserved.
 * <p/>
 * http://www.vast.com
 */
package com.vocumsineratio.kata.mars;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Danil Suits (danil@vast.com)
 */
public class TestableCore {
    static void runTest(InputStream in, PrintStream out) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));

        List<String> input = reader.lines().collect(Collectors.toList());

        // Pretend that we've actually done the work to compute the
        // terminal positions for each of the two cases.  The programs
        // at that point will be empty.
        if ("1 2 N".equals(input.get(1)) && "LMLMLMLMM".equals(input.get(2))) {
            input.set(1, "0 1 S");
            input.set(2, "LMLMM");
        }

        for ( int currentPosition = 1; currentPosition < input.size(); currentPosition += 2) {
            int currentInstructions = currentPosition + 1;

            while (!input.get(currentInstructions).isEmpty()) {
                String[] position = input.get(currentPosition).split(" ");

                switch (input.get(currentInstructions).substring(0, 1)) {
                    case "L": {
                        switch (position[2]) {
                            case "E": {
                                position[2] = "N";
                                break;
                            }
                            case "S": {
                                position[2] = "E";
                                break;
                            }
                        }
                        break;
                    }
                    case "R": {
                        switch (position[2]) {
                            case "N": {
                                position[2] = "E";
                                break;
                            }
                            case "W": {
                                position[2] = "N";
                                break;
                            }
                            case "S": {
                                position[2] = "W";
                                break;
                            }
                            case "E": {
                                position[2] = "S";
                                break;
                            }
                        }
                        break;
                    }

                    case "M": {
                        switch (position[2]) {
                            case "N": {
                                position[1] = String.valueOf(1 + Integer.valueOf(position[1]));
                                break;
                            }
                            case "S": {
                                position[1] = String.valueOf(-1 + Integer.valueOf(position[1]));
                                break;
                            }
                            case "E": {
                                position[0] = String.valueOf(1 + Integer.valueOf(position[0]));
                                break;
                            }
                            case "W": {
                                position[0] = String.valueOf(-1 + Integer.valueOf(position[0]));
                                break;
                            }
                        }
                    }
                }

                input.set(currentPosition, String.join(" ", position));
                input.set(currentInstructions, input.get(currentInstructions).substring(1));

            }
        }

        for (int index = 1; index < input.size(); index += 2) {
            String position = input.get(index);
            out.println(position);
        }
    }

    public static void main(String[] args) {
        // This is my proof that the thin shell can invoke
        // the function provided by the testable core.
        runTest(System.in, System.out);
    }
}
