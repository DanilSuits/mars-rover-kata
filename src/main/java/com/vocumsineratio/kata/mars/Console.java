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
import java.util.List;

/**
 * @author Danil Suits (danil@vast.com)
 */
class Console {
    static Input readFrom(InputStream in) throws IOException {
        List<String> simulationInputs = new ArrayList<>();
        {
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));

            String currentLine;
            while ((currentLine = reader.readLine()) != null) {
                simulationInputs.add(currentLine);
            }
        }

        // NOTE: the use of Lists as the mechanism for communicating state is an
        // arbitrary choice at this point, I just want something that looks like
        // a pure function  f: immutable state -> immutable state

        // In this case, I'm using lists, because that makes it easy to use
        // random access, which allows me to easily document the input format?
        // A thin justification, perhaps.

        return Parser.parseInput(simulationInputs);
    }

    static void writeTo(PrintStream out, Output output) {
        List<String> lines = Formatter.format(output);

        for (String report : lines) {
            out.println(report);
        }
    }

    static class Formatter {
        private static List<String> format(Output output) {
            List<String> lines = new ArrayList<>();
            for (Output.Rover rover : output.rovers) {
                String report = format(rover);
                lines.add(report);
            }
            return lines;
        }

        private static String format(Output.Rover rover) {
            final Output.Coordinate coordinate = rover.coordinate;
            final Output.Heading heading = rover.heading;

            return format(coordinate, heading);
        }

        private static String format(Output.Coordinate coordinate, Output.Heading heading) {
            return format(
                    coordinate.X,
                    coordinate.Y,
                    heading.name()
            );
        }

        private static String format(int posX, int posY, String heading) {
            StringBuilder b = new StringBuilder();
            b.append(posX).append(" ").append(posY).append(" ").append(heading);
            return b.toString();
        }
    }

    private static class Parser {
        private static Input.Plateau parsePlateau(String grid) {
            String[] args = grid.split(" ");
            final int maxRight = Integer.parseInt(args[0]);
            final int maxUp = Integer.parseInt(args[1]);

            return createPlateau(
                    createCoordinate(
                            maxRight,
                            maxUp
                    )
            );
        }

        private static Input.Coordinate createCoordinate(int maxRight, int maxUp) {
            return new Input.Coordinate(maxRight, maxUp);
        }

        private static Input.Plateau createPlateau(Input.Coordinate upperRight) {
            return new Input.Plateau(upperRight);
        }

        private static Input.Rover parseRover(String roverInput, String instructions) {
            return createRover(
                    Parser.parseRoverPosition(roverInput),
                    Parser.parseInstructions(instructions)
            );
        }

        private static Input.Rover createRover(Input.Position position, List<Input.Instruction> instructions1) {
            return new Input.Rover(position, instructions1);
        }

        private static Input.Position parseRoverPosition(String state) {
            String[] args = state.split(" ");
            final int posX = Integer.parseInt(args[0]);
            final int posY = Integer.parseInt(args[1]);
            final String heading = args[2];

            return createPosition(posX, posY, heading);
        }

        private static Input.Position createPosition(int posX, int posY, String heading) {

            return createPosition(
                    createCoordinate(posX, posY)
                    , createHeading(heading)
            );
        }

        private static Input.Position createPosition(Input.Coordinate coordinate, Input.Heading heading) {
            return new Input.Position(coordinate, heading);
        }

        private static Input.Heading createHeading(String w) {
            return Input.Heading.valueOf(w);
        }

        private static List<Input.Instruction> parseInstructions(String currentLine) {
            List<Input.Instruction> instructions = new ArrayList<>(currentLine.length());
            for (int index = 0; index < currentLine.length(); ++index) {

                final String instructionCode = currentLine.substring(index, 1 + index);
                instructions.add(createInstruction(instructionCode));
            }
            return instructions;
        }

        private static Input.Instruction createInstruction(String instructionCode) {
            return Input.Instruction.valueOf(instructionCode);
        }

        private static Input parseInput(List<String> simulationInputs) {
            final int FIRST_ROVER_OFFSET = 1;
            final int ROVER_RECORD_LENGTH = 2;

            final int ROVER_STATE_OFFSET = 0;
            final int ROVER_INSTRUCTIONS_OFFSET = 1;

            String plateauInputs = simulationInputs.get(0);
            final Input.Plateau plateau = Parser.parsePlateau(plateauInputs);

            List<Input.Rover> inputRovers = new ArrayList<>();
            for (int recordOffset = FIRST_ROVER_OFFSET; recordOffset < simulationInputs.size(); recordOffset += ROVER_RECORD_LENGTH) {
                String roverInput = simulationInputs.get(ROVER_STATE_OFFSET + recordOffset);
                String instructions = simulationInputs.get(ROVER_INSTRUCTIONS_OFFSET + recordOffset);

                final Input.Rover rover = Parser.parseRover(roverInput, instructions);
                inputRovers.add(rover);
            }

            return new Input(plateau, inputRovers);
        }
    }

}
