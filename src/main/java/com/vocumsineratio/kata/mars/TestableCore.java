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
import java.util.function.Function;
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

    static final EnumMap<CompassPoint, int []> MOVE = new EnumMap<>(CompassPoint.class);
    static {
        MOVE.put(CompassPoint.N, new int[]{0, 1});
        MOVE.put(CompassPoint.S, new int[]{0, -1});
        MOVE.put(CompassPoint.E, new int[]{1, 0});
        MOVE.put(CompassPoint.W, new int[]{-1, 0});
    }

    static class Location {
        int x;
        int y;

        public Location(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    static class Position {
        Location location;
        CompassPoint heading;

        Position(Location location, CompassPoint heading) {
            this.location = location;
            this.heading = heading;
        }
    }

    static class Rover {
        Position position;
        String remainingInstructions;

        Rover(Position position, String remainingInstructions) {
            this.position = position;
            this.remainingInstructions = remainingInstructions;
        }
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

        List<Rover> squad = new ArrayList<>();
        
        int POSITION_OFFSET = 0;
        int INSTRUCTION_OFFSET = 1;
        {
            Function<String, Position> parsePosition = line -> {
                final String currentLocation = line.substring(0, line.length()-2);
                String[] rawCoordinates = currentLocation.split(" ");

                int xPos = Integer.parseInt(rawCoordinates[0]);
                int yPos = Integer.parseInt(rawCoordinates[1]);
                Location roverLocation = new Location(xPos, yPos);

                final String startHeading = line.substring(line.length() - 1);
                CompassPoint currentHeading = CompassPoint.valueOf(startHeading);

                return new Position(roverLocation, currentHeading);
            };

            for (int index = FIRST_ROVER_OFFSET; index < lines.size(); index += INPUT_LINES_PER_ROVER) {
                String position = lines.get(POSITION_OFFSET + index);
                Rover rover = new Rover(parsePosition.apply(position), lines.get(INSTRUCTION_OFFSET + index));
                squad.add(rover);
            }
        }

        // RUN the model.
        {
            int FIRST_INSTRUCTION_OFFSET = 0;
            int NEXT_INSTRUCTION_OFFSET = 1;

            for (Rover rover : squad) {

                Position roverPosition = rover.position;

                while (! rover.remainingInstructions.isEmpty()) {

                    // TODO: real parsing
                    final String currentInstruction = rover.remainingInstructions.substring(FIRST_INSTRUCTION_OFFSET, NEXT_INSTRUCTION_OFFSET);
                    rover.remainingInstructions = rover.remainingInstructions.substring(NEXT_INSTRUCTION_OFFSET);

                    // PROCESS INSTRUCTIONS
                    {
                        if ("L".equals(currentInstruction)) {
                            roverPosition.heading = LEFT.get(roverPosition.heading);
                        }

                        if ("R".equals(currentInstruction)) {
                            roverPosition.heading = RIGHT.get(roverPosition.heading);
                        }

                        if ("M".equals(currentInstruction)) {
                            int[] moves = MOVE.get(roverPosition.heading);
                            roverPosition.location.x += moves[0];
                            roverPosition.location.y += moves[1];
                        }
                    }
                }
            }
        }

        for (int n = 0; n < squad.size(); ++n) {
            Rover rover = squad.get(n);
            Position roverPosition = rover.position;
            int index = FIRST_ROVER_OFFSET + 2 * n;
            // UPDATE STATE
            {
                lines.set(POSITION_OFFSET + index, (roverPosition.location.x + " " + roverPosition.location.y) + " " + roverPosition.heading.name());
                lines.set(INSTRUCTION_OFFSET + index, rover.remainingInstructions);
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
