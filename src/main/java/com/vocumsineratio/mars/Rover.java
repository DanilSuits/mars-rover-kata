package com.vocumsineratio.mars;

import java.util.Collections;
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

        Function<State, State> move = moves.get(crnt.orientation);
        return move.apply(crnt);
    }

    static State west(State crnt) {
        return new State(crnt.x - 1, crnt.y, crnt.orientation);
    }

    static State east(State crnt) {
        return new State(crnt.x + 1, crnt.y, crnt.orientation);
    }

    static State right(State crnt) {
        HashMap<String, String> rightTurns = new HashMap<>();
        rightTurns.put("W", "N");
        rightTurns.put("N", "E");
        rightTurns.put("S", "W");
        
        assert rightTurns.containsKey(crnt.orientation);

        return new State(
                crnt.x,
                crnt.y,
                rightTurns.get(
                        crnt.orientation
                ));
    }

    static String[] output(String... lines) {
        assert lines[0].equals("5 5");
        assert lines[1].equals("1 2 N");
        assert lines[2].equals("LMLMLMLMM");
        assert lines[3].equals("3 3 E");
        assert lines[4].equals("MMRMMRMRRM");


        int roverCount = (lines.length - 1) / 2;
        String[] report = new String[roverCount];

        report[0] = Schema.rover(1, 3, "N");

        Map<String, Function<State, State>> instructionSet = new HashMap<>();
        instructionSet.put("M", Rover::move);
        instructionSet.put("R", Rover::right);

        State crntState = new State(5, 1, "S");
        {
            crntState = right(crntState);
        }
        {
            String roverInstructions = lines[4].substring(6);
            {
                for (int instructionPointer = 0; instructionPointer < roverInstructions.length(); ++instructionPointer) {
                    String currentInstruction = roverInstructions.substring(instructionPointer, 1 + instructionPointer);
                    Function<State, State> instruction = instructionSet.get(currentInstruction);
                    crntState = instruction.apply(crntState);
                }
            }
        }

        report[1] = Schema.rover(crntState);
        return report;
    }
}
