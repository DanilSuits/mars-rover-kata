package com.vocumsineratio.mars;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * @author Danil Suits (danil@vast.com)
 */
class Rover {
    static class Schema {
        static String rover(int x, int y, String orientation) {
            return String.format("%d %d %s", x, y, orientation);
        }

        static String rover(State s) {
            return rover(s.x, s.y, s.orientation);
        }
    }

    static class State {
        int x;
        int y;
        String orientation;

        State(int x, int y, String orientation) {
            this.x = x;
            this.y = y;
            this.orientation = orientation;
        }
    }

    static State move(State crnt) {
        HashMap<String, Function<State, State>> moves = new HashMap<>();
        moves.put("W", Rover::west);
        moves.put("E", Rover::east);
        moves.put("S", Rover::south);
        moves.put("N", Rover::north);

        Function<State, State> move = moves.get(crnt.orientation);
        return move.apply(crnt);
    }

    static State west(State crnt) {
        return new State(crnt.x - 1, crnt.y, crnt.orientation);
    }

    static State east(State crnt) {
        return new State(crnt.x + 1, crnt.y, crnt.orientation);
    }

    static State south(State crnt) {
        return new State(crnt.x, crnt.y - 1, crnt.orientation);
    }

    static State north(State crnt) {
        return new State(1, 3, "N");
    }

    static State right(State crnt) {
        HashMap<String, String> rightTurns = new HashMap<>();
        rightTurns.put("W", "N");
        rightTurns.put("N", "E");
        rightTurns.put("S", "W");
        rightTurns.put("E", "S");

        assert rightTurns.containsKey(crnt.orientation);

        return new State(
                crnt.x,
                crnt.y,
                rightTurns.get(
                        crnt.orientation
                ));
    }
    
    private static State left(State crnt) {
        HashMap<String, String> leftTurns = new HashMap<>();
        leftTurns.put("E", "N");
        leftTurns.put("S", "E");
        leftTurns.put("W", "S");
        leftTurns.put("N", "W");

        return new State(
                crnt.x,
                crnt.y,
                leftTurns.get(
                        crnt.orientation
                ));
    }

    static Map<String, Function<State, State>> instructionSet = new HashMap<>();

    static String[] output(String... lines) {
        assert lines[0].equals("5 5");
        assert lines[1].equals("1 2 N");
        assert lines[2].equals("LMLMLMLMM");
        assert lines[3].equals("3 3 E");
        assert lines[4].equals("MMRMMRMRRM");


        int plateauLength = 1;
        int roverLength = 2;
        int roverCount = (lines.length - plateauLength) / roverLength;
        String[] report = new String[roverCount];

        instructionSet.put("M", Rover::move);
        instructionSet.put("R", Rover::right);
        instructionSet.put("L", Rover::left);

        final int positionOffset = 0;
        final int instructionsOffset = 1;

        State [] rovers = new State[roverCount];
        String [] roverInstructions = new String[roverCount];

        for (int roverId = 0; roverId < roverCount; ++roverId) {
            int roverOffset = plateauLength + roverLength * roverId;
            String positionDescription = lines[roverOffset + positionOffset];
            String [] roverArgs = positionDescription.split(" ");

            rovers[roverId] = new State(
                    Integer.parseInt(roverArgs[0]),
                    Integer.parseInt(roverArgs[1]),
                    roverArgs[2]
            );

            roverInstructions[roverId] = lines[roverOffset + instructionsOffset];
        }

        for (int roverId = 0; roverId < roverCount; ++roverId) {
            String [] crntInstructions = roverInstructions[roverId].split("");
            State crntState = rovers[roverId];

            String currentReport = report(crntInstructions, crntState, instructionSet);
            report[roverId] = currentReport;
        }
        return report;
    }

    private static String report(String[] crntInstructions, State crntState, Map<String, Function<State, State>> instructionSet) {
        for (String currentInstruction : crntInstructions) {
            Function<State, State> instruction = instructionSet.get(currentInstruction);
            crntState = instruction.apply(crntState);
        }

        return Schema.rover(crntState);
    }
}
