/**
 * Copyright Vast 2018. All Rights Reserved.
 * <p/>
 * http://www.vast.com
 */
package com.vocumsineratio.kata.mars;

import java.util.EnumMap;
import java.util.List;

/**
 * @author Danil Suits (danil@vast.com)
 */
class Domain {
    static class Squad implements API.Squad {
        List<Rover> squad;

        Squad(List<Rover> squad) {
            this.squad = squad;
        }

        @Override
        public void run() {
            for (Rover rover : squad) {

                Position roverPosition = rover.position;
                Program instructions = rover.instructions;

                while (instructions.hasCurrent()) {

                    final String currentInstruction = instructions.current();

                    final InstructionCode code = InstructionCode.valueOf(currentInstruction);

                    // PROCESS INSTRUCTIONS
                    {
                        if ("L".equals(code.name())) {
                            roverPosition.left();
                        }

                        if ("R".equals(code.name())) {
                            roverPosition.right();
                        }

                        if ("M".equals(code.name())) {
                            roverPosition.move();
                        }
                    }

                    instructions.next();
                }
            }
        }
    }

    enum InstructionCode {
        L, M, R
    }
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

    static final EnumMap<CompassPoint, int[]> MOVE = new EnumMap<>(CompassPoint.class);

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

        void left() {
            heading = LEFT.get(heading);
        }

        void right() {
            heading = RIGHT.get(heading);
        }

        void move() {
            int[] moves = MOVE.get(heading);
            location.x += moves[0];
            location.y += moves[1];
        }
    }

    static class Rover {
        Position position;
        Program instructions;

        Rover(Position position, Program instructions) {
            this.position = position;
            this.instructions = instructions;
        }
    }

    static class Program {
        String remainingInstructions;

        Program(String remainingInstructions) {
            this.remainingInstructions = remainingInstructions;
        }

        boolean hasCurrent() {
            return !remainingInstructions.isEmpty();
        }

        String current() {
            return remainingInstructions.substring(FIRST_INSTRUCTION_OFFSET, NEXT_INSTRUCTION_OFFSET);
        }

        void next() {
            remainingInstructions = remainingInstructions.substring(NEXT_INSTRUCTION_OFFSET);
        }

        static final int FIRST_INSTRUCTION_OFFSET = 0;
        static final int NEXT_INSTRUCTION_OFFSET = 1;
    }
}
