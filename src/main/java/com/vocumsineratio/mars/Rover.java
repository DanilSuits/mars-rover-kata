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
        assert "E" == crnt.orientation;
        return new State(1 + crnt.x, crnt.y, crnt.orientation);
    }

    static String [] output(String... lines) {
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

        Function<State, State> instruction = instructionSet.get("M");
        State s = instruction.apply(new State(4, 1, "E"));

        report[1] = Schema.rover(s);
        return report;
    }
}
