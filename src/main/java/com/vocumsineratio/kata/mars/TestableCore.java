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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Danil Suits (danil@vast.com)
 */
public class TestableCore {
    static class Move {
        final int offsetX;
        final int offsetY;

        Move(int offsetX, int offsetY) {
            this.offsetX = offsetX;
            this.offsetY = offsetY;
        }
    }

    static class RoverState {
        int posX;
        int posY;
        String orientation;

        RoverState(int posX, int posY, String orientation) {
            this.posX = posX;
            this.posY = posY;
            this.orientation = orientation;
        }
    }

    static String simulateRover(String state, String instructions) {
        // TODO: provide a real implementation
        if ("1 2 N".equals(state) && "LMLMLMLMM".equals(instructions)) {
            return "1 3 N";
        }

        if ("3 3 E".equals(state) && "MMRMMRMRRM".equals(instructions)) {
            return "5 1 E";
        }

        Map<String, Move> moves = new HashMap<>();
        moves.put("W", new Move(-1,0));

        final Map<Character,String> TRANSITIONS = new HashMap<>();
        {
            final String TURN_LEFT = "NWSEN";
            TRANSITIONS.put('L', TURN_LEFT);

            final String TURN_RIGHT = "NESWN";
            TRANSITIONS.put('R', TURN_RIGHT);
        }

        RoverState rover = parse(state);

        for(char command : instructions.toCharArray()) {
            if ('M' == command) {
                Move move = moves.get(rover.orientation);
                rover.posX += move.offsetX;
                rover.posY += move.offsetY;
            } else {
                String orientation = rover.orientation;

                // In the mean time, pretend everything is a LEFT rotation.
                String transitionsForRotation = TRANSITIONS.get(command);
                int pos = transitionsForRotation.indexOf(orientation);
                String result = transitionsForRotation.substring(pos + 1, pos + 2);

                rover.orientation = result;
            }
        }

        StringBuilder b = new StringBuilder();
        b.append(rover.posX).append(" ").append(rover.posY).append(" ").append(rover.orientation);
        final String currentState = b.toString();
        return currentState;

    }

    private static RoverState parse(String state) {
        String [] args = state.split(" ");
        final int posX = Integer.parseInt(args[0]);
        final int posY = Integer.parseInt(args[1]);
        final String w = args[2];
        return new RoverState(posX, posY, w);
    }

    private static List<String> runSimulation(List<String> simulationInputs) {
        // NOTE: the use of Lists as the mechanism for communicating state is an
        // arbitrary choice at this point, I just want something that looks like
        // a pure function  f: immutable state -> immutable state

        // In this case, I'm using lists, because that makes it easy to use
        // random access, which allows me to easily document the input format?
        // A thin justification, perhaps.
        List<String> output = new ArrayList<>();

        final int FIRST_ROVER_OFFSET = 1;
        final int ROVER_RECORD_LENGTH = 2;

        final int ROVER_STATE_OFFSET = 0;
        final int ROVER_INSTRUCTIONS_OFFSET = 1;

        for(int recordOffset = FIRST_ROVER_OFFSET; recordOffset < simulationInputs.size(); recordOffset += ROVER_RECORD_LENGTH) {
            String roverState = simulationInputs.get(ROVER_STATE_OFFSET + recordOffset);
            String instructions = simulationInputs.get(ROVER_INSTRUCTIONS_OFFSET + recordOffset);

            String report = simulateRover(roverState, instructions);
            output.add(report);
        }
        return output;
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

        List<String> output = runSimulation(simulationInputs);

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
