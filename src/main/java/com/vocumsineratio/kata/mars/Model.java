/**
 * Copyright Vast 2018. All Rights Reserved.
 * <p/>
 * http://www.vast.com
 */
package com.vocumsineratio.kata.mars;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Danil Suits (danil@vast.com)
 */
class Model {
    static Output runSimulation(Input input) {
        SimulationDefinition simulationDefinition = Builder.buildSimulation(input);

        List<RoverState> simulationResults = runSimulation(simulationDefinition);

        final Output output = prepareReport(simulationResults);
        return output;
    }

    private static Output prepareReport(List<RoverState> simulationResults) {
        List<Output.Rover> rovers = new ArrayList<>(simulationResults.size());
        for (RoverState state : simulationResults) {
            Output.Heading heading = Output.Heading.valueOf(state.orientation);
            Output.Coordinate coordinate = new Output.Coordinate(state.posX, state.posY);
            Output.Rover rover = new Output.Rover(coordinate, heading);
            rovers.add(rover);

        }
        return new Output(rovers);
    }

    private static List<RoverState> runSimulation(SimulationDefinition simulationDefinition) {
        GridDefinition gridDefinition = simulationDefinition.grid;
        ArrayGrid grid = ArrayGrid.from(gridDefinition.maxRight, gridDefinition.maxUp);

        for (RoverDefinition roverDefinition : simulationDefinition.rovers) {
            RoverState state = roverDefinition.state;
            grid.roverArrived(state.posX, state.posY);
        }

        List<RoverState> simulationResults = new ArrayList<>();
        for (RoverDefinition roverDefinition : simulationDefinition.rovers) {
            RoverState currentRover = roverDefinition.state;
            grid.roverLeft(currentRover.posX, currentRover.posY);
            for (Instruction currentInstruction : roverDefinition.instructions) {
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
        private final boolean[][] positions;

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
            boolean[][] positions = new boolean[1 + maxRight][1 + maxUp];
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

    interface Heading<T extends Heading<T>> {
        T left();
        T right();
    }

    enum SimpleHeading implements Heading<SimpleHeading> {
        N {
            @Override
            public SimpleHeading left() {
                return W;
            }

            @Override
            public SimpleHeading right() {
                return E;
            }
        },
        E {
            @Override
            public SimpleHeading left() {
                return N;
            }

            @Override
            public SimpleHeading right() {
                return S;
            }
        },
        W {
            @Override
            public SimpleHeading left() {
                return S;
            }

            @Override
            public SimpleHeading right() {
                return N;
            }
        },
        S {
            @Override
            public SimpleHeading left() {
                return E;
            }

            @Override
            public SimpleHeading right() {
                return W;
            }
        };

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
                    Heading heading = SimpleHeading.valueOf(orientation);

                    return new RoverState(currentState.posX, currentState.posY, heading.left().toString());

                }
            });

            instructionSet.put(Input.Instruction.R, new Instruction() {
                @Override
                public RoverState applyTo(RoverState currentState) {
                    String orientation = currentState.orientation;
                    Heading heading = SimpleHeading.valueOf(orientation);

                    return new RoverState(currentState.posX, currentState.posY, heading.right().toString());
                }
            });

            List<Instruction> program = new ArrayList<>();
            for (Input.Instruction instruction : instructions) {
                Instruction currentInstruction = instructionSet.get(instruction);
                program.add(currentInstruction);
            }
            return program;
        }

        private static SimulationDefinition buildSimulation(Input input) {
            List<RoverDefinition> rovers = new ArrayList<>();
            for (Input.Rover currentRover : input.rovers) {
                RoverDefinition roverDefinition = Builder.buildRover(currentRover);
                rovers.add(roverDefinition);
            }

            GridDefinition grid = new GridDefinition(input.plateau.upperRight.X, input.plateau.upperRight.Y);
            return new SimulationDefinition(grid, rovers);
        }
    }

}
