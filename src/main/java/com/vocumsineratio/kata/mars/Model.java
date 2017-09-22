/**
 * Copyright Vast 2018. All Rights Reserved.
 * <p/>
 * http://www.vast.com
 */
package com.vocumsineratio.kata.mars;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Danil Suits (danil@vast.com)
 */
class Model {
    static Output runSimulation(Input input) {
        SimulationDefinition simulationDefinition = Builder.buildSimulation(input);

        SimulationDefinition simulationResults = runSimulation(simulationDefinition);

        final Output output = prepareReport(projectResults(simulationResults));
        return output;
    }

    private static Output prepareReport(List<RoverState> simulationResults) {
        List<Output.Rover> rovers = new ArrayList<>(simulationResults.size());
        for (RoverState state : simulationResults) {
            SimpleHeading source = state.orientation;

            // TODO: EnumMap?
            Output.Heading heading = Output.Heading.valueOf(source.name());
            Output.Coordinate coordinate = new Output.Coordinate(state.posX, state.posY);
            Output.Rover rover = new Output.Rover(coordinate, heading);
            rovers.add(rover);

        }
        return new Output(rovers);
    }

    private static SimulationDefinition runSimulation(SimulationDefinition simulationDefinition) {
        GridDefinition gridDefinition = simulationDefinition.grid;
        Domain.Plateau<RoverState> grid = ArrayGrid.from(gridDefinition.maxRight, gridDefinition.maxUp);

        for (RoverDefinition roverDefinition : simulationDefinition.rovers) {
            RoverState state = roverDefinition.state;
            grid.roverArrived(state);
        }

        SimulationDefinition simulationResult = new SimulationDefinition(simulationDefinition.grid, new ArrayList<>());
        for (RoverDefinition roverDefinition : simulationDefinition.rovers) {
            RoverState currentRover = roverDefinition.state;
            grid.roverLeft(currentRover);
            for (Instruction<RoverState> currentInstruction : roverDefinition.instructions) {
                RoverState roverAfterInstruction = currentInstruction.applyTo(currentRover);

                if (grid.isOccupied(roverAfterInstruction)) {
                    break;
                }
                currentRover = roverAfterInstruction;
            }
            RoverState finalState = currentRover;
            grid.roverArrived(finalState);
            simulationResult.rovers.add(new RoverDefinition(finalState, Collections.EMPTY_LIST));
        }

        return simulationResult;
    }

    private static List<RoverState> projectResults(SimulationDefinition simulationResult) {
        List<RoverState> out = new ArrayList<>();
        for(RoverDefinition rover : simulationResult.rovers) {
            out.add(rover.state);
        }
        return out;
    }

    static class Domain {
        interface Heading<Heading extends Domain.Heading> {
            Heading left();

            Heading right();
        }

        interface Rover<Rover extends Domain.Rover> {
            Rover left();
            Rover right();

            Rover move();
        }

        interface Plateau<Rover extends Domain.Rover> {
            void roverArrived(Rover rover);
            void roverLeft(Rover rover);

            boolean isOccupied(Rover rover);
        }
    }


    static class ArrayGrid implements Domain.Plateau<RoverState>{

        private final boolean[][] positions;

        ArrayGrid(boolean[][] positions) {
            this.positions = positions;
        }

        private void roverArrived(int posX, int posY) {
            positions[posX][posY] = true;
        }

        private void roverLeft(int posX, int posY) {
            positions[posX][posY] = false;
        }

        private boolean isOccupied(int posX, int posY) {
            return positions[posX][posY];
        }

        static ArrayGrid from(int maxRight, int maxUp) {
            boolean[][] positions = new boolean[1 + maxRight][1 + maxUp];
            return new ArrayGrid(positions);
        }

        @Override
        public void roverArrived(RoverState rover) {
            roverArrived(rover.posX, rover.posY);
        }

        @Override
        public void roverLeft(RoverState rover) {
            roverLeft(rover.posX, rover.posY);
        }

        @Override
        public boolean isOccupied(RoverState rover) {
            return isOccupied(rover.posX, rover.posY);
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

    enum SimpleHeading implements Domain.Heading<SimpleHeading> {
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

    static class RoverState implements Domain.Rover<RoverState> {
        final int posX;
        final int posY;
        final SimpleHeading orientation;

        RoverState(int posX, int posY, SimpleHeading orientation) {
            this.posX = posX;
            this.posY = posY;
            this.orientation = orientation;
        }

        @Override
        public RoverState left() {
            return new RoverState(posX, posY, orientation.left());
        }

        @Override
        public RoverState right() {
            return new RoverState(posX, posY, orientation.right());
        }

        @Override
        public RoverState move() {
            final Map<SimpleHeading, Move> moves = new EnumMap<>(SimpleHeading.class);
            {
                moves.put(SimpleHeading.W, new Move(-1, 0));
                moves.put(SimpleHeading.E, new Move(1, 0));
                moves.put(SimpleHeading.N, new Move(0, 1));
                moves.put(SimpleHeading.S, new Move(0, -1));
            }

            Move move = moves.get(orientation);
            return new RoverState(posX + move.offsetX, posY + move.offsetY, orientation);
        }
    }

    interface Instruction<Rover extends Domain.Rover<Rover>> {
        Rover applyTo(Rover currentState);
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
            // TODO: this is probably an enum map
            SimpleHeading heading = SimpleHeading.valueOf(position.heading.name());
            return new RoverState(position.coordinate.X, position.coordinate.Y, heading);
        }

        private static RoverDefinition buildRover(Input.Rover rover) {
            RoverState roverState = buildRoverState(rover.position);
            List<Instruction> program = buildProgram(rover.instructions);

            return new RoverDefinition(roverState, program);
        }

        private static List<Instruction> buildProgram(List<Input.Instruction> instructions) {


            Map<Input.Instruction, Instruction> instructionSet = new HashMap<>();
            
            instructionSet.put(Input.Instruction.M, rover -> rover.move());
            instructionSet.put(Input.Instruction.L, rover -> rover.left());
            instructionSet.put(Input.Instruction.R, rover -> rover.right());

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
