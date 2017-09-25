/**
 * Copyright Vast 2018. All Rights Reserved.
 * <p/>
 * http://www.vast.com
 */
package com.vocumsineratio.kata.mars;

import javax.xml.transform.stream.StreamSource;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author Danil Suits (danil@vast.com)
 */
public class TestableCore {
    enum CompassPoint {
        N, W, S, E
    }

    static final EnumMap<CompassPoint, CompassPoint> LEFT = new EnumMap<>(CompassPoint.class);
    static {
        LEFT.put(CompassPoint.N, CompassPoint.W);
        LEFT.put(CompassPoint.W, CompassPoint.S);
        LEFT.put(CompassPoint.S, CompassPoint.E);
        LEFT.put(CompassPoint.E, CompassPoint.N);
    }

    static final EnumMap<CompassPoint, CompassPoint> RIGHT = new EnumMap<>(CompassPoint.class);
    static {
        RIGHT.put(CompassPoint.E, CompassPoint.S);
        RIGHT.put(CompassPoint.S, CompassPoint.W);
        RIGHT.put(CompassPoint.W, CompassPoint.N);
        RIGHT.put(CompassPoint.N, CompassPoint.E);
    }

    static void runTest(InputStream in, PrintStream out) throws IOException {
        {
            // FOR TEST CALIBRATION ONLY
            if (false) return;
        }

        // Stream Parsing.
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        List<String> lines = reader.lines().collect(Collectors.toList());

        int FIRST_ROVER_OFFSET = 1;
        int INPUT_LINES_PER_ROVER = 2;

        // RUN the model.
        {
            int POSITION_OFFSET = 0;
            int INSTRUCTION_OFFSET = 1;

            int NEXT_INSTRUCTION_OFFSET = 1;

            for (int index = FIRST_ROVER_OFFSET; index < lines.size(); index += INPUT_LINES_PER_ROVER) {

                while (! lines.get(INSTRUCTION_OFFSET + index).isEmpty()) {

                    String position = lines.get(POSITION_OFFSET + index);
                    String instructions = lines.get(INSTRUCTION_OFFSET + index);

                    // TODO: real parsing
                    final String currentLocation = position.substring(0, position.length()-2);
                    String[] rawCoordinates = currentLocation.split(" ");
                    int xPos = Integer.parseInt(rawCoordinates[0]);
                    int yPos = Integer.parseInt(rawCoordinates[1]);
                    final String startHeading = position.substring(position.length() - 1);
                    CompassPoint currentHeading = CompassPoint.valueOf(startHeading);

                    // TODO: real parsing
                    final String currentInstruction = instructions.substring(0, 1);
                    final String remainingInstructions = instructions.substring(NEXT_INSTRUCTION_OFFSET);
                    lines.set(INSTRUCTION_OFFSET + index, remainingInstructions);

                    if ("L".equals(currentInstruction)) {
                        currentHeading = LEFT.get(currentHeading);
                        String endLocation = xPos + " " + yPos;
                        lines.set(POSITION_OFFSET + index, endLocation + " " + currentHeading.name());
                    }

                    if ("R".equals(currentInstruction)) {
                        currentHeading = RIGHT.get(currentHeading);
                        String endLocation = xPos + " " + yPos;
                        lines.set(POSITION_OFFSET + index, endLocation + " " + currentHeading.name());
                    }

                    if ("M".equals(currentInstruction)) {

                        if (CompassPoint.W.equals(currentHeading)) {
                            xPos -= 1;
                        }

                        if (CompassPoint.E.equals(currentHeading)) {
                            xPos += 1;
                        }

                        if (CompassPoint.S.equals(currentHeading)) {
                            yPos -= 1;
                        }

                        if (CompassPoint.N.equals(currentHeading)) {
                            yPos += 1;
                        }

                        String endLocation = xPos + " " + yPos;
                        lines.set(POSITION_OFFSET + index, endLocation + " " + currentHeading.name());
                    }
                }
            }

        }

        IntStream.range(0, lines.size())
                .filter( n -> (n % 2) == 1)
                .mapToObj(n -> lines.get(n))
                .forEach(out::println);
    }

    public static void main(String[] args) throws IOException {
        // This is my proof that the thin shell can invoke
        // the function provided by the testable core.
        runTest(System.in, System.out);
    }
}
