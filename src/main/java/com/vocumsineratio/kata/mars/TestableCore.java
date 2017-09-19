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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Danil Suits (danil@vast.com)
 */
public class TestableCore {
    static String[] simulateCollision(String[] simulation) {
        return runSimulation(simulation);
    }

    static String[] runSimulation(String[] simulation) {
        List<String> simulationInputs = Arrays.asList(simulation);
        List<String> report = runSimulation(simulationInputs);
        final String [] template = new String[0];
        return report.toArray(template);
    }
    
    static String simulateRover(String state, String instructions) {
        String [] simulation = {"5 5", state, instructions};
        String [] report = runSimulation(simulation);
        return report[0];
    }

    private static List<String> runSimulation(List<String> simulationInputs) {
        // NOTE: the use of Lists as the mechanism for communicating state is an
        // arbitrary choice at this point, I just want something that looks like
        // a pure function  f: immutable state -> immutable state

        // In this case, I'm using lists, because that makes it easy to use
        // random access, which allows me to easily document the input format?
        // A thin justification, perhaps.

        Input input = Parser.parseInput(simulationInputs);

        SimulationDefinition simulationDefinition = Builder.buildSimulation(input);

        List<RoverState> simulationResults = runSimulation(simulationDefinition);

        List<String> output = toResult(simulationResults);

        return output;
    }

    private static List<String> toResult(List<RoverState> simulationResults) {
        List<String> output = new ArrayList<>();
        for(RoverState finalState : simulationResults) {
            String report = toResult(finalState);
            output.add(report);
        }
        return output;
    }

    private static String toResult(RoverState rover) {
        StringBuilder b = new StringBuilder();
        b.append(rover.posX).append(" ").append(rover.posY).append(" ").append(rover.orientation);
        return b.toString();
    }


    private static List<RoverState> runSimulation(SimulationDefinition simulationDefinition) {
        GridDefinition gridDefinition = simulationDefinition.grid;
        ArrayGrid grid = ArrayGrid.from(gridDefinition.maxRight, gridDefinition.maxUp);

        for(RoverDefinition roverDefinition : simulationDefinition.rovers) {
            RoverState state = roverDefinition.state;
            grid.roverArrived(state.posX,state.posY);
        }

        List<RoverState> simulationResults = new ArrayList<>();
        for(RoverDefinition roverDefinition : simulationDefinition.rovers) {
            RoverState currentRover = roverDefinition.state;
            grid.roverLeft(currentRover.posX, currentRover.posY);
            for(Instruction currentInstruction : roverDefinition.instructions) {
                RoverState roverAfterInstruction = currentInstruction.applyTo(currentRover);

                if (grid.isOccupied(roverAfterInstruction.posX, roverAfterInstruction.posY)) {
                    break;
                }
                currentRover = roverAfterInstruction;
            }
            RoverState finalState = currentRover;
            grid.roverArrived(finalState.posX, finalState.posY);
            simulationResults.add(finalState);
        }
        return simulationResults;
    }


    static class ArrayGrid {
        private final boolean [][] positions;

        ArrayGrid(boolean[][] positions) {
            this.positions = positions;
        }

        void roverArrived(int posX, int posY) {
            positions[posX][posY] = true;
        }

        void roverLeft(int posX, int posY) {
            positions[posX][posY] = false;
        }

        boolean isOccupied(int posX, int posY) {
            return positions[posX][posY];
        }

        static ArrayGrid from(int maxRight, int maxUp) {
            boolean [][] positions = new boolean[1 + maxRight][1 + maxUp];
            return new ArrayGrid(positions);
        }
    }

    static class Move {
        final int offsetX;
        final int offsetY;

        Move(int offsetX, int offsetY) {
            this.offsetX = offsetX;
            this.offsetY = offsetY;
        }
    }

    static class RoverState {
        final int posX;
        final int posY;
        final String orientation;

        RoverState(int posX, int posY, String orientation) {
            this.posX = posX;
            this.posY = posY;
            this.orientation = orientation;
        }
    }

    interface Instruction {
        RoverState applyTo(RoverState currentState);
    }

    static class RoverDefinition {
        public final RoverState state;
        public final List<Instruction> instructions;

        RoverDefinition(RoverState state, List<Instruction> instructions) {
            this.state = state;
            this.instructions = instructions;
        }
    }

    static class GridDefinition {
        public final int maxRight;
        public final int maxUp;

        public GridDefinition(int maxRight, int maxUp) {
            this.maxRight = maxRight;
            this.maxUp = maxUp;
        }
    }

    static class SimulationDefinition {
        public final GridDefinition grid;
        public final List<RoverDefinition> rovers;

        public SimulationDefinition(GridDefinition grid, List<RoverDefinition> rovers) {
            this.grid = grid;
            this.rovers = rovers;
        }
    }

    static class Input {
        public final Plateau plateau;
        public final List<Rover> rovers;

        Input(Plateau plateau, List<Rover> rovers) {
            this.plateau = plateau;
            this.rovers = rovers;
        }

        enum Heading {
            N, E, W, S
        }

        static class Coordinate {
            public final int X;
            public final int Y;

            Coordinate(int x, int y) {
                X = x;
                Y = y;
            }
        }

        static class Plateau {
            public final Coordinate upperRight;
            public final Coordinate lowerLeft = new Coordinate(0,0);

            Plateau(Coordinate upperRight) {
                this.upperRight = upperRight;
            }
        }

        enum Instruction {
            M, L, R
        }

        static class Position {
            public final Coordinate coordinate;
            public final Heading heading;

            Position(Coordinate coordinate, Heading heading) {
                this.coordinate = coordinate;
                this.heading = heading;
            }
        }

        static class Rover {
            public final Position position;
            public final List<Instruction> instructions;

            Rover(Position position, List<Instruction> instructions) {
                this.position = position;
                this.instructions = instructions;
            }
        }
    }

    private static class Parser {
        private static Input.Plateau parsePlateau(String grid) {
            String [] args = grid.split(" ");
            final int maxRight = Integer.parseInt(args[0]);
            final int maxUp = Integer.parseInt(args[1]);

            Input.Coordinate upperRight = new Input.Coordinate(maxRight, maxUp);
            Input.Plateau plateau = new Input.Plateau(upperRight);

            return plateau;
        }

        private static Input.Rover parseRover(String roverInput, String instructions) {
            Input.Position position = Parser.parseRoverPosition(roverInput);
            final List<Input.Instruction> instructions1 = Parser.parseInstructions(instructions);
            return new Input.Rover(position, instructions1);
        }

        private static Input.Position parseRoverPosition(String state) {
            String [] args = state.split(" ");
            final int posX = Integer.parseInt(args[0]);
            final int posY = Integer.parseInt(args[1]);
            final String w = args[2];
            Input.Coordinate coordinate = new Input.Coordinate(posX, posY);
            Input.Heading heading = Input.Heading.valueOf(args[2]);

            return new Input.Position(coordinate, heading);
        }

        private static List<Input.Instruction> parseInstructions(String currentLine) {
            List<Input.Instruction> instructions = new ArrayList<>(currentLine.length());
            for (int index = 0; index < currentLine.length(); ++index) {

                instructions.add(Input.Instruction.valueOf(currentLine.substring(index, 1 + index)));
            }
            return instructions;
        }

        private static Input parseInput(List<String> simulationInputs) {
            final int FIRST_ROVER_OFFSET = 1;
            final int ROVER_RECORD_LENGTH = 2;

            final int ROVER_STATE_OFFSET = 0;
            final int ROVER_INSTRUCTIONS_OFFSET = 1;

            String plateauInputs = simulationInputs.get(0);
            final Input.Plateau plateau = Parser.parsePlateau(plateauInputs);

            List<Input.Rover> inputRovers = new ArrayList<>();
            for(int recordOffset = FIRST_ROVER_OFFSET; recordOffset < simulationInputs.size(); recordOffset += ROVER_RECORD_LENGTH) {
                String roverInput = simulationInputs.get(ROVER_STATE_OFFSET + recordOffset);
                String instructions = simulationInputs.get(ROVER_INSTRUCTIONS_OFFSET + recordOffset);

                final Input.Rover rover = Parser.parseRover(roverInput, instructions);
                inputRovers.add(rover);
            }

            return new Input(plateau, inputRovers);
        }
    }

    static class Builder {
        private static RoverState buildRoverState(Input.Position position) {
            return new RoverState(position.coordinate.X, position.coordinate.Y, position.heading.name());
        }

        private static RoverDefinition buildRover(Input.Rover rover) {
            RoverState roverState = buildRoverState(rover.position);
            List<Instruction> program = buildProgram(rover.instructions);

            return new RoverDefinition(roverState, program);
        }

        private static List<Instruction> buildProgram(List<Input.Instruction> instructions) {

            Map<Input.Instruction, Instruction> instructionSet = new HashMap<>();
            instructionSet.put(Input.Instruction.M, new Instruction() {
                @Override
                public RoverState applyTo(RoverState currentState) {

                    Map<String, Move> moves = new HashMap<>();
                    {
                        moves.put("W", new Move(-1, 0));
                        moves.put("E", new Move(1, 0));
                        moves.put("N", new Move(0, 1));
                        moves.put("S", new Move(0, -1));
                    }

                    Move move = moves.get(currentState.orientation);
                    int posX = currentState.posX + move.offsetX;
                    int posY = currentState.posY + move.offsetY;

                    return new RoverState(posX, posY, currentState.orientation);
                }
            });

            final String TURN_LEFT = "NWSEN";
            final String TURN_RIGHT = new StringBuilder(TURN_LEFT).reverse().toString();

            instructionSet.put(Input.Instruction.L, new Instruction() {
                @Override
                public RoverState applyTo(RoverState currentState) {
                    String orientation = currentState.orientation;


                    int pos = TURN_LEFT.indexOf(orientation);
                    String result = TURN_LEFT.substring(pos + 1, pos + 2);

                    return new RoverState(currentState.posX, currentState.posY, result);

                }
            });

            instructionSet.put(Input.Instruction.R, new Instruction() {
                @Override
                public RoverState applyTo(RoverState currentState) {
                    String orientation = currentState.orientation;

                    int pos = TURN_RIGHT.indexOf(orientation);
                    String result = TURN_RIGHT.substring(pos + 1, pos + 2);

                    return new RoverState(currentState.posX, currentState.posY, result);
                }
            }) ;

            List<Instruction> program = new ArrayList<>();
            for(Input.Instruction instruction : instructions) {
                Instruction currentInstruction = instructionSet.get(instruction);
                program.add(currentInstruction);
            }
            return program;
        }

        private static SimulationDefinition buildSimulation(Input input) {
            List<RoverDefinition> rovers = new ArrayList<>();
            for(Input.Rover currentRover : input.rovers) {
                RoverDefinition roverDefinition = Builder.buildRover(currentRover);
                rovers.add(roverDefinition);
            }

            GridDefinition grid = new GridDefinition(input.plateau.upperRight.X, input.plateau.upperRight.Y);
            return new SimulationDefinition(grid, rovers);
        }
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
