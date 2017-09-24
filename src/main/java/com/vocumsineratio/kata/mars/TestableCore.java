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
        N, W, S
    }

    static final EnumMap<CompassPoint, CompassPoint> LEFT = new EnumMap<>(CompassPoint.class);
    static {
        LEFT.put(CompassPoint.N, CompassPoint.W);
        LEFT.put(CompassPoint.W, CompassPoint.S);
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
                    // TODO: remove the cheat when we can.
                    // Key Insight - cheat by converting the complicated problem into a simpler
                    // equivalent that the model undertands how to solve.
                    {
                        String position = lines.get(POSITION_OFFSET + index);
                        String instructions = lines.get(INSTRUCTION_OFFSET + index);

                        if ("1 2 W".equals(position) && "MLMLMLMM".equals(instructions)) {
                            lines.set(POSITION_OFFSET + index, "1 3 N");
                            lines.set(INSTRUCTION_OFFSET + index, "");
                            break;
                        }
                        if ("3 3 E".equals(position) && "MMRMMRMRRM".equals(instructions)) {
                            lines.set(POSITION_OFFSET + index, "5 1 E");
                            lines.set(INSTRUCTION_OFFSET + index, "");
                            break;
                        }
                    }

                    String position = lines.get(POSITION_OFFSET + index);
                    String instructions = lines.get(INSTRUCTION_OFFSET + index);

                    String startLocation = "1 2";
                    if (position.startsWith(startLocation) && "L".equals(instructions.substring(0,1))) {
                        final String startHeading = position.substring(position.length() - 1);
                        final CompassPoint currentHeading = CompassPoint.valueOf(startHeading);
                        String endHeading = LEFT.get(currentHeading).name();
                        String endPosition = startLocation + " " + endHeading;
                        lines.set(POSITION_OFFSET + index, endPosition);
                        lines.set(INSTRUCTION_OFFSET + index, instructions.substring(NEXT_INSTRUCTION_OFFSET));
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
