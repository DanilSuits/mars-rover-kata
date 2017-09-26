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

                    final InstructionCode code = instructions.current();

                    // PROCESS INSTRUCTIONS
                    {
                        if (InstructionCode.L.equals(code)) {
                            roverPosition.left();
                        }

                        if (InstructionCode.R.equals(code)) {
                            roverPosition.right();
                        }

                        if (InstructionCode.M.equals(code)) {
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

    static class Move {
        final int [] offsets;

        Move(int... offsets) {
            this.offsets = offsets;
        }

        void applyTo(Location location) {
            location.x += offsets[0];
            location.y += offsets[1];
        }
    }

    static final EnumMap<CompassPoint, Move> MOVE = new EnumMap<>(CompassPoint.class);

    static {
        MOVE.put(CompassPoint.N, new Move(0, 1));
        MOVE.put(CompassPoint.S, new Move(0, -1));
        MOVE.put(CompassPoint.E, new Move(1, 0));
        MOVE.put(CompassPoint.W, new Move(-1, 0));
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
            MOVE.get(heading).applyTo(location);
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

        InstructionCode current() {
            String name = remainingInstructions.substring(FIRST_INSTRUCTION_OFFSET, NEXT_INSTRUCTION_OFFSET);
            return InstructionCode.valueOf(name);
        }

        void next() {
            remainingInstructions = remainingInstructions.substring(NEXT_INSTRUCTION_OFFSET);
        }

        static final int FIRST_INSTRUCTION_OFFSET = 0;
        static final int NEXT_INSTRUCTION_OFFSET = 1;
    }
}
